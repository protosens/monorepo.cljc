(ns protosens.task.shadow

  (:require [protosens.process :as $.process]))


;;;;;;;;;;


(defn run


  ([]

   (run nil))


  ([arg+]

   (let [exit-code (-> (protosens.process/shell (concat ["clojure"
                                                         "-M:ext/shadow-cljs"]
                                                        arg+
                                                        *command-line-args*))
                       ($.process/exit-code))]
     (when (not (zero? exit-code))
       (System/exit exit-code))
     exit-code)))
