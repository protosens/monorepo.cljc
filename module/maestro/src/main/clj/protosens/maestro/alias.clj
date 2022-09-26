(ns protosens.maestro.alias

  "Miscellaneous helpers centered around aliases.
  
   See the [[protosens.maestro]] namespace."

  (:require [clojure.string         :as string]
            [protosens.maestro.util :as $.maestro.util]))


;;;;;;;;;;


(defn append+

  "In `basis`, add the given aliases as root aliases to resolve by appending them to
   any existing ones."

  [basis alias+]

  ($.maestro.util/append-at basis
                            :maestro/alias+
                            alias+))


(defn prepend+

  "In `basis`, add the given aliases as root aliases to resolve by prepending them to
   any existing ones."
  
  [basis alias+]

  ($.maestro.util/prepend-at basis
                             :maestro/alias+
                             alias+))


;;;;;;;;;;


(defn extra-path+

  "Extracts a list of all paths provided in `:extra-paths` for the given list of aliases.

   Notable use-cases are:

     - Working with [tools.build](https://clojure.org/guides/tools_build)
     - Fetching test paths for tests runners like [Kaocha](https://github.com/lambdaisland/kaocha)"

  [basis alias+]

  (mapcat (comp :extra-paths
                (basis :aliases))
          alias+))


(defn stringify+

  "Stringifies the given collection of aliases by concatenating them, just like Clojure CLI likes it."

  [alias+]

  (string/join alias+))
