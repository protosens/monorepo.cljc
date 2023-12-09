(ns protosens.test.string.char

  (:refer-clojure :exclude [first
                            last])
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
        "Negative index fails")

  (T/is (thrown? Exception
                 ($.string.char/at "bar"
                                   1000))
        "Out of range fails"))



(T/deftest at-end

  (T/is (= \b
           ($.string.char/at-end "foobar"
                                 2))
        "In range")

  (T/is (thrown? Exception
                 ($.string.char/at-end "foobar"
                                       -1))
        "Negative index fails")

  (T/is (thrown? Exception
                 ($.string.char/at-end "foobar"
                                       1000))
        "Out of range fails"))



(T/deftest first

  (T/is (= \b
           ($.string.char/first "bar"))
        "Non-empty string")

  (T/is (thrown? Exception
                 ($.string.char/first ""))
        "Empty string fails"))



(T/deftest last

  (T/is (= \r
           ($.string.char/last "bar"))
        "Non-empty string")

  (T/is (thrown? Exception
                 ($.string.char/last ""))
        "Empty string fails"))
