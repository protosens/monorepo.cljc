(ns protosens.string.char

  "Operations related to characters in strings.")


;;;;;;;;;;


(defn at

  "Returns the character in `string` at index `i`.
  
   Throws if `i` is out of range."

  [^String string i]

  (.charAt string
           i))



(defn at-end

  "Like [[at]] but works in the opposite direction, starting from the
   end of `string`.

   E.g. When `i` is `0`, returns the last character.
  
   Throws if `i` is out of range."

  [^String string i]

  (at string
      (- (count string)
         (inc i))))
