(ns protosens.maestro.search.dispatch.shallow

  (:require [protosens.maestro.search           :as $.maestro.search]
            [protosens.maestro.search.namespace :as $.maestro.search.namespace]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;;


(defn dispatch

  [state node]

  (-> (if-some [nm (name node)]
        (-> state
            ($.maestro.search.namespace/exclude nm))
        state)
      ($.maestro.search/accept node)))



(defmethod $.maestro.search/dispatch
           "SHALLOW"

  [state node]

  (dispatch state
            node))
