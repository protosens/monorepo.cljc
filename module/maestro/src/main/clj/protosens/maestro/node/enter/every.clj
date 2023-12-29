(ns protosens.maestro.node.enter.every

  (:require [protosens.maestro      :as-alias $.maestro]
            [protosens.maestro.node :as       $.maestro.node]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;;


(defn enter

  [state node]

  (if-some [nm (name node)]
    ($.maestro.node/accept state
                           node
                           (cons (keyword nm)
                                 (sort (filter (fn [kw]
                                                 (= (namespace kw)
                                                    nm))
                                               (keys (get-in state
                                                             [::$.maestro/deps-maestro-edn
                                                              :aliases]))))))
    state))



(defmethod $.maestro.node/enter
           "EVERY"

  [state node]

  (enter state
         node))
