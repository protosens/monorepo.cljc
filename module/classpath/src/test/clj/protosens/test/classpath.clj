(ns protosens.test.classpath

  (:require [clojure.test        :as T]
            [protosens.classpath :as $.classpath]
            [protosens.string    :as $.string]))


;;;;;;;;;;


(T/deftest current

  (T/is (string? ($.classpath/current)))

  (T/is (seq ($.classpath/split ($.classpath/current)))))



(T/deftest split

  (T/is (= '("foo"
             "bar")
           ($.classpath/split (format "foo%sbar"
                                      ($.classpath/separator))))))
