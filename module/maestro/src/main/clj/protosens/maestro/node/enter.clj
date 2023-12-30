(ns protosens.maestro.node.enter

  (:require [protosens.maestro.plugin :as $.maestro.plugin]))


;;;;;;;;;;


(defn assert-unqualified

  [node]

  (when (qualified-keyword? node)
    ($.maestro.plugin/fail (format "`:GOD` node should not be namespaced: `%s`"
                                   (namespace node)))))
