(ns protosens.calver

  (:import (java.time Instant
                      ZoneOffset)
           (java.time.format DateTimeFormatter))
  (:refer-clojure :exclude [format])
  (:require [protosens.git     :as $.git]
            [protosens.process :as $.process]
            [protosens.string  :as $.string]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;; Private


(def ^:private ^DateTimeFormatter -formatter

  (-> (DateTimeFormatter/ofPattern "yyyy.MM.dd/HH'h'mm")
      (.withZone ZoneOffset/UTC)))


;;;;;;; Public


(defn latest

  []

  (when-some [sha (-> ($.git/exec ["rev-list"
                                   "--tags=release/*"
                                   "--max-count=1"])
                      ($.process/out))]
    [sha
     (-> ($.git/exec ["describe"
                      "--tags"
                      sha])
         ($.process/out))]))



(defn format


  ([]

   (format nil))


  ([^Instant instant]

   (.format -formatter
            (or instant
                (Instant/now)))))



(defn tag


  ([]

   (tag nil))


  ([formatted]

   ($.git/tag-add (str "release/"
                       (or formatted
                           (format))))))
