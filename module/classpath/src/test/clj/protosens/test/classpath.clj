(ns protosens.test.classpath

  (:require [clojure.test        :as T]
            [protosens.classpath :as $.classpath]))


;;;;;;;;;;

(T/deftest pprint

  (T/is (= (str \a \newline \b \newline \c \newline \d \newline)
           (with-out-str
             ($.classpath/pprint "a:b:c:d")))))
