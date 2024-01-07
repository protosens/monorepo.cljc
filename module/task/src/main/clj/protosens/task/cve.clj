(ns protosens.task.cve

  (:require [protosens.classpath :as $.classpath]
            [protosens.process   :as $.process]))


;;;;;;;;;;


(defn run

  []

  (let [cp ($.classpath/compute)]
    @($.process/shell ["clojure"
                       "-T:ext/nvd-clojure"
                       "nvd.task/check"
                       ":classpath"
                       (format "\"%s\""
                               cp)
                       ":config-filename"
                       "\"nvd/config.edn\""])))
