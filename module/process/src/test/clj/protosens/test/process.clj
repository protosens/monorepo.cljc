(ns protosens.test.process

  "Tests `$.process`."

  (:require [clojure.test      :as T]
            [protosens.process :as $.process]))


;;;;;;;;;;


(T/deftest run

  (let [process ($.process/run ["echo" "foo"])]

    (T/is (zero? ($.process/exit-code process)))

    (T/is ($.process/success? process))

    (T/is (nil? ($.process/err process)))

    (T/is (= "foo"
             ($.process/out process)))))



(T/deftest shell

  (let [process ($.process/shell ["echo" "This is printed from a test."])]

    (T/is (zero? ($.process/exit-code process)))

    (T/is ($.process/success? process))))
