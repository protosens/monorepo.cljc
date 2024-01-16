(ns protosens.task.nrepl

  (:require [protosens.maestro.plugin.nrepl :as $.maestro.plugin.nrepl]))


;;;;;;;;;;


(defn server

  []

  ($.maestro.plugin.nrepl/server [:ext/nrepl]
                                 ["-m" "nrepl.cmdline"
                                  "-p" "14563"]))
