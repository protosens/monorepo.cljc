(ns protosens.maestro.plugin.clj-kondo

  "Maestro plugin for linting Clojure code via [Clj-kondo](https://github.com/clj-kondo/clj-kondo).
  
   Assumes `clj-kondo` is installed and available in the shell."

  (:require [protosens.maestro   :as $.maestro]
            [protosens.classpath :as $.classpath]
            [protosens.process   :as $.process]))


;;;;;;;;;;


(defn prepare

  "Prepares the Clj-kondo cache by linting all dependencies and copying configuration files.
  
   Should be called prior to [[lint]]ing for the first time and on dependency updates.
  
   Returns `true` in case of success."

  []

  (let [cp (-> ($.maestro/create-basis)
               (:aliases)
               (keys)
               ($.classpath/compute))]
    (-> ($.process/shell ["clj-kondo" "--parallel" "--copy-configs" "--lint" cp "--dependencies"])
        ($.process/success?))))



(defn lint

  "Lints the whole repository by extracting `:extra-paths` from aliases.

   Options may be:

   | Key            | Value                                                       |
   |----------------|-------------------------------------------------------------|
   | `:path-filter` | Predicate function deciding whether a path should be linted |

   Returns `true` in case of success."
        

  ([]

   (lint nil))


  ([option+]

   (-> ($.process/shell (concat ["clj-kondo" "--parallel" "--lint"]
                                (let [basis       ($.maestro/create-basis)
                                      path-filter (:path-filter option+)]
                                  (if path-filter
                                    (reduce-kv (fn [acc alias data]
                                                 (reduce (fn [acc-2 path]
                                                           (cond->
                                                             acc-2
                                                             (path-filter alias
                                                                          path)
                                                             (conj path)))
                                                         acc
                                                         (:extra-paths data)))
                                               []
                                               (basis :aliases))
                                    (mapcat :extra-paths
                                            (vals (:aliases ($.maestro/create-basis))))))))
       ($.process/success?))))
