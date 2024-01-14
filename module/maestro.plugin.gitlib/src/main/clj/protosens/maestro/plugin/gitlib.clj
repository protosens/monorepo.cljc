(ns protosens.maestro.plugin.gitlib

  "Exposing modules to be consumed publicly.
 
   Modules containing a `:maestro.plugin.gitlib/name` in their alias data can be exposed publicly as
   [Git dependencies](https://clojure.org/guides/deps_and_cli#_using_git_libraries) and consumed from
   [Clojure CLI](https://clojure.org/guides/deps_and_cli). Naturally, users must rely on `:deps/root`
   to point to individual modules.

   Modules meant for exposition must have a `:maestro.plugin.gitlib/name`. A name is a symbol
   `<organization>/<artifact>` such as `com.acme/some-lib`.

   The [[deploy]] task does the necessary step for exposition."

  (:require [babashka.fs              :as bb.fs]
            [clojure.java.io          :as C.java.io]
            [clojure.pprint           :as C.pprint]
            [clojure.string           :as C.string]
            [protosens.deps.edn       :as $.deps.edn]
            [protosens.git            :as $.git]
            [protosens.maestro        :as $.maestro]
            [protosens.maestro.alias  :as $.maestro.alias]
            [protosens.maestro.plugin :as $.maestro.plugin]
            [protosens.path           :as $.path]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;; Miscellaneous helpers


(defn exposed?

  "Returns true if an alias (given its data) is meant to be exposed as a Git library."

  
  ([definition]

   (:maestro.plugin.gitlib/name definition))


  ([deps-edn alias]

   (exposed? (get-in deps-edn
                     [:aliases
                      alias]))))


;;;;;;;;;; Creating a `deps.edn` file for an exposed alias


(defn flat-deps-edn

  [{:as   state
    dep+  ::dep+
    path+ ::path+}]

  (assoc state
         ::deps.edn
         {:deps  dep+
          :paths path+}))



(defn dep+

  [{:as             state
    alias-exposed   ::alias
    alias-required+ ::require
    definition      ::definition
    deps-edn-master ::$.maestro/deps.edn
    sha             ::sha
    url             ::url}]

  (let [alias->definition
        (deps-edn-master :aliases)
        ,
        merge-extra-dep+
        (fn [extra-dep+ alias-required]
          (let [definition    (alias->definition alias-required)
                extra-dep-2+  (merge extra-dep+
                                     (:extra-deps definition))
                root-required (:maestro/root definition)]
            (if root-required
              ;;
              ;; Alias is an internal dependency which must be exposed as well.
              (if-some [name-exposed (definition :maestro.plugin.gitlib/name)]
                (assoc extra-dep-2+
                       name-exposed
                       {:deps/root (-> root-required
                                       ($.path/from-string)
                                       ($.path/normalized)
                                       (str))
                        :git/sha   sha
                        :git/url   url})
                ($.maestro.plugin/fail (format "Alias `%s` is required by alias `%s` but is not exposed."
                                               alias-required
                                               alias-exposed)))
              ;;
              ;; Alias is external, we only need its extra deps.
              extra-dep-2+)))]
    (assoc state
           ::dep+
           (reduce merge-extra-dep+
                   (definition :extra-deps)
                   alias-required+))))



(defn required

  [{:as             state
    alias-exposed   ::alias
    deps-edn-master ::$.maestro/deps.edn}]

  (let [alias-required+ (-> ($.maestro/run [alias-exposed]
                                           deps-edn-master)
                            ($.maestro.alias/accepted))]
    (assoc state
           ::require
           (filter #(not= %
                          alias-exposed)
                   alias-required+))))



(defn path+

  [{:as           state
    alias-exposed ::alias
    definition    ::definition
    root          ::root}]

  (let [extra-path+ (definition :extra-paths)
        relativize  (fn [path]
                      (let [path-2 (-> path
                                       ($.path/from-string)
                                       ($.path/normalized))]
                        (when-not ($.path/starts-with? path-2
                                                       root)
                          ($.maestro.plugin/fail (format "Module `%s` has an extra path outside of its `:maestro/root`: `%s`"
                                                         alias-exposed
                                                         path)))
                        (-> ($.path/relative root
                                             path-2)
                            (str))))]
    (assoc state
           ::path+
           (mapv relativize
                 extra-path+))))



(defn root

  [{:as           state
    alias-exposed ::alias
    definition    ::definition}]

  (or (when-some [root (definition :maestro/root)]
        (assoc state
               ::root
               (-> root
                   ($.path/from-string)
                   ($.path/normalized))))
      ($.maestro.plugin/fail (format "Missing root directory for module `%s`."
                                     alias-exposed))))



(defn prepare

  [{:as             state
    alias-exposed   ::alias
    deps-edn-master ::$.maestro/deps.edn}]

  (-> state
      (assoc ::definition
             (get-in deps-edn-master
                     [:aliases
                      alias-exposed]))
      (root)
      (path+)
      (required)
      (dep+)
      (flat-deps-edn)))



(defn write-prepared

  [{:as      state
    deps-edn ::deps.edn
    root     ::root}]

  (let [path (-> (str root
                      "/deps.edn")
                 ($.path/from-string)
                 ($.path/normalized)
                 (str))]
    (with-open [file (C.java.io/writer path)]
      (binding [*out* file]
        (println ";; This file was generated by Maestro.")
        (println ";;")
        (println ";;")
        (C.pprint/pprint deps-edn)))
    (assoc state
           ::output
           path)))


;;;;;;;;;; Preparing `deps.edn` files for exposed aliases, using the above


(defn prepare+

  [{:as             state
    deps-edn-master ::$.maestro/deps.edn}]

   (let [alias->definition (deps-edn-master :aliases)
         alias+            (keys alias->definition)
         alias-exposed+    (into []
                                 (keep (fn [alias]
                                         (when (get-in alias->definition
                                                       [alias
                                                        :maestro.plugin.gitlib/name])
                                           alias)))
                                 alias+)]
     (assoc state
            ::prepared+
            (mapv (fn [alias-exposed]
                    (-> state
                        (assoc ::alias
                               alias-exposed)
                        (prepare)))
                  alias-exposed+))))



(defn write-prepared+

  [{:as       state
    prepared+ ::prepared+}]

  (assoc state
         ::prepared+
         (mapv write-prepared
               prepared+)))



(defn expose+

  [state]

  (-> state
      (prepare+)
      (write-prepared+)))


;;;;;;;;;; Task


(defn commit

  [state message]

  (let [state-2 (-> state
                    (assoc ::sha
                           ($.git/commit-sha 0))
                    (expose+))]
    ($.git/add ["."])
    ($.git/commit message)
    state-2))



(defn run

  "Task exposing selected modules for consumption by Clojure CLI as Git dependencies.

   High-level steps are:

   - Ensure Git tree is absolutely clean
   - Select modules with a `:maestro.plugin.gitlib/name` in their alias data
   - In their `:maestro/root`, generate a `deps.edn` file
   - Dependencies on other modules are Git dependencies with the SHA of the previous commit
   - Commit
   - Repeat once

   This produces 2 commits and the SHA of the last commit is what users can rely on when pushed.
   
   Either `proto-basis` or the top `deps.edn` file must contain `:maestro.plugin.gitlib/url` pointing
   to the URL of the repo.

   For testing purposes, one can point to the absolute path of the repository. For production
   purposes, always use the public URL of the repository.
  
   **Note**: the `release` profile is activated automatically when resolving `:maestro/require` for each
   module."

  [config]

  ($.maestro.plugin/intro "maestro.plugin.gitlib/expose")
  ($.maestro.plugin/safe
    (delay
      ($.maestro.plugin/step "Aliases with `:maestro.plugin.gitlib/name` will be exposed as gitlibs")
      (when-not ($.git/clean?)
        ($.maestro.plugin/fail "Repository must be sparkling clean, no modified or untracked files"))
      (when-not (config ::url)
        ($.maestro.plugin/fail "Missing Git URL in given configuration"))
      (let [state (assoc config
                         ::$.maestro/deps.edn
                         ($.maestro.plugin/read-deps-edn))]
        ;;
        ;; First commit for preparation.
        ($.maestro.plugin/step "First commit for preparation")
        (commit state
                "Prepare module exposition as gitlibs (DO NOT USE)")
        ;;
        ;; Expose and print feedback for all modules.
        ($.maestro.plugin/step "Exposing modules as gitlibs")
        ($.maestro.plugin/step "Second commit for actual exposition")
        ($.maestro.plugin/step "Custom `deps.edn` files created for:")
        (let [state-2 (commit state
                              "Expose public modules as gitlibs")]
          (doseq [{alias-exposed ::alias
                   path-deps-edn ::output} (state-2 ::prepared+)]
            ($.maestro.plugin/step 1
                                   (format "%s  ->  %s"
                                           alias-exposed
                                           path-deps-edn)))
          ($.maestro.plugin/done (format "After pushing, users can point to commit `%s`"
                                         ($.git/commit-sha 0)))
          state-2)))))
