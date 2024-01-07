(ns protosens.task.cve

  (:require [babashka.deps     :as bb.deps]
            [clojure.string    :as C.string]
            [protosens.process :as $.process]))


;;;;;;;;;;


(defn run

  []

  (let [cp (with-out-str
             (bb.deps/clojure ["-Spath"]))]
    @($.process/shell ["clojure"
                       "-T:ext/nvd-clojure"
                       "nvd.task/check"
                       ":classpath"
                       (format "\"%s\""
                               cp)
                       ":config-filename"
                       "\"nvd/config.edn\""])))
