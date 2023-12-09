(ns protosens.test.string.char

  (:refer-clojure :exclude [last])
  (:require [clojure.test          :as T]
            [protosens.string.char :as $.string.char]))


;;;;;;;;;;


(T/deftest at

  (T/is (= \b
           ($.string.char/at "bar"
                             0))
        "In range")

  (T/is (thrown? Exception
                 ($.string.char/at "bar"
                                   -1))
        "Negative index")

  (T/is (thrown? Exception
                 ($.string.char/at "bar"
                                   1000))
        "Out of range"))



(T/deftest at-end

  (T/is (= \b
           ($.string.char/at-end "foobar"
                                 2))
        "In range")

  (T/is (thrown? Exception
                 ($.string.char/at-end "foobar"
                                       -1))
        "Negative index")

  (T/is (thrown? Exception
                 ($.string.char/at-end "foobar"
                                       1000))
        "Out of range"))



(T/deftest last

  (T/is (= \r
           ($.string.char/last "bar"))
        "Non-empty string")

  (T/is (thrown? Exception
                 ($.string.char/last ""))
        "Empty string"))
