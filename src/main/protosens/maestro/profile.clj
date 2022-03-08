(ns protosens.maestro.profile
  (:require [protosens.maestro.util :as $.maestro.util]))


;;;;;;;;;;


(defn append+

  [basis alias+]

  ($.maestro.util/append-at basis
                           :maestro/profile+
                    alias+))


(defn prepend+
  
  [basis alias+]

  ($.maestro.util/prepend-at basis
                             :maestro/profile+
                             alias+))
