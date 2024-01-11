(ns protosens.task.nrepl

  (:require [protosens.task :as $.task]))


;;;;;;;;;;


(defn run

  []

  ($.task/shell (concat ["clojure"
                            "-M:ext/nrepl"]
                        *command-line-args*)))
