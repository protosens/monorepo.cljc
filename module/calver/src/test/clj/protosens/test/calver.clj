(ns protosens.test.calver

  (:import (java.time LocalDateTime
                      ZoneOffset))
  (:require [clojure.test     :as T]
            [protosens.calver :as $.calver]))


;;;;;;;;;;


(T/deftest instant->version

  (T/is (= "2024.01.03/10h32"
           (-> (LocalDateTime/of 2024
                                 01
                                 03
                                 10
                                 32)
               (.atZone ZoneOffset/UTC)
               (.toInstant)
               ($.calver/instant->version)))))



(T/deftest now

  (T/is (string? ($.calver/now))))
