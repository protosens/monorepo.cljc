(ns protosens.maestro.plugin.quickdoc

  "Maestro plugin generating markdown documentation for modules using [Quickdoc](https://github.com/borkdude/quickdoc)

   Works with Babashka out of the box. For Clojure JVM, add the JVM flavor of Quickdoc to your dependencies.

   Attention, it is necessary adding `clj-kondo` to your `bb.edn` file as a [Babashka pod](https://github.com/babashka/pods):

   ```clojure
   {:pods
    {clj-kondo/clj-kondo {:version \"2022.09.08\"}}}
   ```"

  (:require [babashka.fs        :as bb.fs]
            [protosens.deps.edn :as $.deps.edn]
            [protosens.edn.read :as $.edn.read]
            [protosens.maestro  :as $.maestro]
            [quickdoc.api       :as quickdoc]))


;;;;;;;;;; Tasks


(defn bundle

  "Task generating a single documentation file for the given aliases.

   All `:extra-paths` of those aliases will be merged and used as source paths.

   Quickdoc options may be provided under `:maestro.plugin.quickdoc/option+`."
  

  ([]

   (bundle nil))


  ([basis]

   (let [basis ($.maestro/ensure-basis basis)
         path+ (sort ($.deps.edn/path+ basis
                                       (or (basis :maestro.plugin.quickdoc/alias+)
                                           (some-> (first *command-line-args*)
                                                   ($.edn.read/string))
                                           (keys (basis :aliases)))))]
     (quickdoc/quickdoc (assoc (basis :maestro.plugin.quickdoc/option+)
                               :source-paths
                               path+))
     (run! println
           path+))))



(defn module+

  "Task generating documentation for modules automatically.

   Selects modules that have an `:maestro.plugin.quickdoc.path/output` in their alias data specifying
   where the markdown file should be written to. Source paths are based on `:extra-paths`.

   Quickdoc options may be provided under `:maestro.plugin.quickdoc/option+`.
   
   Prints which modules have produced documentation where."

  ([]

   (module+ nil))


  ([basis]

   (let [basis ($.maestro/ensure-basis basis)]
     (doseq [[alias
              path-output
              path-source+] (keep (fn [[alias data]]
                                    (when-some [path (:maestro.plugin.quickdoc.path/output data)]
                                      [alias
                                       path
                                       (or (not-empty (data :extra-paths))
                                           ($.maestro/fail (str "Missing extra paths in alias data: "
                                                                alias)))]))
                                  (-> basis
                                      (:aliases)
                                      (sort)))]
       (let [dir (bb.fs/parent path-output)]
         (when-not (bb.fs/exists? dir)
           (bb.fs/create-dirs dir)))
       (quickdoc/quickdoc (assoc (basis :maestro.plugin.quickdoc/option+)
                                 :outfile      path-output
                                 :source-paths path-source+))
       (println (format "%s -> %s"
                        alias
                        path-output))))))
