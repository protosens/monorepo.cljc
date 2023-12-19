(ns protosens.string.char

  "Operations related to characters in strings."

  (:refer-clojure :exclude [first
                            last]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;;


(defn at

  "Returns the character in `string` at index `i`.
  
   Throws if `i` is out of range."

  [^String string i]

  (.charAt string
           i))



(defn at?

  "Returns true is character at `i` in `string` equals given character `c`."

  [^String string i c]

  (= (at string
         i)
     c))



(defn at-end

  "Like [[at]] but works in the opposite direction, starting from the
   end of `string`.

   E.g. When `i` is `0`, returns the last character.
  
   Throws if `i` is out of range."

  [string i]

  (at string
      (- (count string)
         (inc i))))



(defn first

  "Returns the first character of `string`.
  
   Throws if `string` is empty."

  [string]

  (at string
      0))



(defn last

  "Returns the last character of `string`.
  
   Throws if `string` is empty."

  [string]

  (at-end string
          0))
