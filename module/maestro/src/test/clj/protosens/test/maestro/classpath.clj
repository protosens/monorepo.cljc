(ns protosens.test.maestro.classpath

  (:require [clojure.test                :as T]
            [protosens.maestro.classpath :as $.maestro.classpath]))


;;;;;;;;;;

(T/deftest pprint

  (T/is (= (str \a \newline \b \newline \c \newline \d \newline)
           (with-out-str
             ($.maestro.classpath/pprint "a:b:c:d")))))
