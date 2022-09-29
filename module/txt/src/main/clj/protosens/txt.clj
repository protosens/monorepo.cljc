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



(defn newline

  "Returns the platform-dependend line separator."

  []

  (System/lineSeparator))



(defn realign

  "Realign all lines in the given string.
 
   Relative to the first one by truncating the smallest leading whitespace in subsequent once.
   Useful for printing multi-line EDN strings.
  
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
                     (cons (first line+)
                           (map (fn [^String line]
                                  (cond->
                                    line
                                    (not (string/blank? line))
                                    (.substring n-truncate
                                                (count line))))
                                (rest line+))))))))
