(ns protosens.test.string

  "Test `protosens.string`."

  (:refer-clojure :exclude [newline])
  (:require [clojure.test     :as T]
            [protosens.string :as $.string]))


;;;;;;;;;; Helpers


(defmacro bound*

  [form]

  (if (System/getProperty "babashka.version")
    `(try
       ~form
       false
       (catch Throwable _ex
         true))
    `(= StringIndexOutOfBoundsException
        (try
          ~form
          nil
          (catch Throwable ex#
            (class ex#))))))


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

    (T/is (bound*
            ($.string/cut-out "12345"
                              2
                              100))
          "End too big")

    (T/is (bound*
            ($.string/cut-out "12345"
                              3
                              2))
          "Start > end")

    (T/is (bound*
            ($.string/cut-out "12345"
                              -1
                              2))
          "Negative start")

    (T/is (bound*
            ($.string/cut-out "12345"
                              2
                              -1))
          "Negative end")))



(T/deftest first-line

  (T/is (= ""
           ($.string/first-line "")))

  (T/is (= "foo"
           ($.string/first-line "foo")))

  (T/is (= "foo"
           ($.string/first-line "foo\n")))

  (T/is (= "foo"
           ($.string/first-line "foo\nbar"))))



(T/deftest line+

  (T/is (= [""]
           ($.string/line+ "")))

  (T/is (= ["foo"]
           ($.string/line+ "foo")))

  (T/is (= ["foo" ""]
           ($.string/line+ "foo\n")))

  (T/is (= ["foo" "bar"]
           ($.string/line+ "foo\nbar")))

  (T/is (= ["foo\nbar"]
           ($.string/line+ "foo\nbar"
                           0)))

  (T/is (= ["foo" "bar\nbaz\n"]
           ($.string/line+ "foo\nbar\nbaz\n"
                           1)))

  (T/is (= ["foo" "bar" "baz\n"]
           ($.string/line+ "foo\nbar\nbaz\n"
                           2)))

  (T/is (= ["foo" "bar" "baz" ""]
           ($.string/line+ "foo\nbar\nbaz\n"
                           3)))

  (T/is (= ["foo" "bar" "baz" ""]
           ($.string/line+ "foo\nbar\nbaz\n"
                           4)))

  (T/is (= ["foo" "bar" "baz" ""]
           ($.string/line+ "foo\nbar\nbaz\n"
                           5))))



(T/deftest n-first

  (T/testing

    "Success"

    (T/is (= "te"
             ($.string/n-first "test"
                               2)))

    (T/is (= ""
             ($.string/n-first "test"
                               0))))

  (T/testing

    "Failure"

    (T/is (bound*
            ($.string/n-first "test"
                              100)))

    (T/is (bound*
            ($.string/n-first "test"
                              -1)))))



(T/deftest n-last

  (T/testing

    "Success"

    (T/is (= "st"
             ($.string/n-last "test"
                              2)))

    (T/is (= ""
             ($.string/n-last "test"
                              0))))

  (T/testing

    "Failure"

    (T/is (bound*
            ($.string/n-last "test"
                             100)))

    (T/is (bound*
            ($.string/n-last "test"
                             -1)))))



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

     (T/is (bound*
             ($.string/trunc-left "12345"
                                  100))
           "Input too small")

     (T/is (bound*
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

     (T/is (bound*
              ($.string/trunc-right "12345"
                                    100))
           "Input too small")

     (T/is (bound*
             ($.string/trunc-right "12345"
                                   -1))
           "Negative index")))
