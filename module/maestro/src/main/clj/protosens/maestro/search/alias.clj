(ns protosens.maestro.search.alias

  (:require [protosens.maestro                  :as-alias $.maestro]
            [protosens.maestro.search           :as       $.maestro.search]
            [protosens.maestro.search.namespace :as       $.maestro.search.namespace]))


(set! *warn-on-reflection*
      true)


(declare definition)


;;;;;;;;;;


(defn copy

  [state alias]

  (assoc-in state       
            [::$.maestro/deps-edn
             :aliases
             alias]
            (definition state
                        alias)))



(defn defined?

  [state alias]

  (contains? (get-in state
                     [::$.maestro/deps-maestro-edn
                      :aliases])
             alias))



(defn definition

  [state alias]

  (get-in state
          [::$.maestro/deps-maestro-edn
           :aliases
          alias]))



(defn deeper

  [state alias]

  (let [required-alias+ (get-in state
                                [::$.maestro/deps-maestro-edn
                                 :aliases
                                 alias
                                 :maestro/require])]
    (-> state
        (copy alias)
        ($.maestro.search/deeper alias
                                 required-alias+))))



(defn include?

  [state alias]

  (or ($.maestro.search.namespace/included? state
                                            (namespace alias))
      ($.maestro.search/input? state
                               alias)))
