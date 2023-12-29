(ns protosens.maestro.node.enter.shallow

  (:require [protosens.maestro.namespace :as $.maestro.namespace]
            [protosens.maestro.node      :as $.maestro.node]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;;


(defn enter

  [state node]

  (-> (if-some [nm (name node)]
        ($.maestro.namespace/exclude state
                                     nm)
        state)
      ($.maestro.node/accept node)))



(defmethod $.maestro.node/enter
           "SHALLOW"

  [state node]

  (enter state
         node))
