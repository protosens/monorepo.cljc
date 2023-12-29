(ns protosens.maestro.search

  (:require [protosens.graph.dfs :as       $.graph.dfs]
            [protosens.maestro   :as-alias $.maestro]))


(set! *warn-on-reflection*
      true)


(declare conj-path)


;;;;;;;;;;


(defn accept

  [state node]

  (-> state
      (update ::$.maestro/accepted
              conj
              node)
      (conj-path node)))



(defn accepted?

  [state node]

  (contains? (state ::$.maestro/accepted)
             node))



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
      (accept node)
      ($.graph.dfs/deeper node+)))



(defn input?

  [state node]

  (contains? (state ::$.maestro/input)
             node))



(defn reject

  [state node]

  (update state
          ::rejected
          conj
          node))



(defn rejected?

  [state node]

  (contains? (state ::rejected)
             node))



(defn visited?

  [state node]

  (or (accepted? state
                 node)
      (rejected? state
                 node)))
