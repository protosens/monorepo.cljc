(ns protosens.task.test.jvm

  (:require [clojure.string             :as C.string]
            [protosens.maestro.plugin   :as $.maestro.plugin]
            [protosens.task             :as $.task]
            [protosens.task.test.kaocha :as $.task.test.kaocha]))


;;;;;;;;;;


(defn run


  ([]

   (run nil))


  ([arg+]

   (let [alias+ ($.task.test.kaocha/sync)]
     ($.maestro.plugin/intro "protosens.task.test.jvm/run")
     ($.maestro.plugin/safe
       (delay
         ($.maestro.plugin/step "Testing synced aliases with Kaocha on the JVM")
         (println)
         (println)
         ($.task/shell (concat ["clj"
                                (str "-M"
                                     (C.string/join ""
                                                    alias+)
                                     :ext/kaocha)]
                                ["-m"            "kaocha.runner"
                                 "--config-file" "test/kaocha.edn"]
                                arg+
                                (rest *command-line-args*))))))))
