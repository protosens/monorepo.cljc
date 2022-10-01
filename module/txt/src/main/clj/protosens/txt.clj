(ns protosens.txt

  "Collection of string manipulation utilities."

  (:refer-clojure :exclude [newline])
  (:require [clojure.string :as string]))


;;;;;;;;;;


(defn count-leading-space

  "Returns the number of whitespaces in the given string."

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
                           (map (fn [^String line]
                                  (cond->
                                    line
                                    (not (string/blank? line))
                                    (.substring n-truncate
                                                (count line))))
                                (rest line+))))))))




