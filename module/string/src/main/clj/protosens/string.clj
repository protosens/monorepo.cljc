(ns protosens.string

  "Collection of string manipulation utilities."

  (:refer-clojure :exclude [newline])
  (:require [clojure.string :as string]))


(declare trunc-left)


(set! *warn-on-reflection*
      true)


;;;;;;;;;;


(defn count-leading-space

  "Returns the number of whitespaces in the given string.
  
   More precisely, `\\space` characters."
  
  [s]

  (-> (take-while (fn [c]
                    (= c
                       \space))
                  s)
      (count)))



(defn cut-out

  "Returns the sub-string of `s` starting at `i-begin` (inclusive) and ending
   at `i-end` (exclusive)."

  [^String s i-begin i-end]

  (.substring s
              i-begin
              i-end))



(defn line+

  "Returns a vector of the `n` first lines in `s`.

   Default value for `n` is 1 million.

   Size of returned vector is at most `(+ n 1)` where the last item is the rest
   of the string (if any)."


  ([s]

   (line+ s
          nil))


  ([s n]

   (string/split s
                 #"\r?\n"
                 (inc (or n
                          1000000)))))



(defn n-first

  "Returns a sub-string of `s` composed of the N first characters."

  [s n-char]

  (cut-out s
           0
           n-char))



(defn n-last

  "Returns a sub-string of `s` composed of the N last characters."

  [s n-char]

  (let [total (count s)]
    (cut-out s
             (- total
                n-char)
             total)))



(defn newline

  "Returns the platform-dependend line separator."

  []

  (System/lineSeparator))



(defn realign

  "Realign all lines in the given string.
   Useful for printing multi-line EDN strings.
 
   More precisely:

   - Leading whitespace is truncated on the first line
   - Other lines are truncated by the smallest leading whitespace of them all
  
   Also see [[count-leading-space]]."

  [s]

  (let [line+ (string/split-lines s)]
    (if (<= (count line+)
            1)
      s
      (let [n-truncate (reduce min
                               (map count-leading-space
                                    (filter (comp not
                                                  string/blank?)
                                            (rest line+))))]
        (string/join (newline)
                     (cons (string/triml (first line+))
                           (map (fn [line]
                                  (cond->
                                    line
                                    (not (string/blank? line))
                                    (trunc-left n-truncate)))
                                (rest line+))))))))



(defn trunc-left

  "Truncates from the left.
  
   Returns the given `s`tring without the first `n` characters."

  [s n]

  (cut-out s
           n
           (count s)))



(defn trunc-right

  "Truncates from the right.
  
   Returns the given `s`tring without the last `n` characters."

  [s n]

  (cut-out s
           0
           (- (count s)
              n)))
