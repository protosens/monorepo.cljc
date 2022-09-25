(ns user

  "Loaded automatically."

  (:require [clojure.string         :as string]
            [protosens.maestro.user :as $.maestro.user]))


;;;;;;;;;;


(defn req

  "Require all namespaces from this repository that are present on the classpath."

  []

  ($.maestro.user/require-filtered {:map-namespace  (fn [nmspace]
                                                      (when (string/includes? (str nmspace)
                                                                              "protosens")
                                                        [nmspace
                                                         :as
                                                         (symbol (str "$."
                                                                      (second (string/split (str nmspace)
                                                                                            #"protosens\."))))]))
                                    :require.before (fn [nmspace]
                                                      (println "Require"
                                                               nmspace))}))



(def required-ns+

  "Namespace required automatically."

  (req))
