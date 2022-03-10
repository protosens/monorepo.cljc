(ns protosens.maestro.aggr
  
  (:refer-clojure :exclude [alias]))


;;;;;;;;;;


(defn alias

  "In `basis`, adds `alias` under :`maestro/require`."


  ([basis alias]

   (update basis
           :maestro/require
           conj
           alias))


  ([basis alias _alias-data]

   (protosens.maestro.aggr/alias basis
                                 alias)))



(defn env

  "Merge `:maestro/env` from alias `data` into `:maestro/env` in `basis`.

   Those are typically used to represent environment variables."


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

  "Default alias aggregating function for [[protosens.maestro/walk]].

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
