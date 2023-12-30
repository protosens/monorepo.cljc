(ns protosens.test.maestro.node.enter

  (:require [clojure.test                 :as T]
            [protosens.maestro.node.enter :as $.maestro.node.enter]))


;;;;;;;;;;


(T/deftest assert-unqualified

  (T/is (nil? ($.maestro.node.enter/assert-unqualified :FOO)))

  (T/is (thrown? Exception
                 ($.maestro.node.enter/assert-unqualified :FOO/bar))))
