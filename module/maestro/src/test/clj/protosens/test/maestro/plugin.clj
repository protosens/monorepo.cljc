(ns protosens.test.maestro.plugin

  (:require [clojure.test                :as T]
            [protosens.maestro.plugin    :as $.maestro.plugin]
            [protosens.test.util.maestro :as $.test.util.maestro]))


;;;;;;;;;;


(T/deftest fail

  ($.test.util.maestro/t-fail*
    ($.maestro.plugin/fail "Error")
    "Without cause")

  ($.test.util.maestro/t-fail*
    ($.maestro.plugin/fail "Error"
                           (Exception. "Exception"))
    "With cause"))



(T/deftest safe

  (T/is (= 42
           ($.maestro.plugin/safe (delay 42)))
        "Success")

  ($.test.util.maestro/t-fail*
    ($.maestro.plugin/safe
      (delay
        (throw (Exception. "Exception"))))))


;;;;;;;;;;


(T/deftest read-file-edn

  (T/is (map? ($.maestro.plugin/read-file-edn "./deps.maestro.edn"))))



(T/deftest read-deps-maestro-edn

  (T/is (map? ($.maestro.plugin/read-deps-maestro-edn)))

  (T/is (= ($.maestro.plugin/read-file-edn "./deps.maestro.edn")
           ($.maestro.plugin/read-deps-maestro-edn))
        "From working tree")

  (T/is (= ($.maestro.plugin/read-deps-maestro-edn)
           ($.maestro.plugin/read-deps-maestro-edn "HEAD"))
        "From revision"))

