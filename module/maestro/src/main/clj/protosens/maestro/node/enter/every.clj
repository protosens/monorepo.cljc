(ns protosens.maestro.node.enter.every

  (:require [protosens.maestro           :as-alias $.maestro]
            [protosens.maestro.namespace :as $.maestro.namespace]
            [protosens.maestro.node      :as       $.maestro.node]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;;


(defn enter

  [state node]

  (if-some [nm (name node)]
    (let [node-nmspace (keyword nm)
          node+        (sort (filter (fn [kw]
                                       (= (namespace kw)
                                          nm))
                                     (keys (get-in state
                                                   [::$.maestro/deps-maestro-edn
                                                    :aliases]))))]
    (-> state
        ($.maestro.namespace/force-include node-nmspace)
        ($.maestro.node/unreject+ node+)
        ($.maestro.node/accept node
                               (cons node-nmspace
                                     node+))))
    state))



(defmethod $.maestro.node/enter
           "EVERY"

  [state node]

  (enter state
         node))
