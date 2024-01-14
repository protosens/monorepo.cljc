(ns protosens.task.deps.nvd

  (:require [babashka.fs              :as bb.fs]
            [protosens.classpath      :as $.classpath]
            [protosens.maestro        :as $.maestro]
            [protosens.maestro.alias  :as $.maestro.alias]
            [protosens.maestro.plugin :as $.maestro.plugin]
            [protosens.process        :as $.process]))


;;;;;;;;;; Private


(defn- -api-token

  []

  (let [path "./private/nvd-token.txt"]
    ($.maestro.plugin/step (format "Retrieving NVD API token from `%s`"
                                   path))
    (when-not (bb.fs/exists? path)
      ($.maestro.plugin/fail (format "NVD API token must be in `%s`"
                                     path)))
    (or (not-empty (slurp path))
        ($.maestro.plugin/fail (format "NVD API token from `%s` is empty?"
                                       path)))))



(defn- -classpath

  []

  ($.maestro.plugin/step "Computing classpath to check from current `deps.edn`")
  (binding [$.maestro.plugin/*print-path?* true]
    (let [alias+ (->> ($.maestro/run-string (or (first *command-line-args*)
                                                ":GOD")
                                            ($.maestro.plugin/read-deps-edn))
                      ($.maestro.alias/accepted)
                      (filter #(not= %
                                     :ext/nvd-clojure)))]
      ($.classpath/compute alias+))))


;;;;;;;;;; Public


(defn check

  []

  ($.maestro.plugin/intro "protosens.task.deps.nvd/check")
  ($.maestro.plugin/safe
    (delay
      (let [token (-api-token)
            cp    (-classpath)]
        ($.maestro.plugin/step "Everything is ready, will now run `:ext/nvd-clojure`")
        (println)
        (if (-> ($.process/shell ["clojure"
                                  "-T:ext/nvd-clojure"
                                  "nvd.task/check"
                                  ":classpath"
                                  (format "\"%s\""
                                          cp)
                                  ":config-filename"
                                  "\"./nvd/config.edn\""]
                                 {:extra-env {"NVD_API_TOKEN" token}})
                ($.process/success?))
          ($.maestro.plugin/done "No vulnerabilities found")
          ($.maestro.plugin/fail "Some vulnerabilities were found or an error occured"))))))
