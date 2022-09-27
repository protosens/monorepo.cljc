(ns protosens.maestro.plugin.quickdoc

  "Maestro plugin generating markdown documentation for modules using [Quickdoc](https://github.com/borkdude/quickdoc)

   Works only with Babashka."

  (:require [babashka.fs             :as bb.fs]
            [protosens.maestro       :as $.maestro]
            [quickdoc.api            :as quickdoc]))


;;;;;;;;;;


(defn task

  "Generates documentation for all modules.

   Alias data for the module must contain `:extra-paths`, those will be the source
   provided for analysis. To activate Quickdoc, it must also contain `:maestro.plugin.quickdoc.path/output`
   specifying the output path for the generated markdown."


  ([]

   (task nil))


  ([option+]

   (let [basis ($.maestro/ensure-basis option+)]
     (doseq [[path-output
              path-source+] (keep (fn [[alias data]]
                                    (when-some [path (:maestro.plugin.quickdoc.path/output data)]
                                      [path
                                       (or (not-empty (data :extra-paths))
                                           (throw (Exception. (str "Missing extra paths in alias data: "
                                                                   alias))))]))
                                  (basis :aliases))]
       (let [dir (bb.fs/parent path-output)]
         (when-not (bb.fs/exists? dir)
           (bb.fs/create-dirs dir)))
       (quickdoc/quickdoc (assoc option+
                                 :outfile      path-output
                                 :source-paths path-source+))))))
