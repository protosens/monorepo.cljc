(ns protosens.task.test.kaocha

  (:refer-clojure :exclude [sync])
  (:require [protosens.maestro.plugin.kaocha :as $.maestro.plugin.kaocha]))


;;;;;;;;;;


(defn sync

  []

  ($.maestro.plugin.kaocha/sync "./private/tmp/maestro-kaocha.edn"
                                [:test]))
