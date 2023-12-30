(ns protosens.maestro.node.enter.god

  (:require [protosens.maestro            :as-alias $.maestro]
            [protosens.maestro.namespace  :as $.maestro.namespace]
            [protosens.maestro.node       :as       $.maestro.node]
            [protosens.maestro.node.enter :as       $.maestro.node.enter]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;;


(defn enter

  [state node]

  ($.maestro.node.enter/assert-unqualified node)
  (let [alias+        (-> state
                          (get-in [::$.maestro/deps-maestro-edn
                                   :aliases])
                          (keys)
                          (sort))
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
