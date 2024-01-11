(ns protosens.task.test.node

  (:require [protosens.task.shadow :as $.task.shadow]))


;;;;;;;;;;


(defn- -run

  [arg+ compilation-mode]

  ($.task.shadow/run (concat arg+
                             [compilation-mode
                              ":test/node"])))



(defn advanced

  []

  (-run nil
        "release"))



(defn simple

  []

  (-run nil
        "compile"))



(defn watch

  []

  (-run ["--config-merge"
         "{:autorun true}"]
        "watch"))

