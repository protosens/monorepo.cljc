(ns protosens.txt

  (:refer-clojure :exclude [newline])
  (:require [clojure.string :as string]))


;;;;;;;;;;


(defn count-leading-space

  [s]

  (-> (take-while (fn [c]
                    (= c
                       \space))
                  s)
      (count)))



(defn newline

  []

  (System/lineSeparator))



(defn realign

  ;; Realign all lines relative to the first one by truncating leading whitespace.
  ;; Useful for printing multi-line EDN strings.

  [s]

  (let [line+ (string/split-lines s)]
    (if (= (count line+)
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
