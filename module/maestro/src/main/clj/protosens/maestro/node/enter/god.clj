(ns protosens.maestro.node.enter.god

  (:require [protosens.maestro           :as-alias $.maestro]
            [protosens.maestro.namespace :as $.maestro.namespace]
            [protosens.maestro.node      :as       $.maestro.node]
            [protosens.maestro.plugin    :as       $.maestro.plugin]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;;


(defn enter

  [state node]

  (when (qualified-keyword? node)
    ($.maestro.plugin/fail (format "`:GOD` node should not be namespaced: `%s`"
                                   node)))
  (let [alias+        (keys (get-in state
                                    [::$.maestro/deps-maestro-edn
                                     :aliases]))
        node-nmspace+ (sort (into #{}
                                  (comp (keep namespace)
                                        (map keyword))
                                  alias+))
        node+         (concat node-nmspace+
                              alias+)]
    (-> state
        ($.maestro.namespace/force-include+ node-nmspace+)
        ($.maestro.node/unreject+ node+)
        ($.maestro.node/accept node
                               node+))))



(defmethod $.maestro.node/enter
           "GOD"

  [state node]

  (enter state
         node))
