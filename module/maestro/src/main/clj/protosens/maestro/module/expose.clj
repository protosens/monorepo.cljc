(ns protosens.maestro.module.expose

  "Exposing modules to be consumed publicly.
 
   Modules containing a `:maestro.module.expose/name` in their alias data can be exposed publicly as
   [Git dependencies](https://clojure.org/guides/deps_and_cli#_using_git_libraries) and consumed from
   [Clojure CLI](https://clojure.org/guides/deps_and_cli). Naturally, users must rely on `:deps/root`
   to point to individual modules.

   Modules meant for exposition must have a `:maestro.module.expose/name`. A name is a symbol
   `<organization>/<artifact>` such as `com.acme/some-lib`.

   The [[deploy]] task does the necessary step for exposition."

  (:require [babashka.fs        :as bb.fs]
            [clojure.java.io    :as C.java.io]
            [clojure.pprint     :as C.pprint]
            [clojure.string     :as C.string]
            [protosens.edn.read :as $.edn.read]
            [protosens.git      :as $.git]
            [protosens.maestro  :as $.maestro]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;; Private helpers


(defn- -read-deps-edn

  []

  ($.edn.read/file "deps.maestro.edn"))


;;; Generating `deps.edn` files


(defn ^:no-doc -prepare-deps-edn

  ;; Computes the content of the `deps.edn` file for a module.
  ;;
  ;; Returns data for feedack.

  [deps-edn git-sha exposed]

  (let [definition       (get-in deps-edn
                                 [:aliases
                                  exposed])
        root-dir         (or (definition :maestro/root)
                             ($.maestro/fail (format "Missing root directory for module `%s`."
                                                     exposed)))
        path+            (mapv (fn [path]
                                 ;;
                                 ;; TODO. Should do proper resolution for accuracy.
                                 (when-not (C.string/starts-with? path
                                                                root-dir)
                                   ($.maestro/fail (format "Module `%s` has an extra path outside of its `:maestro/root`: `%s`"
                                                           exposed
                                                           path)))
                                 (str (bb.fs/relativize root-dir
                                                        path)))
                               (definition :extra-paths))
        deps-edn-exposed (-> ($.maestro/-run (str exposed)
                                             deps-edn)
                             (::$.maestro/result))
        child+           (into []
                               (keep (fn [[dep-alias dep-definition]]
                                       (when-not (= dep-alias
                                                    exposed)
                                         (when-some [dep-root-dir (:maestro/root dep-definition)]
                                           (if-some [exposed-name (:maestro.module.expose/name dep-definition)]
                                             [dep-alias
                                              exposed-name
                                              dep-root-dir]
                                             ($.maestro/fail (format "Module `%s` is required by module `%s` but is not exposed."
                                                                     exposed
                                                                     dep-alias)))))))
                               (deps-edn-exposed :aliases))
        url              (deps-edn-exposed :maestro.module.expose/url)]
    {::file    {:deps  (into (or (deps-edn-exposed :deps)
                                 {})
                             (map (fn [[_dep-alias exposed-name dep-root-dir]]
                                    [exposed-name
                                     {:deps/root dep-root-dir
                                      :git/sha   git-sha
                                      :git/url   url}]))
                             child+)
                :paths path+}
     ::path    (str root-dir
                    "/deps.edn")
     ::require (map first
                    child+)}))



(defn- -write-deps-edn-exposed

  [path deps-edn-exposed]

  (let [parent (bb.fs/parent path)]
    (when-not (bb.fs/exists? parent)
      (bb.fs/create-dirs parent)))
  (with-open [writer (C.java.io/writer path)]
    (binding [*out* writer]
      (println ";; This is a file generated by Maestro for allowing this module to be consumed")
      (println ";; by external users with Clojure CLI.")
      (println ";;")
      (println ";; It is accessible as a Git dependency with `:deps/root` pointing to this directory.")
      (println ";;")
      (C.pprint/pprint deps-edn-exposed)))
  path)


;;;;;;;;;; Public helpers


(defn exposed?

  "Returns true if an alias (given its data) is meant to be exposed as a Git library."

  
  ([definition]

   (:maestro.module.expose/name definition))


  ([deps-edn alias]

   (exposed? (get-in deps-edn
                     [:aliases
                      alias]))))


;;;;;;;;;; Exposition


(defn ^:no-doc -expose

  ;; Exposition step.
  ;;
  ;; Repeated twice in [[run]] which also pretty-prints feedback data returned by this function.

  [git-sha deps-edn]

  (let [exposed+ (into []
                       (keep (fn [alias]
                               (when (exposed? deps-edn
                                               alias)
                                 alias)))
                       (keys (deps-edn :aliases)))
        write    (or (deps-edn :maestro.module.expose/write)
                     -write-deps-edn-exposed)]
    (into (sorted-map)
          (map (fn [exposed]
                 (let [deps-edn-exposed (-prepare-deps-edn deps-edn
                                                           git-sha
                                                           exposed)]
                   (write (deps-edn-exposed ::path)
                          (deps-edn-exposed ::file))
                   [exposed
                    deps-edn-exposed])))
          exposed+)))



(defn run

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

   (run nil))


  ([deps-edn]

   ;;
   ;; Ensure repository is clean.
   (when-not ($.git/clean?)
     ($.maestro/fail "Repository must be sparkling clean, no modified or untracked files"))
   (let [deps-edn-2 (or deps-edn
                        (-read-deps-edn))]
     (when-not (deps-edn-2 :maestro.module.expose/url)
       ($.maestro/fail "Missing Git URL"))
     ;;
     ;; Prepare exposition.
     (let [git-sha ($.git/commit-sha 0)]
       (println "Prepare module exposition")
       (-expose git-sha
                deps-edn-2)
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
                                   deps-edn-2)]
         (println (format "    %s -> %s"
                          alias
                          (feedback ::path)))
         (doseq [alias-child (sort (feedback ::require))]
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



(defn run-local

  "Local exposition for testing purporses.

   Exactly like [[deploy]] but sets the repository URL to the current directory.

   Which must be the root directory of the repository.

   For instance, it allows testing exposition and running the [[verify]] task without having to push anything."


  ([]

   (run-local nil))


  ([deps-edn]

   (run (-> (or deps-edn
                (-read-deps-edn))
            (assoc :maestro.module.expose/url
                   (System/getProperty "user.dir"))))))
