(ns protosens.test.txt

  "Test `protosens.txt`."

  (:refer-clojure :exclude [newline])
  (:require [clojure.test  :as T]
            [protosens.txt :as $.txt]))


;;;;;;;;;; Values


(def nl
     ($.txt/newline))


;;;;;;;;;; Tests


(T/deftest count-leading-space

  (T/is (= 0
           ($.txt/count-leading-space "test")))
  (T/is (= 1
           ($.txt/count-leading-space " test")))
  (T/is (= 2
           ($.txt/count-leading-space "  test")))
  (T/is (= 3
           ($.txt/count-leading-space "   test"))))



(T/deftest cut-out

  (T/testing

    "Success"

    (T/is (= "12"
             ($.txt/cut-out "12345"
                            0
                            2)))

    (T/is (= "23"
             ($.txt/cut-out "12345"
                            1
                            3))))

  (T/testing

    "Failure"

    (T/is (thrown? Exception
                   ($.txt/cut-out "12345"
                                  2
                                  100))
          "End too big")

    (T/is (thrown? Exception
                   ($.txt/cut-out "12345"
                                  3
                                  2))
          "Start > end")

    (T/is (thrown? Exception
                   ($.txt/cut-out "12345"
                                  -1
                                  2))
          "Negative start")

    (T/is (thrown? Exception
                   ($.txt/cut-out "12345"
                                  2
                                  -1))
          "Negative end")))



(T/deftest newline

  (T/is (contains? #{"\n"
                     "\r\n"}
                   nl)))



(T/deftest realign

  (T/is (= "foo"
           ($.txt/realign "foo")))

  (T/is (= "    foo"
           ($.txt/realign "    foo")))


  (T/is (= "  "
           ($.txt/realign "  ")))

  (T/is (= (str "foo" nl
                "bar")
           ($.txt/realign "foo
                           bar")))

  (T/is (= (str "foo" nl
                "bar")
           ($.txt/realign "foo
                              bar")))

  (T/is (= (str "foo" nl
                "bar" nl
                "baz")
           ($.txt/realign "foo
                              bar
                              baz")))

  (T/is (= (str "foo" nl
                "bar" nl
                "  baz")
           ($.txt/realign "foo
                           bar
                             baz")))

  (T/is (= (str "foo" nl
                "bar" nl
                "  baz")
           ($.txt/realign "     foo
                           bar
                             baz")))

  (T/is (= (str "foo"  nl
                " bar" nl
                "baz")
           ($.txt/realign "foo
                              bar
                             baz"))))



(T/deftest trunc-left

  (T/testing

    "Success"

    (T/is (= "12345"
             ($.txt/trunc-left "12345"
                               0)))

    (T/is (= "2345"
             ($.txt/trunc-left "12345"
                               1)))

    (T/is (= "345"
             ($.txt/trunc-left "12345"
                               2))))

  (T/testing

     "Failure"

     (T/is (thrown? Exception
                    ($.txt/trunc-left "12345"
                                      100))
           "Input too small")

     (T/is (thrown? Exception
                    ($.txt/trunc-left "12345"
                                      -1))
           "Negative index")))



(T/deftest trunc-right

  (T/testing

    "Success"

    (T/is (= "12345"
             ($.txt/trunc-right "12345"
                                0)))

    (T/is (= "1234"
             ($.txt/trunc-right "12345"
                                1)))

    (T/is (= "123"
             ($.txt/trunc-right "12345"
                                2))))

  (T/testing

     "Failure"

     (T/is (thrown? Exception
                    ($.txt/trunc-right "12345"
                                       100))
           "Input too small")

     (T/is (thrown? Exception
                    ($.txt/trunc-right "12345"
                                       -1))
           "Negative index")))
