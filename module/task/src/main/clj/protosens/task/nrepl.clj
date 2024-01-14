(ns protosens.task.nrepl

  (:require [protosens.deps.edn       :as $.deps.edn]
            [protosens.maestro        :as $.maestro]
            [protosens.maestro.plugin :as $.maestro.plugin]
            [protosens.task           :as $.task]))


;;;;;;;;;;


(defn server

  []

  ($.maestro.plugin/intro "protosens.task.nrepl/server")
  ($.maestro.plugin/safe
    (delay
      ($.maestro.plugin/step "Starting NREPL server")
      (when-not (-> ($.deps.edn/read)
                    (:aliases)
                    (contains? :ext/nrepl))
        ($.maestro.plugin/fail (format "`deps.edn` does not contain `:ext/nrepl`")))
      ($.task/with-appended-alias+
        [:ext/nrepl]
        (delay
          ($.maestro/clj (concat [(str "-M"
                                       (first *command-line-args*))
                                  "-m" "nrepl.cmdline"
                                  "-p" "14563"]
                                 (rest *command-line-args*))))))))
