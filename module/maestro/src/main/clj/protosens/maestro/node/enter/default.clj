(ns protosens.maestro.node.enter.default

  (:require [protosens.maestro.alias     :as $.maestro.alias]
            [protosens.maestro.plugin    :as $.maestro.plugin]
            [protosens.maestro.node      :as $.maestro.node]
            [protosens.maestro.namespace :as $.maestro.namespace]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;;


(defn qualified

  ;; Qualified, must be an existing alias.

  [state node]

  (when-not ($.maestro.alias/defined? state
                                      node)
    ($.maestro.plugin/fail (format "Node `%s` does not exist"
                                   node)))
  (cond->
    state
    ($.maestro.alias/include? state
                              node)
    ($.maestro.alias/accept node)))



(defn unqualified

  ;; Unqualified, might be an existing alias but does not need to be.

  [state node]

  (-> state
      ($.maestro.namespace/include (name node))
      ($.maestro.alias/accept node)))


;;;


(defn enter

  [state node]

  (let [f (if (qualified-keyword? node)
            qualified
            unqualified)]
    (f state
       node)))


(defmethod $.maestro.node/enter
           :default

  [state node]

  (enter state
         node))
