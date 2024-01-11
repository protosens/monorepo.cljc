(ns protosens.task.shadow

  (:require [protosens.deps.edn       :as $.deps.edn]
            [protosens.maestro.plugin :as $.maestro.plugin]
            [protosens.process        :as $.process]))


;;;;;;;;;;


(defn run


  ([]

   (run nil))


  ([arg+]

   (when-not (-> ($.deps.edn/read)
                 (:aliases)
                 (contains? :ext/shadow-cljs))
     ($.maestro.plugin/fail "`deps.edn` does not contain `:ext/shadow-cljs`"))
   (let [exit-code (-> (protosens.process/shell (concat ["clojure"
                                                         "-M:ext/shadow-cljs"]
                                                        arg+
                                                        *command-line-args*))
                       ($.process/exit-code))]
     (when (not (zero? exit-code))
       ($.maestro.plugin/fail (format "Shadow-CLJS terminated with code %d"
                                      exit-code)))
     exit-code)))



(defn task

  []

  ($.maestro.plugin/intro "protosens.task.shadow/task")
  ($.maestro.plugin/safe
    (delay
      ($.maestro.plugin/step "Running Shadow-CLJS with command-line arguments")
      (println)
      (run)
      ($.maestro.plugin/done "Shadow-CLJS terminated normally"))))
