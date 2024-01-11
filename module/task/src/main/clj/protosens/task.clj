(ns protosens.task

  (:require [protosens.process :as $.process]))


;;;;;;;;;;


(defn shell

  [command]

  (-> ($.process/shell command)
      ($.process/exit-code)
      (System/exit)))
