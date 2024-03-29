(ns protosens.maestro

  "See [README](./) about core principles.
 
   [[search]] is the star of this namespace and exemplifies the Maestro philosophy.
  
   To understand the notion of a \"basis\", see [[create-basis]] and [[ensure-basis]]."

  (:import (java.io PushbackReader))
  (:require [clojure.edn            :as edn]
            [clojure.java.io        :as java.io]
            [clojure.set            :as set]
            [clojure.string         :as string]
            [protosens.maestro.aggr :as $.maestro.aggr]
            [protosens.string       :as $.string]))


(declare fail-mode)


(set! *warn-on-reflection*
      true)


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

  (let [^String message-2 ($.string/realign message)]
    (case (fail-mode)
      :exit
      (binding [*out* *err*]
        (println message-2)
        (System/exit 1))
      ;;
      :throw
      (throw (Exception. message-2)))))



(defn fail-mode

  "How failure is handled.

   See [[fail]].
  
   There are 2 modes:

   - `:exit` is usually prefered on Babashka ; error message is printed and process exits with code 1
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


;;;;;;;;;; Basis


(defn create-basis

  "Reads and prepares a `deps.edn` file.
  
   The result is called a `basis` for consistency with other Clojure libraries.

   Options may be:

   | Key                | Value                                 | Default          |
   |--------------------|---------------------------------------|------------------|
   | `:maestro/project` | Alternative path to a `deps.edn` file | `\"./deps.edn\"` |"

  ;; In theory, this could use `$.deps.edn/read`.
  ;; In practice, it breaks `bb genesis`.


  ([]

   (create-basis nil))


  ([option+]
   
   (with-open [reader (-> (or (:maestro/project option+)
                              "./deps.edn")
                          (java.io/reader)
                          (PushbackReader.))]
     (edn/read reader))))



(defn ensure-basis

  "Reads the basis from disk if necessary and merge everything.

   The term \"proto-basis\" denotes that it may already be a proper basis or that it may
   contain key-values to merge to basis to read from disk.

   It is commonly used by many Maestro-related utilities as a convenience for users.

   In practice, [[create-basis]] is called if `:aliases` are missing."

  [proto-basis]

  (if (:aliases proto-basis)
    proto-basis
    (merge (create-basis proto-basis)
           proto-basis)))


;;;;;;;;;; Searching


(defn- -on-require

  ;; Called by [[search]] when executing `:maestro/on-require` hooks after the search.

  [basis]

  (transduce (comp (map (basis :aliases))
                   (mapcat :maestro/on-require))
             (completing (fn [basis-2 hook]
                           ((cond->
                              hook
                              (not (fn? hook))
                              (requiring-resolve))
                            basis-2)))
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

  "Searches for all required aliases.

   Namely, the full chain of aliases required by the aliases provided unde `:maestro/alias+` while activating
   profiles provided under `:maestro/profile+`.

   High-level steps are:

   - Pass `proto-basis` through [[ensure-basis]]
   - Apply `:maestro/mode` (if any)
   - Search for all required aliases, put vector result under `:maestro/require`
   - Under `:maestro/profile->alias+`, remember which profiles result in which aliases being selected
   - Execute hooks provided in `:maestro/on-require` of required aliase
   - Return the whole basis with everything

   Also see:

   - [[protosens.maestro.aggr]] for expert users needing this function to do more
   - [[main]] for doing a search conveniently as a task (perfect for Babashka)"

  [proto-basis]

  (let [mode           (:maestro/mode proto-basis)
        basis          (ensure-basis proto-basis)
        mode-2         (when mode
                         (or (get-in basis
                                     [:maestro/mode+
                                      mode])
                             (fail (str "Maestro mode not found in basis: "
                                        mode))))
        basis-2        (cond->
                         basis
                         mode-2
                         (-> (update :maestro/alias+
                                     #(into (vec %)
                                            (mode-2 :maestro/alias+)))
                             (update :maestro/profile+
                                     #(into (vec %)
                                            (mode-2 :maestro/profile+)))))
        profile+       (-> (basis-2 :maestro/profile+)
                           (vec)
                           (conj 'default))
        alias-request+ (set (:maestro/alias+ basis))]
    (-> basis-2
        (assoc :maestro/profile+
               profile+)
        (update :maestro/require
                #(or %
                     []))
        (-search nil
                 0
                 (or (basis-2 :maestro/aggr)
                     $.maestro.aggr/default)
                 (basis-2 :maestro/alias+)
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

  "Extracts a set of all required aliases selected by the given profiles.

   Call only after [[search]]."
  
  [basis profile+]

  (reduce set/union
          (-> (basis :maestro/profile->alias+)
              (select-keys profile+)
              (vals))))



(defn not-by-profile+
  
  "Extracts a set of all required aliases NOT selected by the given profiles.

   Opposite of [[by-profile+]].

   Call only after [[search]]."

  [basis profile+]

  (reduce set/union
          (-> (reduce (fn [profile->alias+ profile]
                        (dissoc profile->alias+
                                profile))
                      (basis :maestro/profile->alias+)
                      profile+)
              (vals))))



(defn stringify-required

  "Stringifies concatenated aliases from `:maestro/require`.

   Just like Clojure CLI likes it.

   See [[search]]."

  [basis]

  (string/join (basis :maestro/require)))


;;;;;;;;; Task


(defn cli-arg+

  "Processes CLI arguments in a commonly needed way.

   [[main]] is one example of a function that requires CLI arguments to be processed like so.

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


  ([proto-basis]

   (cli-arg+ proto-basis
             nil))


  ([proto-basis arg+]

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
     (-> proto-basis
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



(defn main

  "Task searching and printing all required aliases.
  
   High level steps:
  
   - Handle CLI arguments with [[cli-arg+]]
   - Call [[search]]
   - Print required aliases (or pass result to function under `:maestro.task/finalize` if present)

   Commonly used as a Babashka task. The output is especially useful in combination with Clojure CLI by
   leveraring shell substitution (e.g. `$()`) to insert aliases under `-M` and friends."


  ([]

   (main nil))


  ([proto-basis]

   (-> proto-basis
       (cli-arg+)
       (search)
       ((or (:maestro.task/finalize proto-basis)
            (comp print
                  stringify-required))))))
