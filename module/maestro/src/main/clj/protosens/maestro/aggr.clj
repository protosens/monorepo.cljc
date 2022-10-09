(ns protosens.maestro.aggr

  "Altering what is collected when searching for required aliases.
  
   When running [[protosens.maestro/search]], `basis` can contain an extra key `:maestro/aggr`
   pointing to a function such as `(fn [basis alias alias-data] basis-2)`.
  
   By default, this function is [[default]]. Technically, power users can provided an alternative implementation
   for additional features."
  
  (:refer-clojure :exclude [alias]))


;;;;;;;;;;


(defn alias

  "In `basis`, appends `alias` under `:maestro/require`."


  ([basis alias]

   (update basis
           :maestro/require
           conj
           alias))


  ([basis alias _alias-data]

   (protosens.maestro.aggr/alias basis
                                 alias)))



(defn env

  "Merges `:maestro/env` from `alias-data` into `:maestro/env` in `basis`.

   Those are typically used to represent environment variables and become useful
   when executing a process. For instance, sell utilities in Babashka accepts such
   a map of environment variables."


  ([basis alias-data]

   (update basis
           :maestro/env
           merge
           (:maestro/env alias-data)))


  ([basis _alias alias-data]

   (env basis
        alias-data)))


;;;


(defn default

  "Default alias aggregating function.
  
   As used by [[protosens.maestro/search]] unless overwritten by the user.

   Uses:

   - [[alias]]
   - [[env]]"


  ([basis alias]

   (default basis
            alias
            (get-in basis
                    [:aliases
                     alias])))


  ([basis alias alias-data]

   (-> basis
       (protosens.maestro.aggr/alias alias)
       (env alias-data))))
