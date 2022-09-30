(ns protosens.maestro.plugin.quickdoc

  "Maestro plugin generating markdown documentation for modules using [Quickdoc](https://github.com/borkdude/quickdoc)

   Works only with Babashka.

   Attention, it is necessary adding the `clj-kondo` to your `bb.edn` file as a [Babashka pod](https://github.com/babashka/pods):

   ```clojure
   {:pods
    {clj-kondo/clj-kondo {:version \"2022.09.08\"}}}
   ```"

  (:require [babashka.fs             :as bb.fs]
            [clojure.edn             :as edn]
            [protosens.maestro       :as $.maestro]
            [protosens.maestro.alias :as $.maestro.alias]
            [quickdoc.api            :as quickdoc]))


;;;;;;;;;; Private


(defn- -quickdoc-option+

  ;;

  [option+ basis]

  (merge (basis :maestro.plugin.quickdoc/option+)
         option+))


;;;;;;;;;; Tasks


(defn bundle

  "Generates a single documentation file for the given aliases.

   All `:extra-paths` of those aliases will be merged and used as source paths.

   For options, see the Quickdoc documentation.
  
   Prints paths that have been bundled together."
  

  ([]

   (bundle nil))


  ([option+]
   
   (bundle option+
           nil))


  ([option+ alias+]

   (let [basis ($.maestro/ensure-basis option+)
         path+ (sort ($.maestro.alias/extra-path+ basis
                                                  (or alias+
                                                      (edn/read-string (first *command-line-args*)))))]
     (quickdoc/quickdoc (-> option+
                           (-quickdoc-option+ basis)
                           (assoc :source-paths
                                  path+)))
     (run! println
           path+))))



(defn module+

  "Generates documentation for modules automatically.

   Selects modules that have an `:maestro.plugin.quickdoc.path/output` in their alias data specifying
   where the markdown file should be written to. Source paths are based on `:extra-paths`.

   For options, see the Quickdoc documentation.
   
   Prints which modules have produced documentation where."

  ([]

   (module+ nil))


  ([option+]

   (let [basis     ($.maestro/ensure-basis option+)
         option-2+ (-quickdoc-option+ option+
                                      basis)]
     (doseq [[alias
              path-output
              path-source+] (keep (fn [[alias data]]
                                    (when-some [path (:maestro.plugin.quickdoc.path/output data)]
                                      [alias
                                       path
                                       (or (not-empty (data :extra-paths))
                                           (throw (Exception. (str "Missing extra paths in alias data: "
                                                                   alias))))]))
                                  (-> basis
                                      (:aliases)
                                      (sort)))]
       (let [dir (bb.fs/parent path-output)]
         (when-not (bb.fs/exists? dir)
           (bb.fs/create-dirs dir)))
       (quickdoc/quickdoc (assoc option-2+
                                 :outfile      path-output
                                 :source-paths path-source+))
       (println (format "%s -> %s"
                        alias
                        path-output))))))
