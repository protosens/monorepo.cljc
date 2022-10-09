(ns protosens.maestro.module.expose

  "Exposing modules to be consumed publicly.
 
   Modules containing a `:maestro.module.expose/name` in their alias data can be exposed publicly as
   [Git dependencies](https://clojure.org/guides/deps_and_cli#_using_git_libraries) and consumed from
   [Clojure CLI](https://clojure.org/guides/deps_and_cli). Naturally, users must rely on `:deps/root`
   to point to individual modules.

   Modules meant for exposition must have a `:maestro.module.expose/name`. A name is a symbol
   `<organization>/<artifact>` such as `com.acme/some-lib`.

   The [[deploy]] task does the necessary step for exposition.
   The [[verify]] task may be used as a precaution."

  (:require [babashka.fs                       :as bb.fs]
            [clojure.java.io                   :as java.io]
            [clojure.pprint                    :as pprint]
            [clojure.string                    :as string]
            [protosens.git                     :as $.git]
            [protosens.maestro                 :as $.maestro]
            [protosens.maestro.module.requirer :as $.maestro.module.requirer]))


(declare exposed?)


(set! *warn-on-reflection*
      true)


;;;;;;;;;; Generating `deps.edn` files


(defn- -prepare-deps-edn

  ;; Computes the content of the `deps.edn` file for a module.
  ;;
  ;; Returns data for feedack.

  [basis git-sha alias]

  (let [alias->data (basis :aliases)
        data        (alias->data alias)
        root-dir    (data :maestro/root)
        _           (when-not root-dir
                      ($.maestro/fail (str "Missing root directory for alias: "
                                           alias)))
        required    (-> basis
                        (assoc :maestro/alias+
                               [alias])
                        ($.maestro/search)
                        (:maestro/require))
        deps-edn    (reduce (fn [deps-edn alias-required]
                              (let [data-required (alias->data alias-required)
                                    deps-edn-2    (update deps-edn
                                                          :deps
                                                          merge
                                                          (data-required :extra-deps))]
                                (if (and (not= alias-required
                                               alias)
                                         (exposed? data-required))
                                  (assoc-in deps-edn-2
                                            [:deps
                                             (data-required :maestro.module.expose/name)]
                                            {:deps/root (data-required :maestro/root)
                                             :git/sha   git-sha
                                             :git/url   (basis :maestro.module.expose/url)})
                                  (update deps-edn-2
                                          :paths
                                          into
                                          (map (fn [path]
                                                 (when-not (string/starts-with? path
                                                                                root-dir)
                                                   ($.maestro/fail (format "Path to add does not belong:
                                                                           
                                                                                Exposed alias `%s` has root `%s`
                                                                                Required alias %s` has path `%s`
                                                                            
                                                                            Probably, required alias should be exposed as well.
                                                                            Does it have a `:maestro.module.expose/name` in its data?"
                                                                           alias
                                                                           root-dir
                                                                           alias-required
                                                                           path)))
                                                 (str (bb.fs/relativize root-dir
                                                                        path))))
                                          (data-required :extra-paths)))))
                            {:deps  (sorted-map)
                             :paths #{}}
                            required)]
    {:maestro/require required
     ::deps.edn       (update deps-edn
                              :paths
                              (comp vec
                                    sort))
     ::deps.edn.path  (str root-dir
                           "/deps.edn")}))



(defn- -write-deps-edn

  ;; Default way of writing a `deps-edn` file by pretty-printing it to the given `path`."

  [path deps-edn]

  (let [parent (bb.fs/parent path)]
    (when-not (bb.fs/exists? parent)
      (bb.fs/create-dirs parent)))
  (with-open [writer (java.io/writer path)]
    (binding [*out* writer]
      (println ";; This is a file generated by Maestro for allowing this module to be consumed")
      (println ";; by external users with Clojure CLI.")
      (println ";;")
      (println ";; It is accessible as a Git dependency with `:deps/root` pointing to this directory.")
      (println ";;")
      (pprint/pprint deps-edn)))
  path)


;;;;;;;;;; Exposition


(defn ^:no-doc -expose

  ;; Exposition step.
  ;;
  ;; Repeated twice in [[deploy]] which also pretty-prints feedback data returned by this function.

  [git-sha basis]

  (let [basis-2     (-> basis
                        ($.maestro/ensure-basis)
                        (update :maestro/profile+
                                #(conj (vec %)
                                       'release)))
        alias->data (basis-2 :aliases)
        basis-3     (-> basis-2
                        (assoc :maestro/alias+
                               (vec (keys alias->data)))
                        ($.maestro/search))
        gitlib+     (filterv (fn [alias]
                               (if-some [data (alias->data alias)]
                                 (exposed? data)
                                 ($.maestro/fail (str "No data for alias: "
                                                      alias))))
                             (basis-3 :maestro/require))
        write       (or (basis-2 :maestro.module.expose/write)
                        -write-deps-edn)]
    (into (sorted-map)
          (map (fn [alias]
                 (let [prepared (-prepare-deps-edn basis-2
                                                   git-sha
                                                   alias)]
                   (write (prepared ::deps.edn.path)
                          (prepared ::deps.edn))
                   [alias (dissoc prepared
                                  ::deps.edn)])))
          (sort gitlib+))))



(defn deploy

  "Task exposing selected modules for consumption by Clojure CLI as Git dependencies.

   High-level steps are:

   - Ensure Git tree is absolutely clean
   - Select modules with a `:maestro.module.expose/name` in their alias data
   - In their `:maestro/root`, generate a `deps.edn` file
   - Dependencies on other modules are Git dependencies with the SHA of the previous commit
   - Commit
   - Repeat once

   This produces 2 commits and the SHA of the last commit is what users can rely on when pushed.
   
   Either `proto-basis` or the top `deps.edn` file must contain `:maestro.module.expose/url` pointing
   to the URL of the repo.

   For testing purposes, one can point to the absolute path of the repository. For production
   purposes, always use the public URL of the repository.
  
   **Note**: the `release` profile is activated automatically when resolving `:maestro/require` for each
   module."


  ([]

   (deploy nil))


  ([proto-basis]

   ;;
   ;; Ensure repository is clean.
   (when-not ($.git/clean?)
     ($.maestro/fail "Repository must be sparkling clean, no modified or untracked files"))
   (let [basis ($.maestro/ensure-basis proto-basis)]
     (when-not (basis :maestro.module.expose/url)
       ($.maestro/fail "Missing Git URL"))
      ;;
      ;; Prepare exposition.
      (let [git-sha ($.git/commit-sha 0)]
        (println "Prepare module exposition")
        (-expose git-sha
                 basis)
        ($.git/add ["."])
        ($.git/commit (format "Prepare module exposition
                       
                               Base: %s"
                               git-sha)))
      ;;
      ;; Expose and print feedback for all modules.
      (let [git-sha-2 ($.git/commit-sha 0)]
        (println "Expose modules")
        (println)
        (doseq [[alias
                 feedback] (-expose git-sha-2
                                    basis)]
          (println (format "    %s -> %s"
                           alias
                           (feedback ::deps.edn.path)))
          (doseq [alias-child (sort (filter (fn [alias-child]
                                              (not= alias-child
                                                    alias))
                                            (feedback :maestro/require)))]
            (println "       "
                     alias-child))
          (println))
        ($.git/add ["."])
        ($.git/commit (format "Expose modules
                            
                               Pre-exposed: %s"
                              git-sha-2)))
      ;;
      ;; Done!
      (println "Users can point to commit:"
               ($.git/commit-sha 0)))))



(defn deploy-local

  "Local exposition for testing purporses.

   Exactly like [[deploy]] but sets the repository URL to the current directory.

   Which must be the root directory of the repository.

   For instance, it allows testing exposition and running the [[verify]] task without having to push anything."


  ([]

   (deploy-local nil))


  ([basis]

   (deploy (assoc basis
                  :maestro.module.expose/url
                  (System/getProperty "user.dir")))))



(defn exposed?

  "Returns true if an alias (given its data) is meant to be exposed as a Git library."

  
  ([alias-data]

   (alias-data :maestro.module.expose/name))


  ([basis alias]

   (exposed? (get-in basis
                     [:aliases
                      alias]))))


;;;;;;;;;; Verification


(defn- -req-alias-filter

  ;; Used to seled only exposed modules when interacting with [[$.maestro.module.requirer]].

  [basis]

  (let [f (fn [_alias data]
            (exposed? data))]
    (update basis
            :maestro.module.requirer/alias-filter
            #(if %
               (fn [alias data]
                 (and (f alias
                         data)
                      (% alias
                         data)))
               f))))



(defn requirer+

  "Task generating requirer namespaces for all exposed modules.
  
   See the [[protosens.maestro.module.requirer]] about requirer namespaces and
   especially [[protosens.maestro.module.requirer/generate]] about the required
   setup.

   The main benefit about generating those is being able to call the [[verify]]
   task."


  ([]

   (requirer+ nil))


  ([basis]

   ($.maestro.module.requirer/generate (-req-alias-filter basis))))





(defn verify

  "Task verifying exposed modules, checking if namespaces compile.
 
   This is done via [[protosens.maestro.module.requirer/verify]] and ensure that
   modules can be required in their production state.

   See [[requirer+]]."


  ([]

   (verify nil))


  ([basis]

   ($.maestro.module.requirer/verify (-req-alias-filter basis))))
