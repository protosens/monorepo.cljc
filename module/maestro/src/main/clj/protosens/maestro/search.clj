(ns protosens.maestro.search

  (:require [protosens.graph.dfs :as $.graph.dfs]
            [protosens.maestro   :as-alias $.maestro]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;;


(defn conj-path

  [state node]

  (update state
          ::$.maestro/path
          conj
          [node
           ($.graph.dfs/depth state)]))



(defn deeper

  [state node node+]
  
  (-> state
      (conj-path node)
      ($.graph.dfs/deeper node+)))



(defn input?

  [state node]

  (contains? (state ::$.maestro/input)
             node))
