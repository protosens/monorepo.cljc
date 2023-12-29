(ns protosens.maestro.alias

  (:require [protosens.maestro           :as-alias $.maestro]
            [protosens.maestro.namespace :as       $.maestro.namespace]
            [protosens.maestro.node      :as       $.maestro.node]))


(set! *warn-on-reflection*
      true)


(declare copy
         definition)


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



(defn include?

  [state node]

  (or ($.maestro.namespace/included? state
                                     (namespace node))
      ($.maestro.node/input? state
                             node)))
