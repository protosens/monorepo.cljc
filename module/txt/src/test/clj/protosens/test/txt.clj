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
