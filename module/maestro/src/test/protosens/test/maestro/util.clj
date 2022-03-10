(ns protosens.test.maestro.util

  (:require [clojure.test           :as T]
            [protosens.maestro.util :as $.maestro.util]))


;;;;;;;;;;


(T/deftest append-at

  (T/is (= {:foo []}
           ($.maestro.util/append-at {}
                                     :foo
                                     []))
        "No value for key, nothing to append")

  (T/is (= {:foo []}
           ($.maestro.util/append-at {:foo []}
                                     :foo
                                     []))
        "Existing empty vector, nothing to append")

  (T/is (= {:foo [:a :b :c :d]}
           ($.maestro.util/append-at {:foo [:a :b]}
                                     :foo
                                     [:c :d]))
        "Append to existing vector"))


(T/deftest prepend-at

  (T/is (= {:foo []}
           ($.maestro.util/prepend-at {}
                                      :foo
                                      []))
        "No value for key, nothing to prepend")

  (T/is (= {:foo []}
           ($.maestro.util/prepend-at {:foo []}
                                      :foo
                                      []))
        "Existing empty vector, nothing to prepend")

  (T/is (= {:foo [:c :d :a :b]}
           ($.maestro.util/prepend-at {:foo [:a :b]}
                                      :foo
                                      [:c :d]))
        "Prepend to existing vector"))
