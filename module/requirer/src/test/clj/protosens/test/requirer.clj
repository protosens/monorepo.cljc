(ns protosens.test.requirer

  "Tests `$.requirer`."

  (:require [clojure.test       :as T]
            [protosens.deps.edn :as $.deps.edn]
            [protosens.requirer :as $.requirer]))


;;;;;;;;;; Values


(def ^:no-doc -d*deps-edn

  ;; Test `deps.edn` file.

  (delay
    ($.deps.edn/read "module/requirer/resrc/test")))


;;;;;;;;;;


(defn- -require-project

  [f]

  (T/is (true? (f @-d*deps-edn
                  {:protosens.process/option+ {:out nil}}))
        "Without any alias")

  (T/testing

    "With alias"

    (T/is (true? (f @-d*deps-edn
                    {:alias+                    [:extra]
                     :protosens.process/option+ {:out nil}}))
          "Good alias")

    (T/is (false? (f @-d*deps-edn
                     {:alias+                    [:fail]
                      :protosens.process/option+ {:err nil
                                                  :out nil}}))
          "Bad alias")))



(T/deftest bb

  (-require-project $.requirer/bb))



(T/deftest clojure-cli

  (-require-project $.requirer/clojure-cli))
