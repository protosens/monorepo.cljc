(ns protosens.maestro.node.enter.shallow

  (:require [protosens.maestro.namespace :as $.maestro.namespace]
            [protosens.maestro.node      :as $.maestro.node]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;;


(defn enter

  [state node]

  (-> state
      (cond->
        (qualified-keyword? node)
        ($.maestro.namespace/exclude (name node)))
      ($.maestro.node/accept node)))



(defmethod $.maestro.node/enter
           "SHALLOW"

  [state node]

  (enter state
         node))
