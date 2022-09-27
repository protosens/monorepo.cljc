(ns protosens.maestro

  "See README about core principles, [[search]] being the star of this namespace."

  (:import (java.io PushbackReader))
  (:refer-clojure :exclude [print])
  (:require [clojure.edn               :as edn]
            [clojure.java.io           :as java.io]
            [clojure.set               :as set]
            [protosens.maestro.alias   :as $.maestro.alias]
            [protosens.maestro.aggr    :as $.maestro.aggr]
            [protosens.maestro.profile :as $.maestro.profile]))


;;;;;;;;;;


(defn create-basis

  "Reads and prepares a `deps.edn` file.

   Takes a nilable map of options such as:

   | Key                | Value                                 | Default          |
   |--------------------|---------------------------------------|------------------|
   | `:maestro/project` | Alternative path to a `deps.edn` file | `\"./deps.edn\"` |"


  ([]

   (create-basis nil))


  ([option+]
   
   (-> (or (:maestro/project option+)
           "./deps.edn")
       (java.io/reader)
       (PushbackReader.)
       (edn/read))))


(defn cli-arg

  "Reads a command-line argument and updates the basis accordingly.
  
   Either:

   - Single alias
   - Vector of aliases and/or profiles

   Uses the first item of `*command-line-args*` by default.

   Given aliases and profiles are respectively appended to `:maestro/alias+` and `:maestro/profile+`.
   See [[search]] for more information about the net effect.
  
   Often used right after [[create-basis]]."


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



(defn ensure-basis

  "Returns the given argument if it contains `:aliases`.
   Otherwise, forwards it to [[create-basis]]."

  [maybe-basis]

  (if (:aliases maybe-basis)
    maybe-basis
    (merge maybe-basis
           (create-basis maybe-basis))))


;;;;;;;;;;


(defn- -on-require

  ;; Called by [[search]] at the for executing `:maestro/on-require` hooks.

  [basis]

  (transduce (comp (map (basis :aliases))
                   (mapcat :maestro/on-require))
             (completing (fn [basis-2 sym]
                           ((requiring-resolve sym) basis-2)))
             basis
             (basis :maestro/require)))



(defn- -search

  ;; Core implementation of [[search]].
  ;;
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

  "Given input aliases and profiles, under `:maestro/alias+` and `:maestro/profile+` respectively, searches
   for all necessary aliases and puts the results in a vector under `:maestro/require`.

   Input will go through [[ensure-basis]] first.

   Also remembers which profiles resulted in which aliases being selected under `:maestro/profile->alias+`.

   Alias data in `deps.edn` can also contain a vector of qualified symbols under `:maestro/on-require`. Those
   are resolved to functions and executed with the results at the very end if required.

   See the following namespaces for additional helpers:

   - [[protosens.maestro.aggr]] for expert users needing this function to do more
   - [[protosens.maestro.alias]]
   - [[protosens.maestro.profile]]"

  [basis]

  (let [profile+ (-> (:maestro/profile+ basis)
                     (vec)
                     (conj 'default))]
    (-> basis
        (ensure-basis)
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
                             (-> profile meta :direct?)))))
        (dissoc :maestro/seen+)
        (-on-require))))


;;;;;;;;;;


(defn by-profile+

  "Extracts a set of all aliases required in the context of the given collection of profiles.

   See [[search]]."
  
  [basis profile+]

  (reduce set/union
          (-> basis
              :maestro/profile->alias+
              (select-keys profile+)
              (vals))))


(defn not-by-profile+
  
  "Extracts a set of all aliases NOT required in the context of the given collection of profiles.

   Opposite of [[by-profile+]].

   See [[search]]."

  [basis profile+]

  (reduce set/union
          (-> (reduce (fn [profile->alias+ profile]
                        (dissoc profile->alias+
                                profile))
                      (basis :maestro/profile->alias+)
                      profile+)
              (vals))))


;;;;;;;;;;


(defn print

  "Prints aliases from `:maestro/require` after concatenating them, the way Clojure CLI likes it.
  
   See [[search]]."

  [basis]

  (-> (basis :maestro/require)
      ($.maestro.alias/stringify+)
      (clojure.core/print))
  basis)




(defn task

  "Like [[search]] but prepends aliases and profiles found using [[cli-arg]] and ends by [[print]]ing all required aliases.

   Commonly used as a Babashka task."


  ([]

   (task nil))


  ([basis]

   (let [from-cli (cli-arg {})]
     (-> basis
         ($.maestro.alias/prepend+ (from-cli :maestro/alias+))
         ($.maestro.profile/prepend+ (from-cli :maestro/profile+))
         (search)
         ((or (:maestro.task/finalize basis)
              print))))))
