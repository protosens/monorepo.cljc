(ns protosens.test.maestro.alias

  (:require [clojure.test            :as T]
            [protosens.maestro.alias :as $.maestro.alias]))


;;;;;;;;;;


(T/deftest append+

  (T/is (= {:maestro/alias+ '[a b]}
           ($.maestro.alias/append+ {}
                                      '[a b]))
        "No existing alias")

  (T/is (= {:maestro/alias+ '[a b c d]}
           ($.maestro.alias/append+ {:maestro/alias+ '[a b]}
                                      '[c d]))
        "To existing aliass"))


(T/deftest prepend+

  (T/is (= {:maestro/alias+ '[a b]}
           ($.maestro.alias/prepend+ {}
                                       '[a b]))
        "No existing alias")

  (T/is (= {:maestro/alias+ '[c d a b]}
           ($.maestro.alias/prepend+ {:maestro/alias+ '[a b]}
                                       '[c d]))
        "To existing aliass"))


;;;;;;;;;;


(T/deftest extra-paths+

  (T/is (= '("./a-1"
             "./a-2"
             "./c-1"
             "./c-2")
           ($.maestro.alias/extra-path+ {:aliases {:a {:extra-paths ["./a-1"
                                                                     "./a-2"]}
                                                   :b {:extra-paths ["./b-1"
                                                                     "./b-2"]} 
                                                   :c {:extra-paths ["./c-1"
                                                                     "./c-2"]}
                                                   :d {}}}
                                        [:a
                                         :c]))))


(T/deftest stringify+

  (T/is (= ""
           ($.maestro.alias/stringify+ []))
        "No alias")

  (T/is (= ":a"
           ($.maestro.alias/stringify+ [:a]))
        "One alias")

  (T/is (= ":a:b:c"
           ($.maestro.alias/stringify+ [:a
                                        :b
                                        :c]))
        "Several aliases"))
