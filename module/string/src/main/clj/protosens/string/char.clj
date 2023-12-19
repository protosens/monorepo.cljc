(ns protosens.string.char

  "Operations related to characters in strings."

  (:refer-clojure :exclude [first
                            last]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;;


(defn at

  "Returns the character in `string` at index `i`.
  
   Throws if `i` is negative."

  [^CharSequence string i]

  (when (< i
           (.length string))
    (.charAt string
             i)))



(defn at?

  "Returns true is character at `i` in `string` equals given character `c`."

  [string i c]

  (= (at string
         i)
     c))



(defn at-end

  "Like [[at]] but works in the opposite direction, starting from the
   end of `string`.

   E.g. When `i` is `0`, returns the last character.
  
   Throws if `i` is out of range."

  [^CharSequence string i]

  (let [i-2 (- (count string)
               (inc i))]
    (if (neg? i-2)
      nil
      (.charAt string
               i-2))))



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
