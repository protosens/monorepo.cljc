(ns protosens.test.maestro.assert

  ;; Reusable assertions.

  (:require [clojure.test           :as T]
            [protosens.maestro      :as-alias $.maestro]
            [protosens.maestro.walk :as $.maestro.walk]))


;;;;;;;;;;


(defn path

  [message input alias-def+ path]

  (T/is (= path
           (-> ($.maestro.walk/run-string input
                                          {:aliases alias-def+})
               (::$.maestro/path)))
        message))
