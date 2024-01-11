(ns protosens.task.deps

  (:refer-clojure :exclude [sync])
  (:require [protosens.maestro               :as $.maestro]
            [protosens.maestro.plugin.kaocha :as $.maestro.plugin.kaocha]))


;;;;;;;;;;


(defn sync

  []

  (-> ($.maestro/sync)
      ($.maestro.plugin.kaocha/sync)))
