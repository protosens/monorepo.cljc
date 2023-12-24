(ns user

  "Loaded automatically."

  (:require [protosens.namespace :as $.namespace]
            [protosens.symbol    :as $.symbol]
            [portal.api          :as portal]))


;;;;;;;;;; Portal


(def *portal-started?

  (atom false))



(defn portal-tab

  []

  (portal/open {:app false}))



(defn portal-start

  "Opens Portal in the browser."

  []

  (let [[open?
         _]    (reset-vals! *portal-started?
                            true)]
    (when-not open?
      (add-tap portal/submit)
      (portal-tab))))



(intern 'clojure.core
        '_p
        portal-tab)



(intern 'clojure.core
        '_t
        (fn [& arg+]
          (portal-start)
          (tap> (if (= (count arg+)
                       1)
                  (first arg+)
                  (vec arg+)))))


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
