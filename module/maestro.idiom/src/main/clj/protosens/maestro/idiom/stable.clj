(ns protosens.maestro.idiom.stable

  (:import (java.time LocalDateTime)
           (java.time.format DateTimeFormatter))
  (:require [clojure.string :as string]
            [protosens.git  :as $.git]))


(declare today)


;;;;;;;;;; Private


(def ^:private ^DateTimeFormatter -dtf

  ;;

  (DateTimeFormatter/ofPattern "yyyy-MM-dd"))


;;;;;;;;;; Public


(defn all

  []

  (filter (fn [tag]
            (string/starts-with? tag
                                 "stable/"))
          ($.git/tag+)))



(defn latest


  ([]

   (latest nil))


  ([tag+]

   (some->> (not-empty (or tag+
                           (all)))
            (reduce (fn [acc tag]
                      (if (pos? (compare tag
                                         acc))
                        tag
                        acc))))))



(defn tag

  []

  (let [t (today)]
    ($.git/tag-add t)
    t))



(defn tag->date

  [tag]

  (second (string/split tag
                        #"/"
                        2)))



(defn today

  []

  (let [tag (str "stable/"
                 (.format -dtf
                          (LocalDateTime/now)))]
    (if ($.git/resolve tag)
      (loop [i 2]
        (let [tag-2 (format "%s_%02d"
                            tag
                            i)]
          (if ($.git/resolve tag-2)
            (recur (inc i))
            tag-2)))
      tag)))
