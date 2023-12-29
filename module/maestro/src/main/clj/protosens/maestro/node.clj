(ns protosens.maestro.node

  (:require [protosens.graph.dfs :as       $.graph.dfs]
            [protosens.maestro   :as-alias $.maestro]))


(set! *warn-on-reflection*
      true)


(declare conj-path)


;;;;;;;;;; 


(defn- -dispatch-by-namespace

  [_state kw]

  (or (namespace kw)
      (name kw)))



(defmulti enter

  #'-dispatch-by-namespace)


;;;;;;;;;;


(defn accept


  ([state node]

   (accept state
           node
           nil))


  ([state node child+]

   (-> state
       (update ::$.maestro/accepted
               conj
               node)
       (conj-path node)
       (cond->
         (not-empty child+)
         ($.graph.dfs/deeper child+)))))



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
