(ns protosens.task.bb
  
  #?(:bb (:refer-clojure :exclude [test]))
  #?(:bb (:require [babashka.classpath              :as bb.classpath]
                   [kaocha.runner                   :as K.runner]
                   [protosens.classpath             :as $.classpath]
                   [protosens.maestro.plugin        :as $.maestro.plugin]
                   [protosens.maestro.plugin.kaocha :as $.maestro.plugin.kaocha])))


;;;;;;;;;;


#?(:bb (defn test

  []

  ($.maestro.plugin.kaocha/sync)
  ($.maestro.plugin/intro "protosens.task.bb/test")
  ($.maestro.plugin/safe
     (delay
       ($.maestro.plugin/step "Testing `deps.edn` with Kaocha on Babashka")
       (println)
       (println)
       (bb.classpath/add-classpath ($.classpath/compute))
       (apply K.runner/-main 
              (concat ["--config-file"
                       "test/kaocha.edn"]
                      *command-line-args*))))))
