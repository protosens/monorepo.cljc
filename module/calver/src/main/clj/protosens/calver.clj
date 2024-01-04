(ns protosens.calver

  (:import (java.time Instant
                      ZoneOffset)
           (java.time.format DateTimeFormatter)))


(set! *warn-on-reflection*
      true)


;;;;;;;;;; Private


(def ^:private ^DateTimeFormatter -formatter

  (-> (DateTimeFormatter/ofPattern "yyyy.MM.dd/HH'h'mm")
      (.withZone ZoneOffset/UTC)))


;;;;;;; Public


(defn instant->version

  [^Instant instant]

  (.format -formatter
           (or instant
               (Instant/now))))



(defn now

  []

  (instant->version (Instant/now)))
