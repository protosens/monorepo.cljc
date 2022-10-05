(ns protosens.dev.prelude

  (:require [protosens.namespace :as $.namespace]
            [protosens.symbol    :as $.symbol]))


;;;;;;;;;;


(defn req

  "Requires all Protosens namespace available on the classpath."

  []

  ($.namespace/require-found
    (fn [nmspace]
      (when ($.symbol/starts-with? nmspace
                                   'protosens.)
        [nmspace
         :as
         ($.symbol/replace-first nmspace
                                 'protosens
                                 '$)]))))
