(ns protosens.test.symbol

  (:refer-clojure :exclude [replace])
  (:require [clojure.test     :as T]
            [protosens.symbol :as $.symbol]))


;;;;;;;;;;


(T/deftest ends-with?

  (T/is (true? ($.symbol/ends-with? 'foo.bar
                                    'bar)))

  (T/is (true? ($.symbol/ends-with? "foo.bar"
                                    "bar")))

  (T/is (false? ($.symbol/ends-with? 'foo.bar
                                     'xxx)))

  (T/is (false? ($.symbol/ends-with? "foo.bar"
                                     "xxx"))))



(T/deftest includes?

  (T/is (true? ($.symbol/includes? 'test
                                   'test)))

  (T/is (true? ($.symbol/includes? 'test
                                   "test")))

  (T/is (true? ($.symbol/includes? 'test
                                   'es)))

  (T/is (true? ($.symbol/includes? 'test
                                   "es"))))



(T/deftest join

  (T/is (= 'a.b.c
           ($.symbol/join ['a 'b 'c])))

  (T/is (= 'a.b.c
           ($.symbol/join ['a "b" \c])))

  (T/is (= 'a|b|c
           ($.symbol/join '|
                          ['a 'b 'c])))

  (T/is (= 'a|b|c
           ($.symbol/join "|"
                          ['a 'b 'c]))))



(T/deftest qualify

  (T/is (= 'foo/bar
           ($.symbol/qualify 'foo
                             'bar)))

  (T/is (= 'foo/bar
           ($.symbol/qualify "foo"
                             "bar")))

  (T/is (= 'foo/bar
           ($.symbol/qualify 'foo
                             'xxx/bar))))



(T/deftest replace

  (T/is (= 'a-b-c
           ($.symbol/replace 'a_b_c
                             '_
                             '-)))

  (T/is (= 'a-b-c
           ($.symbol/replace "a_b_c"
                             "_"
                             "-")))

  (T/is (= 'a-b-c
           ($.symbol/replace 'a_b_c
                             #"_"
                             (constantly '-)))))



(T/deftest replace-first

  (T/is (= 'a-b_c
           ($.symbol/replace-first 'a_b_c
                                   '_
                                   '-)))

  (T/is (= 'a-b_c
           ($.symbol/replace-first "a_b_c"
                                   "_"
                                   "-")))

  (T/is (= 'a-b_c
           ($.symbol/replace-first 'a_b_c
                                   #"_"
                                   (constantly '-)))))



(T/deftest split

  (T/is (= '(a b c)
           ($.symbol/split 'a.b.c)))

  (T/is (= '(a b c)
           ($.symbol/split "a.b.c")))

  (T/is (= '(a_b_c)
           ($.symbol/split 'a_b_c)))

  (T/is (= '(a_b_c)
           ($.symbol/split "a_b_c")))

  (T/is (= '(a b c)
           ($.symbol/split #"_"
                           'a_b_c)))
  (T/is (= '(a b c)
           ($.symbol/split #"_"
                           "a_b_c"))))



(T/deftest starts-with?

  (T/is (true? ($.symbol/starts-with? 'foo.bar
                                      'foo)))

  (T/is (true? ($.symbol/starts-with? "foo.bar"
                                      "foo")))

  (T/is (false? ($.symbol/starts-with? 'foo.bar
                                       'xxx)))

  (T/is (false? ($.symbol/starts-with? "foo.bar"
                                       "xxx"))))



(T/deftest stringify

  (T/is (= "test"
           ($.symbol/stringify 'test)))

  (T/is (= :not-symbol
           ($.symbol/stringify :not-symbol))))
