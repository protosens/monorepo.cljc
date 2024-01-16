(ns protosens.maestro.plugin.nrepl

  (:require [protosens.maestro        :as $.maestro]
            [protosens.maestro.plugin :as $.maestro.plugin]))


;;;;;;;;;;


(defn server

  [alias+ prepend-arg+]

  ($.maestro.plugin/intro "protosens.maestro.plugin.nrepl/server")
  ($.maestro.plugin/safe*
    ($.maestro.plugin/step "Starting NREPL server")
    ($.maestro.plugin/with-appended-cli-alias+*
      alias+
      ($.maestro/clj (concat [(str "-M"
                                   (first *command-line-args*))]
                             prepend-arg+
                             (rest *command-line-args*))))))
