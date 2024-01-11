(ns protosens.task.test.playwright

  (:refer-clojure :exclude [compile])
  (:require [protosens.task.shadow :as $.task.shadow]))


;;;;;;;;;;


(defn compile

  [compilation-mode]

  ($.task.shadow/run [compilation-mode
                      ":test/playwright"]))
