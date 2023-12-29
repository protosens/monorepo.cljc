(ns protosens.maestro.search.dispatch.every

  (:require [protosens.maestro        :as-alias $.maestro]
            [protosens.maestro.search :as       $.maestro.search]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;;


(defn dispatch

  [state node]

  (if-some [nm (name node)]
    ($.maestro.search/deeper state
                             node
                             (cons (keyword nm)
                                   (sort (filter (fn [alias]
                                                   (= (namespace alias)
                                                      nm))
                                                 (keys (get-in state
                                                               [::$.maestro/deps-maestro-edn
                                                                :aliases]))))))
    state))



(defmethod $.maestro.search/dispatch
           "EVERY"

  [state node]

  (dispatch state
            node))
