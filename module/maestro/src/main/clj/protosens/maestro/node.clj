(ns protosens.maestro.node

  (:require [protosens.graph.dfs :as       $.graph.dfs]
            [protosens.maestro   :as-alias $.maestro]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;; Helpers


(defn ^:no-doc init-state

  [state node+]

  (assoc state
         ::accepted #{}
         ::input    (set node+)
         ::rejected #{}))


;;;;;;;;;; Important, entry point for handling nodes


(defn- -dispatch-by-namespace

  [_state kw]

  (or (namespace kw)
      (name kw)))



(defmulti enter

  #'-dispatch-by-namespace)


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
       ;;
       ;; Only needed for tests (but does not harm, might even become more useful).
       (update ::$.maestro/path
               conj
               [node
                ($.graph.dfs/depth state)])
       ;;
       (cond->
         (not-empty child+)
         ($.graph.dfs/deeper child+)))))



(defn accepted?

  [state node]

  (contains? (state ::accepted)
             node))



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



(defn visited?

  [state node]

  (or (accepted? state
                 node)
      (rejected? state
                 node)))
