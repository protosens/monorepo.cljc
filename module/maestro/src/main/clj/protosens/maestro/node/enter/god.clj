(ns protosens.maestro.node.enter.god

  (:require [protosens.maestro            :as-alias $.maestro]
            [protosens.maestro.node       :as       $.maestro.node]
            [protosens.maestro.node.enter :as       $.maestro.node.enter]
            [protosens.maestro.qualifier  :as       $.maestro.qualifier]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;;


(defn enter

  [state node]

  ($.maestro.node.enter/assert-unqualified node)
  (let [alias+         (-> state
                           (get-in [::$.maestro/deps.edn
                                    :aliases])
                           (keys)
                           (sort))
        node-qualifier+ (sort (into #{}
                                  (comp (keep namespace)
                                        (map keyword))
                                  alias+))
        node+           (concat node-qualifier+
                                alias+)]
    (-> state
        ($.maestro.qualifier/force-include+ node-qualifier+)
        ($.maestro.node/unreject+ node+)
        ($.maestro.node/accept node
                               node+))))



(defmethod $.maestro.node/enter
           "GOD"

  [state node]

  (enter state
         node))
