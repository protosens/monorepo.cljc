(ns protosens.maestro.node.enter.every

  (:require [protosens.maestro           :as-alias $.maestro]
            [protosens.maestro.node      :as       $.maestro.node]
            [protosens.maestro.qualifier :as       $.maestro.qualifier]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;;


(defn enter

  [state node]

  (if (qualified-keyword? node)
    (let [nm             (name node)
          node-qualifier (keyword nm)
          node-matching+ (sort (filter (fn [kw]
                                         (= (namespace kw)
                                            nm))
                                       (keys (get-in state
                                                     [::$.maestro/deps-maestro-edn
                                                      :aliases]))))
          node+          (cons node-qualifier
                               node-matching+)]
      (-> state
          ($.maestro.qualifier/unexclude node-qualifier)
          ($.maestro.node/unreject+ node+)
          ($.maestro.node/accept node
                                 node+)))
    ($.maestro.node/accept state
                           node)))



(defmethod $.maestro.node/enter
           "EVERY"

  [state node]

  (enter state
         node))
