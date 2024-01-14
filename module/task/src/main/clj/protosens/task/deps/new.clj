(ns protosens.task.deps.new

  (:require [protosens.maestro        :as $.maestro]
            [protosens.maestro.plugin :as $.maestro.plugin]
            [protosens.process        :as $.process]))


;;;;;;;;;;


(defn check

  []

  ($.maestro.plugin/intro "protosens.task.deps.new/check")
  ($.maestro.plugin/safe
    (delay
      ($.maestro.plugin/step "Running Antq to find newest versions of dependencies")
      (println)
      (if (-> ($.process/shell ["clojure"
                                "-T:ext/antq"
                                "antq.tool/outdated"])
              ($.process/success?))
        ($.maestro.plugin/done "Everything looks up-to-date")
        ($.maestro.plugin/fail "Newer versions found")))))
