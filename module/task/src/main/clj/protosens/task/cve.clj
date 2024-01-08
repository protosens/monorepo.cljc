(ns protosens.task.cve

  (:require [babashka.fs              :as bb.fs]
            [protosens.classpath      :as $.classpath]
            [protosens.maestro.plugin :as $.maestro.plugin]
            [protosens.process        :as $.process]))


;;;;;;;;;; Private


(defn- -classpath

  []

  ($.maestro.plugin/step "Computing classpath to check from current `deps.edn`")
  ($.classpath/compute))



(defn- -nvd-token

  []

  (let [path "private/nvd-token.txt"]
    ($.maestro.plugin/step (format "Retrieving NVD API token from `%s`"
                                   path))
    (when-not (bb.fs/exists? path)
      ($.maestro.plugin/fail (format "NVD API token must be in `%s`"
                                     path)))
    (or (not-empty (slurp path))
        ($.maestro.plugin/fail (format "NVD API token from `%s` is empty?"
                                       path)))))


;;;;;;;;;; Public


(defn check

  []

  ($.maestro.plugin/intro "protosens.task.cve/check")
  ($.maestro.plugin/safe
    (delay
      (let [cp        (-classpath)
            nvd-token (-nvd-token)]
        ($.maestro.plugin/done "Everything is ready, will now run `:ext/nvd-clojure`")
        @($.process/shell ["clojure"
                           "-T:ext/nvd-clojure"
                           "nvd.task/check"
                           ":classpath"
                           (format "\"%s\""
                                   cp)
                           ":config-filename"
                           "\"nvd/config.edn\""]
                          {:extra-env {"NVD_API_TOKEN" nvd-token}})))))
