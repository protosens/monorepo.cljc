(ns protosens.maestro.plugin.nrepl

  (:require [protosens.maestro        :as $.maestro]
            [protosens.maestro.plugin :as $.maestro.plugin]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;;


(defn server

  [alias+ prepend-arg+]

  ($.maestro.plugin/intro "protosens.maestro.plugin.nrepl/server")
  ($.maestro.plugin/safe*
    ($.maestro.plugin/step "Starting NREPL server")
    (let [[cli-alias+
           cli-arg+]  ($.maestro.plugin/split-cli-arg+)]
      ($.maestro/clj (concat [(apply str
                                     "-M"
                                     (concat (or cli-alias+
                                                 [:GOD])
                                             alias+))]
                             prepend-arg+
                             cli-arg+)))))
