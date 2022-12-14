(ns user

  "Loaded automatically."

  (:require [protosens.namespace :as $.namespace]
            [protosens.symbol    :as $.symbol]
            [portal.api          :as portal]))


;;;;;;;;;; Automatically requiring namespaces


(defn req

  "Requires all Protosens namespace available on the classpath."

  []

  ($.namespace/require-cp-dir+
    (fn [nmspace]
      (when ($.symbol/starts-with? nmspace
                                   'protosens.)
        [nmspace
         :as
         ($.symbol/replace-first nmspace
                                 'protosens
                                 '$)]))))



(def required-ns+

  "Namespace required automatically."

  (req))


;;;;;;;;;; Portal


(defn p

  "Opens Portal in the browser."

  []

  (add-tap portal/submit)
  (portal/open {:app false}))
