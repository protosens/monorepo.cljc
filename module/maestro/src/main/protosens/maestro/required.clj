(ns protosens.maestro.required

  "Using extra key-values on aliases from `deps.edn`, this namespace is able to extract dependencies between
   those aliases. In addition, some profiles can be activated or not so that some of those dependencies can be
   selectively enforced or ignored.

   A typical `deps.edn` would look like this:

   ```clojure
   {:aliases {:foo {:extra-paths     [\"...\"]
                    :maestro/require [:bar
                                      {some-profile :bar}]}
              :bar {:extra-paths [\"...\"]}
              :baz {:extra-paths [\"...\"]}}}
   ```

   In an alias, dependencies to other aliases figure in `:maestro/require`, a vector of:

   - Other aliases
   - Maps of `profile` -> `alias`

   A profile is usually a symbol used to designate aliases needed given some context. Projects usually have profiles
   such as `dev, `test`, etc. However, Maestro does not enforce any convention or naming besides the fact that a `default`
   profile is always activated.

   Hence, a profile can be used to ignore an alias dependency when not activated or select one among others.
   Order matters. Activated profiles are provided under `:maestro/profile+` in the basis (see [[create-basis]]).

   ```clojure
   ;; If profile `foo` is not activated, `:alias-1` will be required.
   ;;
   [{foo :alias-1}]

   ;; Selects which ever is activated.
   ;; If both are activated, selects the first one found in `:maestro/profile+`.
   ;;
   [{foo :alias-1
     bar :alias-2}]

   ;; The `default` profile is implicit.
   ;; This:
   [:alias-1]
   ;; Is functionally equivalent to:
   [{default :alias-1}]
   ```

   See [[create-basis]] and [[search]]."

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

   Given aliases and/or profiles are respectively appended to `:maestro/alias+` and `:maestro/profile+`.
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

  ;; Called by [[search]] at the end to for executing `:maestro/on-require` hooks.

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

  "Given a `basis` originally resulting from [[create-basis]], search for all required aliases by resolving
   everything thas is needed by given \"root\" aliases.

   In `basis`:

     - Root aliases must be provided as a vector under `:maestro/alias+`
     - Profiles to activate are optionally provided as a vector under `:maestro/profile+`
     - The result of all required aliases is a vector under `:maestro/require`
     - `:maestro/profile->alias+` is a map of `profile` -> `set of required aliases` keeping track for which
       profile each alias has been required
     - An alias may contain a vector of qualified symbols under `:maestro/on-require` that will be resolved
       and executed if that alias is required, passing the basis


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
        (dissoc :maestro/seen)
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

  "Prints aliases from `:maestro/require` after concatenating them, like Clojure CLI likes it.
  
   See [[search]]."

  [basis]

  (-> (basis :maestro/require)
      ($.maestro.alias/stringify+)
      (clojure.core/print))
  basis)




(defn task

  "Uses [[search]] on the argument but prepends aliases and profiles found using [[cli-arg]] and ends by printing all required aliases.
  
   Well suited for a task aimed to find aliases given some context (dev, testing, etc)."


  ([]

   (task nil))


  ([basis]

   (let [from-cli (cli-arg {})]
     (-> basis
         ($.maestro.alias/prepend+ (from-cli :maestro/alias+))
         ($.maestro.profile/prepend+ (from-cli :maestro/profile+))
         (search)
         (print)))))
