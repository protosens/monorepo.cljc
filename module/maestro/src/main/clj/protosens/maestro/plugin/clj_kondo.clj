(ns protosens.maestro.plugin.clj-kondo

  "Maestro plugin for linting Clojure code via Clj-kondo.
  
   Assumes it is already and `clj-kondo` is available in the shell."

  (:refer-clojure :exclude [import])
  (:require [protosens.maestro           :as $.maestro]
            [protosens.maestro.classpath :as $.maestro.classpath]
            [protosens.maestro.util      :as $.maestro.util]))


;;;;;;;;;;


(defn prepare

  "Prepares the Clj-kondo cache by linting all dependencies and copying configuration files.
  
   Should be called prior to [[lint]]ing for the first time and on dependency updates."

  []

  (let [cp (-> ($.maestro/create-basis)
               (:aliases)
               (keys)
               ($.maestro.classpath/compute))]
    (@$.maestro.util/d*shell "clj-kondo --parallel --copy-configs --lint" cp "--dependencies")))



(defn lint

  "Lints the whole repository by extracting `:extra-paths` from aliases.

   Options may be:

   | Key            | Value                                                       |
   |----------------|-------------------------------------------------------------|
   | `:path-filter` | Predicate function deciding whether a path should be linted |"
        

  ([]

   (lint nil))


  ([option+]

   (apply @$.maestro.util/d*shell
          "clj-kondo --parallel --lint"
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
                      (vals (:aliases ($.maestro/create-basis)))))))))
