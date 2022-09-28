(ns protosens.maestro

  "See README about core principles, [[search]] being the star of this namespace."

  (:import (java.io PushbackReader))
  (:refer-clojure :exclude [print])
  (:require [clojure.edn               :as edn]
            [clojure.java.io           :as java.io]
            [clojure.set               :as set]
            [clojure.string            :as string]
            [protosens.maestro.alias   :as $.maestro.alias]
            [protosens.maestro.aggr    :as $.maestro.aggr]
            [protosens.maestro.profile :as $.maestro.profile]
            [protosens.maestro.util    :as $.maestro.util]))


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



(defn ensure-basis

  "Returns the given argument if it contains `:aliases`.
   Otherwise, forwards it to [[create-basis]]."

  [maybe-basis]

  (if (:aliases maybe-basis)
    maybe-basis
    (merge (create-basis maybe-basis)
           maybe-basis)))



(defn sort-arg

  "Sorts aliases and vectors into a map of `:maestro/alias+` and `:maestro/profile+`.

   The map in question is often a basis (see [[create-basis]]).

   `arg` can be a vector to sort out or a single item. Useful for parsing aliases and
   profiles provided as a CLI argument."


  ([arg]

   (sort-arg nil
             arg))


  ([hmap arg]

   (reduce (fn [hmap-2 x]
             (update hmap-2
                     (cond
                       (keyword? x) :maestro/alias+
                       (symbol? x)  :maestro/profile+
                       :else        (throw (ex-info "Not an alias nor a profile"
                                                    {:maestro/arg x})))
                     (fnil conj
                           [])
                     x))
           hmap
           (if (vector? arg)
             arg
             [arg]))))


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

   Then, will apply the mode found under `:maestro/mode` if any. Modes are described in the basis under
   `:maestro/mode+`, a map of where keys are modes (typically keywords) and values can contain optional
   `:maestro/alias+` and `:maestro/profile+` to append before starting the search.

   Also remembers which profiles resulted in which aliases being selected under `:maestro/profile->alias+`.

   Alias data in `deps.edn` can also contain a vector of qualified symbols under `:maestro/on-require`. Those
   are resolved to functions and executed with the results at the very end if required.

   See the following namespaces for additional helpers:

   - [[protosens.maestro.aggr]] for expert users needing this function to do more
   - [[protosens.maestro.alias]]
   - [[protosens.maestro.profile]]"

  [basis]

  (let [mode     (:maestro/mode basis)
        basis-2  (ensure-basis basis)
        mode-2   (when mode
                   (or (get-in basis-2
                               [:maestro/mode+
                                mode])
                       (throw (Exception. (str "Maestro mode not found in basis: "
                                               mode)))))
        basis-3  (cond->
                   basis-2
                   mode-2
                   (-> ($.maestro.alias/append+ (mode-2 :maestro/alias+))
                       ($.maestro.profile/append+ (mode-2 :maestro/profile+))))
        profile+ (-> (basis-3 :maestro/profile+)
                     (vec)
                     (conj 'default))]
    (-> basis-3
        (assoc :maestro/profile+
               profile+)
        (update :maestro/require
                #(or %
                     []))
        (-search 0
                 (or (basis-3 :maestro/aggr)
                     $.maestro.aggr/default)
                 (basis-3 :maestro/alias+)
                 profile+
                 (fn [_basis depth profile]
                   (not (and (> depth 
                                1)
                             (-> profile
                                 (meta)
                                 (:direct?))))))
        (dissoc :maestro/seen+)
        (-on-require))))


;;;;;;;;;; Working with post-search results


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


(defn print

  "Prints aliases from `:maestro/require` after concatenating them, the way Clojure CLI likes it.
  
   See [[search]]."

  [basis]

  (-> (basis :maestro/require)
      ($.maestro.alias/stringify+)
      (clojure.core/print))
  basis)


;;;;;;;;; Meant for Babashka


(defn task

  "Like [[search]] but prepends aliases and profiles found using [[cli-arg]] and ends by [[print]]ing all required aliases.

   Commonly used as a Babashka task."


  ([]

   (task nil))


  ([basis]

   (let [n-arg       (count *command-line-args*)
         _           (assert (<= 1
                                 n-arg
                                 2))
         basis-proto (sort-arg (edn/read-string (last *command-line-args*)))
         mode        (when (= n-arg
                              2)
                       (edn/read-string (first *command-line-args*)))]
     (-> basis
         ($.maestro.alias/prepend+ (basis-proto :maestro/alias+))
         ($.maestro.profile/prepend+ (basis-proto :maestro/profile+))
         (cond->
           mode
           (assoc :maestro/mode
                  mode))
         (search)
         ((or (:maestro.task/finalize basis)
              print))))))


;;;;;;;;;; Interacting directly with Clojure CLI


(defn clojure

  "Executes the `clojure` command with `-?` (-M, -X, ...)

   Behaves like [[task]] but instead of printing aliases, there are appended
   to `-?`.

   CLI arguments are split in 2 if there is a `--` argument. What is before
   it will be applied as CLI arguments for [[task]]. Anything after it will
   be feed as additional CLI arguments for the `clojure` command.

   ```clojure
   ;; E.g. CLI args like:  :some/module -- -m some.namespace 1 2 3 
   (clojure \"-M\")
   ```

   The `basis` argument is forwarded to [[task]].

   Works only with Babashka."


  ([-?]

   (clojure -?
            nil))


  ([-? basis]

   (let [[for-task
          [_--
           & for-clojure]] (split-with (fn [arg]
                                         (not= arg
                                               "--"))
                                       *command-line-args*)]
     (@$.maestro.util/d*clojure (str -?
                                     (binding [*command-line-args* for-task]
                                       (-> basis
                                           (assoc :maestro.task/finalize
                                                  (comp $.maestro.alias/stringify+
                                                        :maestro/require))
                                           (task)))
                                     " "
                                     (string/join " "
                                                  for-clojure))))))
