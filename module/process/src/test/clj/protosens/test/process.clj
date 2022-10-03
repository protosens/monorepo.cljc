(ns protosens.test.process

  "Tests `$.process`."

  (:require [clojure.test      :as T]
            [protosens.process :as $.process]))


;;;;;;;;;;


(T/deftest shell

  (let [process ($.process/shell ["echo" "This is printed from a test."])]

    (T/is (zero? ($.process/exit-code process)))
    
    (T/is ($.process/success? process))))
