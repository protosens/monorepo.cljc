(ns protosens.maestro.required

  (:import (java.io PushbackReader))
  (:refer-clojure :exclude [print])
  (:require [clojure.edn             :as edn]
            [clojure.java.io         :as java.io]
            [clojure.set             :as set]
            [protosens.maestro.alias :as $.maestro.alias]
            [protosens.maestro.aggr  :as $.maestro.aggr]))


;;;;;;;;;;


(defn create-basis


  ([]

   (create-basis nil))


  ([option+]
   
   (-> (or (:maestro/project option+)
           "./deps.edn")
       (java.io/reader)
       (PushbackReader.)
       (edn/read))))


(defn cli-arg


  ([basis]

   (cli-arg basis
            (first *command-line-args*)))


  ([basis arg]

   (let [x (edn/read-string arg)]
     (reduce (fn [basis x]
               (update basis
                       (cond
                         (keyword? x) :maestro/alias+
                         (symbol? x)  :maestro/profile+
                         :else        (throw (ex-info "CLI argument must be a keyword or a symbol"
                                                      {:maestro/arg x})))
                       (fnil conj
                             [])
                       x))
             basis
             (if (vector? x)
               x
               [x])))))

;;;;;;;;;;


(defn- -search

  ;; TODO. Consider converting to breadth-first.

  [basis depth aggr alias+ profile+ consider-profile?]

  (reduce (fn [basis-2 alias]
            (let [[alias-2
                   profile] (if (map? alias)
                              (let [profile->alias alias]
                                (some (fn [profile]
                                        (when (consider-profile? basis
                                                                 depth
                                                                 profile)
                                          (when-some [alias (profile->alias profile)]
                                            [alias
                                             profile])))
                                      profile+))
                              [alias
                               'default])]
              (if (nil? alias-2)
                basis-2
                (let [basis-3 (update-in basis-2
                                         [:maestro/profile->alias+
                                          profile]
                                         (fnil conj
                                               #{})
                                         alias-2)]
                  (if (contains? (basis-2 :maestro/seen+)
                                 alias-2)
                    basis-3
                    (let [alias-data (get-in basis
                                             [:aliases
                                              alias-2])]
                      (aggr (-search (update basis-3
                                             :maestro/seen+
                                             (fnil conj
                                                   #{})
                                             alias-2)
                                     (inc depth)
                                     aggr
                                     (:maestro/require alias-data)
                                     profile+
                                     consider-profile?)
                            alias-2
                            alias-data)))))))
          basis
          alias+))


(defn search

  [basis]

  (let [profile+ (-> (basis :maestro/profile+)
                     vec
                     (conj 'default))]
    (-> basis
        (assoc :maestro/profile+
               profile+)
        (update :maestro/require
                #(or %
                     []))
        (-search 0
                 (or (basis :maestro/aggr)
                     $.maestro.aggr/default)
                 (basis :maestro/alias+)
                 profile+
                 (fn [_basis depth profile]
                   (not (and (> depth 
                                1)
                             (-> profile meta :direct?))))))))


;;;;;;;;;;


(defn by-profile+
  
  [basis profile+]

  (reduce set/union
          (-> basis
              :maestro/profile->alias+
              (select-keys profile+)
              vals)))


(defn not-by-profile+
  
  [basis profile+]

  (reduce set/union
          (-> (reduce (fn [profile->alias+ profile]
                        (dissoc profile->alias+
                                profile))
                      (basis :maestro/profile->alias+)
                      profile+)
              vals)))


;;;;;;;;;;


(defn print

  [basis]

  (-> basis
      :maestro/require
      $.maestro.alias/stringify+
      clojure.core/print)
  basis)
