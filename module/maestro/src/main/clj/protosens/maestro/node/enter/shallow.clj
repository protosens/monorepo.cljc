(ns protosens.maestro.node.enter.shallow

  (:require [protosens.maestro.node      :as $.maestro.node]
            [protosens.maestro.qualifier :as $.maestro.qualifier]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;;


(defn enter

  [state node]

  (-> state
      (cond->
        (qualified-keyword? node)
        ($.maestro.qualifier/exclude (name node)))
      ($.maestro.node/accept node)))



(defmethod $.maestro.node/enter
           "SHALLOW"

  [state node]

  (enter state
         node))
