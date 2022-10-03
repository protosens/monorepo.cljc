(ns protosens.test.string

  "Test `protosens.string`."

  (:refer-clojure :exclude [newline])
  (:require [clojure.test     :as T]
            [protosens.string :as $.string]))


;;;;;;;;;; Values


(def nl
     ($.string/newline))


;;;;;;;;;; Tests


(T/deftest count-leading-space

  (T/is (= 0
           ($.string/count-leading-space "test")))
  (T/is (= 1
           ($.string/count-leading-space " test")))
  (T/is (= 2
           ($.string/count-leading-space "  test")))
  (T/is (= 3
           ($.string/count-leading-space "   test"))))



(T/deftest cut-out

  (T/testing

    "Success"

    (T/is (= "12"
             ($.string/cut-out "12345"
                               0
                               2)))

    (T/is (= "23"
             ($.string/cut-out "12345"
                               1
                               3))))

  (T/testing

    "Failure"

    (T/is (thrown? Exception
                   ($.string/cut-out "12345"
                                     2
                                     100))
          "End too big")

    (T/is (thrown? Exception
                   ($.string/cut-out "12345"
                                     3
                                     2))
          "Start > end")

    (T/is (thrown? Exception
                   ($.string/cut-out "12345"
                                     -1
                                     2))
          "Negative start")

    (T/is (thrown? Exception
                   ($.string/cut-out "12345"
                                     2
                                     -1))
          "Negative end")))



(T/deftest newline

  (T/is (contains? #{"\n"
                     "\r\n"}
                   nl)))



(T/deftest realign

  (T/is (= "foo"
           ($.string/realign "foo")))

  (T/is (= "    foo"
           ($.string/realign "    foo")))


  (T/is (= "  "
           ($.string/realign "  ")))

  (T/is (= (str "foo" nl
                "bar")
           ($.string/realign "foo
                              bar")))

  (T/is (= (str "foo" nl
                "bar")
           ($.string/realign "foo
                                 bar")))

  (T/is (= (str "foo" nl
                "bar" nl
                "baz")
           ($.string/realign "foo
                                 bar
                                 baz")))

  (T/is (= (str "foo" nl
                "bar" nl
                "  baz")
           ($.string/realign "foo
                              bar
                                baz")))

  (T/is (= (str "foo" nl
                "bar" nl
                "  baz")
           ($.string/realign "     foo
                              bar
                                baz")))

  (T/is (= (str "foo"  nl
                " bar" nl
                "baz")
           ($.string/realign "foo
                                 bar
                                baz"))))



(T/deftest trunc-left

  (T/testing

    "Success"

    (T/is (= "12345"
             ($.string/trunc-left "12345"
                                  0)))

    (T/is (= "2345"
             ($.string/trunc-left "12345"
                                  1)))

    (T/is (= "345"
             ($.string/trunc-left "12345"
                                  2))))

  (T/testing

     "Failure"

     (T/is (thrown? Exception
                    ($.string/trunc-left "12345"
                                         100))
           "Input too small")

     (T/is (thrown? Exception
                    ($.string/trunc-left "12345"
                                         -1))
           "Negative index")))



(T/deftest trunc-right

  (T/testing

    "Success"

    (T/is (= "12345"
             ($.string/trunc-right "12345"
                                   0)))

    (T/is (= "1234"
             ($.string/trunc-right "12345"
                                   1)))

    (T/is (= "123"
             ($.string/trunc-right "12345"
                                   2))))

  (T/testing

     "Failure"

     (T/is (thrown? Exception
                    ($.string/trunc-right "12345"
                                          100))
           "Input too small")

     (T/is (thrown? Exception
                    ($.string/trunc-right "12345"
                                          -1))
           "Negative index")))
