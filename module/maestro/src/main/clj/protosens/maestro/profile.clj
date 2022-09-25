(ns protosens.maestro.profile

  "Miscellaneous helpers centered around profiles.
  
   See the [[protosens.maestro]] namespace."

  (:require [protosens.maestro.util :as $.maestro.util]))


;;;;;;;;;;


(defn append+

  "In `basis`, activates the given profiles by appending them to any existing ones."

  [basis profile+]

  ($.maestro.util/append-at basis
                           :maestro/profile+
                           profile+))


(defn prepend+
  
  "In `basis`, activates the given profiles by prepending them to any existing ones."

  [basis profile+]

  ($.maestro.util/prepend-at basis
                             :maestro/profile+
                             profile+))
