(ns protosens.maestro.search.dispatch.god

  (:require [protosens.maestro        :as-alias $.maestro]
            [protosens.maestro.plugin :as       $.maestro.plugin]
            [protosens.maestro.search :as       $.maestro.search]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;;


(defmethod $.maestro.search/dispatch
           "GOD"

  [state node]

  (when (qualified-keyword? node)
    ($.maestro.plugin/fail (format "`:GOD` node should not be namespaced: `%s`"
                                   node)))
  ($.maestro.search/deeper state
                           node
                           (let [alias+ (keys (get-in state
                                                      [::$.maestro/deps-maestro-edn
                                                       :aliases]))]
                             (concat (sort (into #{}
                                                 (comp (keep namespace)
                                                       (map keyword))
                                                 alias+))
                                     (sort alias+)))))
