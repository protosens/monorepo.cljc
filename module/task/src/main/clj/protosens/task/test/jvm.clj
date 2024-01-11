(ns protosens.task.test.jvm

  (:require [protosens.maestro.plugin        :as $.maestro.plugin]
            [protosens.maestro.plugin.kaocha :as $.maestro.plugin.kaocha]
            [protosens.task                  :as $.task]))


;;;;;;;;;;


(defn run


  ([]

   (run nil))


  ([arg+]

   ($.maestro.plugin.kaocha/sync)
   ($.maestro.plugin/intro "protosens.task.test.jvm/run")
   ($.maestro.plugin/safe
     (delay
       ($.maestro.plugin/step "Testing `deps.edn` with Kaocha on the JVM")
       (println)
       (println)
       ($.task/shell (concat ["clojure"
                              "-M:ext/kaocha"]
                             arg+
                             *command-line-args*))))))
