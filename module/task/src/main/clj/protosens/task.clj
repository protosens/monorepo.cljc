(ns protosens.task

  (:require [protosens.process :as $.process]))


;;;;;;;;;;


(defn with-appended-alias+

  [alias+ delayed]

  (let [cli-alias+  (first *command-line-args*)
        cli-alias+? (= (first cli-alias+)
                       \:)]
    (binding [*command-line-args* (cons (apply str
                                               (when cli-alias+?
                                                 cli-alias+)
                                               alias+)
                                        (cond->
                                          *command-line-args*
                                          cli-alias+?
                                          (rest)))]
      @delayed)))



(defn shell

  [command]

  (-> ($.process/shell command)
      ($.process/exit-code)
      (System/exit)))
