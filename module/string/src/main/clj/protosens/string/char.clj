(ns protosens.string.char

  "Operations related to characters in strings."

  (:refer-clojure :exclude [last]))


;;;;;;;;;;


(defn at

  "Returns the character in `string` at index `i`."

  [^String string i]

  (.charAt string
           i))
