(ns protosens.maestro.node.enter.god

  (:require [protosens.maestro        :as-alias $.maestro]
            [protosens.maestro.node   :as       $.maestro.node]
            [protosens.maestro.plugin :as       $.maestro.plugin]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;;


(defn enter

  [state node]

  (when (qualified-keyword? node)
    ($.maestro.plugin/fail (format "`:GOD` node should not be namespaced: `%s`"
                                   node)))
  ($.maestro.node/accept state
                         node
                         (let [alias+ (keys (get-in state
                                                    [::$.maestro/deps-maestro-edn
                                                     :aliases]))]
                           (concat (sort (into #{}
                                               (comp (keep namespace)
                                                     (map keyword))
                                               alias+))
                                   (sort alias+)))))



(defmethod $.maestro.node/enter
           "GOD"

  [state node]

  (enter state
         node))
