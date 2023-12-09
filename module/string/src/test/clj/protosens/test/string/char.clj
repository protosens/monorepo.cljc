(ns protosens.test.string.char

  (:require [clojure.test          :as T]
            [protosens.string.char :as $.string.char]))


;;;;;;;;;;


(T/deftest at

  (T/is (= \b
           ($.string.char/at "bar"
                             0))
        "Existing index")

  (T/is (thrown? Exception
                 ($.string.char/at "bar"
                                   -1))
        "Negative index")

  (T/is (thrown? Exception
                 ($.string.char/at "bar"
                                   10))
        "Outside of range"))
