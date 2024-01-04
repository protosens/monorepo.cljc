(ns protosens.test.calver

  (:import (java.time LocalDateTime
                      ZoneOffset))
  (:refer-clojure :exclude [format])
  (:require [clojure.test     :as T]
            [protosens.calver :as $.calver]))


;;;;;;;;;;


(T/deftest format

  (T/is (string? ($.calver/format)))

  (T/is (= "2024.01.03/10h32"
           (-> (LocalDateTime/of 2024
                                 01
                                 03
                                 10
                                 32)
               (.atZone ZoneOffset/UTC)
               (.toInstant)
               ($.calver/format)))))
