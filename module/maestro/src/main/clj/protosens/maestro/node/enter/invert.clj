(ns protosens.maestro.node.enter.invert

  (:require [protosens.maestro.alias      :as $.maestro.alias]
            [protosens.maestro.node       :as $.maestro.node]
            [protosens.maestro.node.enter :as $.maestro.node.enter]))


;;;;;;;;;;


(defn enter

  [state node]
  
  ($.maestro.node.enter/assert-unqualified node)
  (let [alias-accepted+  ($.maestro.alias/accepted state)
        alias-dependent+ ($.maestro.alias/dependent+ state
                                                     alias-accepted+
                                                     $.maestro.alias/include?)]
    (-> state
        ($.maestro.node/unreject+ alias-dependent+)
        ($.maestro.node/accept node
                               alias-dependent+))))



(defmethod $.maestro.node/enter
           "INVERT"

  [state node]

  (enter state
         node))
