(ns protosens.maestro.alias

  (:require [protosens.graph.dfs         :as       $.graph.dfs]
            [protosens.maestro           :as-alias $.maestro]
            [protosens.maestro.namespace :as       $.maestro.namespace]
            [protosens.maestro.node      :as       $.maestro.node]))


(set! *warn-on-reflection*
      true)


(declare copy
         defined?
         definition
         inverted-graph)


;;;;;;;;;;


(defn accept

  [state node]

  (let [required+ (get-in state
                          [::$.maestro/deps-maestro-edn
                           :aliases
                           node
                          :maestro/require])]
    (-> state
        (copy node)
        ($.maestro.node/accept node
                               required+))))



(defn accepted

  [state]

  ;; Using `$.maestro.node/path` instead of `$.maestro.node/accepted` allows us
  ;; to see the order of acceptance, in case that matters.

  (keep (fn [[node _depth]]
          (when (defined? state
                          node)
            node))
        (state ::$.maestro.node/path)))



(defn copy

  [state node]

  (assoc-in state       
            [::$.maestro/deps-edn
             :aliases
             node]
            (definition state
                        node)))



(defn defined?

  [state node]

  (contains? (get-in state
                     [::$.maestro/deps-maestro-edn
                      :aliases])
             node))



(defn definition

  [state node]

  (get-in state
          [::$.maestro/deps-maestro-edn
           :aliases
          node]))



(defn dependent+


  ([state alias+]

   (dependent+ state
               alias+
               nil))


  ([state alias+ visit?]

   (let [alias->required-by (inverted-graph state)
         visit-2?           (or visit?
                                (fn visit-2? [_state-2 _alias]
                                  true))]
     (-> state
         (assoc ::path     []
                ::visited #{})
         ($.graph.dfs/walk (fn enter [state]
                             (let [alias ($.graph.dfs/node state)]
                               (cond->
                                 state
                                 (and (not (contains? (state ::visited)
                                                      alias))
                                      (visit-2? state
                                                alias))
                                 (-> (update ::path
                                             conj
                                             alias)
                                     (update ::visited
                                             conj
                                             alias)
                                     ($.graph.dfs/deeper (alias->required-by alias))))))
                           (mapcat alias->required-by
                                   alias+))
         (::path)))))



(defn include?

  [state node]

  (or ($.maestro.namespace/included? state
                                     (namespace node))
      ($.maestro.node/input? state
                             node)))



(defn inverted-graph

  [state]

  (let [alias->definition (get-in state
                                  [::$.maestro/deps-maestro-edn
                                   :aliases])]
    (reduce-kv (fn [state alias definition]
                 (reduce (fn [state-2 node]
                           (cond->
                             state-2
                             (contains? alias->definition
                                        node)
                             (update node
                                     (fnil conj
                                           [])
                                     alias)))
                         state
                         (:maestro/require definition)))
               {}
               alias->definition)))
