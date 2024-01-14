(ns protosens.maestro.node

  (:require [clojure.set         :as C.set]
            [protosens.graph.dfs :as $.graph.dfs]))


(set! *warn-on-reflection*
      true)


(declare unreject+)


;;;;;;;;;; Helpers


(defn ^:no-doc init-state

  [state node+]

  (assoc state
         ::accepted #{}
         ::input    (set node+)
         ::path     []
         ::rejected #{}))


;;;;;;;;;; Important, entry point for handling nodes


(defn- -dispatch-by-qualifier

  [_state kw]

  (or (namespace kw)
      (name kw)))



(defmulti enter

  #'-dispatch-by-qualifier)


;;;;;;;;;; API


(defn accept


  ([state node]

   (accept state
           node
           nil))


  ([state node child+]

   (-> state
       (update ::accepted
               conj
               node)
       (update ::path
               conj
               [node
                ($.graph.dfs/depth state)])  ;; Depth only useful for tests.
       (cond->
         (not-empty child+)
         ($.graph.dfs/deeper child+)))))



(defn accepted?

  [state node]

  (contains? (state ::accepted)
             node))



(defn expand-input

  [node+]

  (into []
        (comp (mapcat (fn [alias]
                        (if (qualified-keyword? alias)
                          [(keyword (namespace alias))
                           alias]
                          [alias])))
              (distinct))
        node+))



(defn input?

  [state node]

  (contains? (state ::input)
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



(defn unreject+

  [state node+]

  (update state
          ::rejected
          C.set/difference
          (set node+)))



(defn visited?

  [state node]

  (or (accepted? state
                 node)
      (rejected? state
                 node)))
