(ns protosens.task.test.bb
  
  #?(:bb (:refer-clojure :exclude [test]))
  #?(:bb (:require [babashka.classpath         :as bb.classpath]
                   [kaocha.runner              :as K.runner]
                   [protosens.classpath        :as $.classpath]
                   [protosens.maestro.plugin   :as $.maestro.plugin]
                   [protosens.task.test.kaocha :as $.task.test.kaocha])))


;;;;;;;;;;


#?(:bb (defn run

  []

  (let [alias+ ($.task.test.kaocha/sync)]
    ($.maestro.plugin/intro "protosens.task.test/bb")
    ($.maestro.plugin/safe
       (delay
         ($.maestro.plugin/step "Testing `deps.edn` with Kaocha on Babashka")
         (println)
         (println)
         (bb.classpath/add-classpath ($.classpath/compute alias+))
         (apply K.runner/-main 
                (concat ["--config-file" "test/kaocha.edn"]
                        (rest *command-line-args*))))))))
