(ns protosens.test.classpath

  (:require [clojure.test        :as T]
            [protosens.classpath :as $.classpath]))


;;;;;;;;;;


(T/deftest compute

  (T/is (string? ($.classpath/compute)))

  (T/is (string? ($.classpath/compute [:ext/kaocha
                                       :module/maestro]))))



(T/deftest current

  (T/is (string? ($.classpath/current)))

  (T/is (seq ($.classpath/split ($.classpath/current)))))



(T/deftest split

  (T/is (= '("foo"
             "bar")
           ($.classpath/split (format "foo%sbar"
                                      ($.classpath/separator))))))
