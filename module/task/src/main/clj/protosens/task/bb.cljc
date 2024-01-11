(ns protosens.task.bb
  
  #?(:bb (:refer-clojure :exclude [test]))
  #?(:bb (:require [babashka.classpath  :as bb.classpath]
                   [kaocha.runner       :as K.runner]
                   [protosens.classpath :as $.classpath])))


;;;;;;;;;;


#?(:bb (defn test

  []

  (bb.classpath/add-classpath ($.classpath/compute))
  (apply K.runner/-main 
         (concat ["--config-file"
                  "test/kaocha.edn"]
                 *command-line-args*))))
