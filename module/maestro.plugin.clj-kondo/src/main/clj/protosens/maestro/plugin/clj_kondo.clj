(ns protosens.maestro.plugin.clj-kondo

  "Maestro plugin for linting Clojure code via [Clj-kondo](https://github.com/clj-kondo/clj-kondo).
  
   Assumes `clj-kondo` is installed and available in the shell.

   Those tasks only work when executed with [Babashka](https://github.com/babashka/babashka)."

  (:refer-clojure :exclude [import])
  (:require [babashka.process    :as bb.process]
            [protosens.maestro   :as $.maestro]
            [protosens.classpath :as $.classpath]))


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
    (-> (bb.process/shell "clj-kondo" "--parallel" "--copy-configs" "--lint" cp "--dependencies")
        (:exit)
        (zero?))))



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

   (-> (apply bb.process/shell
              (concat ["clj-kondo" "--parallel" "--lint"]
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
       (:exit)
       (zero?))))
