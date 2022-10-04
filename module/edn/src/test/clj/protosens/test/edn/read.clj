(ns protosens.test.edn.read

  "Tests `$.edn.read`."

  (:require [clojure.test       :as T]
            [protosens.edn.read :as $.edn.read]))


;;;;;;;;;;


(T/deftest file

  (T/is (= 1
           ($.edn.read/file "module/edn/resrc/test/test.edn"))))



(T/deftest string

  ;; Also tests possible options.

  (T/is (= 1
           ($.edn.read/string "1 2 3"))
        "Single value")

  (T/is (= ::end
           ($.edn.read/string ""
                              {:end ::end}))
        "End")

  (T/is (= 43
           ($.edn.read/string "#inc 42"
                              {:tag->reader {'inc inc}}))
        "With readers map")

  (T/is (= 43
           ($.edn.read/string "#inc 42"
                              {:default-reader (fn [_tag x]
                                                 (inc x))}))
        "With default reader"))
