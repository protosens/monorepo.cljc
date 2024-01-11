(ns protosens.task.test.jvm

  (:require [protosens.task :as $.task]))


;;;;;;;;;;


(defn run


  ([]

   (run nil))


  ([arg+]

   ($.task/shell (concat ["clojure"
                          "-M:ext/kaocha"]
                         arg+
                         *command-line-args*))))
