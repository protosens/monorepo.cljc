(ns protosens.task.nrepl

  (:require [protosens.deps.edn       :as $.deps.edn]
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
        ($.maestro.plugin/fail "`deps.edn` does not contain `:ext/nrepl`, are you in `:dev` mode?"))
      (println)
      ($.task/shell (concat ["clojure"
                                "-M:ext/nrepl"]
                            *command-line-args*)))))
