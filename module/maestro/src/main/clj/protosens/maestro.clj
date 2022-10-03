(ns protosens.maestro

  "See README about core principles, [[search]] being the star of this namespace."

  (:import (java.io PushbackReader))
  (:refer-clojure :exclude [print])
  (:require [clojure.edn             :as edn]
            [clojure.java.io         :as java.io]
            [clojure.set             :as set]
            [protosens.maestro.alias :as $.maestro.alias]
            [protosens.maestro.aggr  :as $.maestro.aggr]
            [protosens.string        :as $.string]))


(declare fail-mode)


;;;;;;;;;; Handling failure


(def ^:private -*fail-mode

  ;; Decides how failure is handled.

  (atom :exit))



(defn fail

  "Fails with the given error `message`.

   Plugin authors and such should use this function to guarantee consistent behavior.

   Re-aligns multiline strings.
  
   See [[fail-mode]]."

  [message]

  (let [message-2 ($.string/realign message)]
    (case (fail-mode)
      :exit
      (binding [*out* *err*]
        (println message-2)
        (System/exit 1))
      ;;
      :throw
      (throw (Exception. message-2)))))



(defn fail-mode

  "How [[fail]] behaves.
  
   There are 2 modes:

   - `:exit` is usually prefered on Babashka ; error message is printed and process exits with 1
   - `:throw` might be preferred on the JVM ; an exception is thrown with the error message

   Sets behavior to the given `mode`.
   Without argument, returns the current one (default is `:exit`)."


  ([]

   (deref -*fail-mode))


  ([mode]

   (when (not (contains? #{:exit
                           :throw}
                         mode))
     (fail (str "Unknown fail mode: "
                mode)))
   (reset! -*fail-mode
           mode)))


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
  ;; TODO. Refactor this argument-wise.
  ;;       Grew organically, probably just put everything in `basis`.
  ;;
  ;; TODO. Consider converting to breadth-first.

  [basis alias-parent depth aggr alias+ profile+ consider-profile?]

  (reduce (fn [basis-2 alias]
            (let [[alias-2
                   profile] (if (map? alias)
                              (let [profile->alias alias]
                                (some (fn [profile]
                                        (when (consider-profile? basis
                                                                 alias-parent
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
                                     alias-2
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
   - [[protosens.maestro.alias]]"

  [basis]

  (let [mode           (:maestro/mode basis)
        basis-2        (ensure-basis basis)
        mode-2         (when mode
                         (or (get-in basis-2
                                     [:maestro/mode+
                                      mode])
                             (fail (str "Maestro mode not found in basis: "
                                        mode))))
        basis-3        (cond->
                         basis-2
                         mode-2
                         (-> (update :maestro/alias+
                                     #(into (vec %)
                                            (mode-2 :maestro/alias+)))
                             (update :maestro/profile+
                                     #(into (vec %)
                                            (mode-2 :maestro/profile+)))))
        profile+       (-> (basis-3 :maestro/profile+)
                           (vec)
                           (conj 'default))
        alias-request+ (set (:maestro/alias+ basis))]
    (-> basis-3
        (assoc :maestro/profile+
               profile+)
        (update :maestro/require
                #(or %
                     []))
        (-search nil
                 0
                 (or (basis-3 :maestro/aggr)
                     $.maestro.aggr/default)
                 (basis-3 :maestro/alias+)
                 profile+
                 (fn [_basis alias-parent depth profile]
                   (or (<= depth
                           1)
                       (not (-> profile
                                (meta)
                                (:direct?)))
                       (contains? alias-request+
                                  alias-parent))))
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


(defn cli-arg+

  "Processes CLI arguments in a commonly needed way.

   [[task]] is one example of a function that requires CLI arguments to be processed like so.

   Aliases and profiles are sorted and prepended to `:maestro/alias+` and `:maestro/profile+` respectively.
   
   The last argument can be an alias or a profile:

   ```
   :some-alias

   some-profile
   ```

   Or a vector combining any number of them:

   ```
   '[profile-foo alias-a alias-b profile-bar]'
   ```

   If there are 2 arguments, the first one is interpreted as a `:maestro/mode` (see [[search]]):

   ```
   :some-mode '[profile-foo alias-a alias-b profile-bar]'
   ```
  
   `arg+` defaults to `*command-line-args*`."


  ([basis]

   (cli-arg+ basis
             nil))


  ([basis arg+]

   (let [arg-2+   (map edn/read-string
                       (or arg+
                           *command-line-args*))
         n-arg    (count arg-2+)
         arg-last (last arg-2+)
         sorted   (reduce (fn [hmap x]
                            (update hmap
                                    (cond
                                      (keyword? x) :maestro/alias+
                                      (symbol? x)  :maestro/profile+
                                      :else        (fail (str "Not an alias nor a profile: "
                                                              x)))
                                    (fnil conj
                                          [])
                                    x))
                          {}
                          (if (vector? arg-last)
                            arg-last
                            [arg-last]))
         mode   (when (= n-arg
                         2)
                  (first arg-2+))]
     (-> basis
         (update :maestro/alias+
                 #(into (sorted :maestro/alias+)
                        %))
         (update :maestro/profile+
                 #(into (sorted :maestro/profile+)
                        %))
         (cond->
           mode
           (assoc :maestro/mode
                  mode))))))



(defn task

  "Like [[search]] but prepends aliases and profiles found using [[cli-arg]] and ends by [[print]]ing all required aliases.

   Commonly used as a Babashka task. The output is especially useful in combination with Clojure CLI by leveraring shell
   substitution (e.g. `$()`)."


  ([]

   (task nil))


  ([basis]

   (-> basis
       (cli-arg+)
       (search)
       ((or (:maestro.task/finalize basis)
            print)))))

