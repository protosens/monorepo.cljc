(ns protosens.maestro.alias

  (:require [clojure.string         :as string]
            [protosens.maestro.util :as $.maestro.util]))


;;;;;;;;;;


(defn append+

  [basis alias+]

  ($.maestro.util/append-at basis
                            :maestro/alias+
                            alias+))


(defn prepend+
  
  [basis alias+]

  ($.maestro.util/prepend-at basis
                             :maestro/alias+
                             alias+))


;;;;;;;;;;


(defn extra-path+

  [basis alias+]

  (mapcat (comp :extra-paths
                (basis :aliases))
          alias+))


(defn stringify+

  [alias+]

  (string/join alias+))
