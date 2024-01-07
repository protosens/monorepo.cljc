(ns protosens.test.string.char

  (:refer-clojure :exclude [first
                            last])
  (:require [clojure.test          :as T]
            [protosens.string.char :as $.string.char]
            [protosens.test.string :as $.test.string]))


;;;;;;;;;; Tests


(T/deftest at

  (T/is (= \b
           ($.string.char/at "bar"
                             0))
        "In range")

  (T/is (nil? ($.string.char/at "bar"
                                1000))
        "Beyond")

  (T/is ($.test.string/bound*
          ($.string.char/at "bar"
                            -1))
        "Negative index fails"))



(T/deftest at?

  (T/is ($.string.char/at? "foo bar"
                           4
                           \b)
        "Success")

  (T/is (false? ($.string.char/at? "foo bar"
                                   3
                                   \b))
        "Failure")
  
  (T/is (false? ($.string.char/at? "foo bar"
                                   10000
                                   \b))
        "Beyond"))



(T/deftest at-end

  (T/is (= \b
           ($.string.char/at-end "foo bar"
                                 2))
        "In range")

  (T/is (nil? ($.string.char/at-end "foo bar"
                                    1000))
        "Beyond")

  (T/is ($.test.string/bound*
          ($.string.char/at-end "foo bar"
                                -1))
        "Negative index fails"))



(T/deftest first

  (T/is (= \b
           ($.string.char/first "bar"))
        "Non-empty string")

  (T/is (nil? ($.string.char/first ""))
        "Empty"))



(T/deftest last

  (T/is (= \r
           ($.string.char/last "bar"))
        "Non-empty string")

  (T/is (nil? ($.string.char/last ""))
        "Empty"))
