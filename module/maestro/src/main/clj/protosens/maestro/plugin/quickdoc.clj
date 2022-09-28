(ns protosens.maestro.plugin.quickdoc

  "Maestro plugin generating markdown documentation for modules using [Quickdoc](https://github.com/borkdude/quickdoc)

   Works only with Babashka."

  (:require [babashka.fs             :as bb.fs]
            [clojure.edn             :as edn]
            [protosens.maestro       :as $.maestro]
            [protosens.maestro.alias :as $.maestro.alias]
            [quickdoc.api            :as quickdoc]))


;;;;;;;;;; Tasks


(defn bundle

  "Generates a single documentation file for the given aliases.

   All `:extra-paths` of those aliases will be merged and used as source paths.

   For options, see the Quickdoc documentation."
  

  ([option+]
   
   (bundle option+
           nil))


  ([option+ alias+]

   (quickdoc/quickdoc (assoc option+
                             :source-paths
                             ($.maestro.alias/extra-path+ ($.maestro/ensure-basis option+)
                                                          (or alias+
                                                              (edn/read-string (first *command-line-args*))))))))




(defn module+

  "Generates documentation for modules automatically.

   Selects modules that have an `:maestro.plugin.quickdoc.path/output` in their alias data specifying
   where the markdown file should be written to. Source paths are based on `:extra-paths`.

   For options, see the Quickdoc documentation."

  [option+]

  (doseq [[path-output
           path-source+] (keep (fn [[alias data]]
                                 (when-some [path (:maestro.plugin.quickdoc.path/output data)]
                                   [path
                                    (or (not-empty (data :extra-paths))
                                        (throw (Exception. (str "Missing extra paths in alias data: "
                                                                alias))))]))
                               (-> ($.maestro/ensure-basis option+)
                                   (:aliases)))]
    (let [dir (bb.fs/parent path-output)]
      (when-not (bb.fs/exists? dir)
        (bb.fs/create-dirs dir)))
    (quickdoc/quickdoc (assoc option+
                              :outfile      path-output
                              :source-paths path-source+))))
