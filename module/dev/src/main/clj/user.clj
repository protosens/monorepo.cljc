(ns user

  "Loaded automatically."

  (:require [clojure.string         :as string]
            [protosens.maestro.user :as $.maestro.user]))


;;;;;;;;;;


(defn req

  "Require all namespaces from this repository that are present on the classpath."

  []

  ($.maestro.user/require-filtered {:map-namespace  (fn [nmspace]
                                                      (let [nmspace-str (str nmspace)]
                                                        (when (and (string/includes? nmspace-str
                                                                                     "protosens")
                                                                   (not= nmspace
                                                                         'protosens.maestro.plugin.quickdoc))

                                                          [nmspace
                                                           :as
                                                           (symbol (str "$."
                                                                        (second (string/split nmspace-str
                                                                                              #"protosens\."))))])))
                                    :require.before (fn [nmspace]
                                                      (println "Require"
                                                               nmspace))}))



(def required-ns+

  "Namespace required automatically."

  (req))
