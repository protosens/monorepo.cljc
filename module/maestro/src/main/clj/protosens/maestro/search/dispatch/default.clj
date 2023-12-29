(ns protosens.maestro.search.dispatch.default

  (:require [protosens.maestro.plugin           :as $.maestro.plugin]
            [protosens.maestro.search           :as $.maestro.search]
            [protosens.maestro.search.alias     :as $.maestro.search.alias]
            [protosens.maestro.search.namespace :as $.maestro.search.namespace]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;;


(defn qualified

  ;; Qualified, must be an existing alias.

  [state node]

  (when-not ($.maestro.search.alias/defined? state
                                             node)
    ($.maestro.plugin/fail (format "Node `%s` does not exist"
                                   node)))
  (cond->
    state
    ($.maestro.search.alias/include? state
                                     node)
    ($.maestro.search.alias/deeper node)))



(defn unqualified

  ;; Unqualified, might be an existing alias but does not need to be.

  [state node]

  (-> state
      ($.maestro.search.namespace/include (name node))
      ($.maestro.search.alias/deeper node)))


;;;


(defn dispatch

  [state node]

  (let [f (if (qualified-keyword? node)
            qualified
            unqualified)]
    (f state
       node)))


(defmethod $.maestro.search/dispatch
           :default

  [state node]

  (dispatch state
            node))
