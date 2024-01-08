(ns protosens.test.maestro.node.enter

  (:require [clojure.test                 :as T]
            [protosens.maestro.node.enter :as $.maestro.node.enter]
            [protosens.test.util.maestro  :as $.test.util.maestro]))


;;;;;;;;;;


(T/deftest assert-unqualified

  (T/is (nil? ($.maestro.node.enter/assert-unqualified :FOO)))

  ($.test.util.maestro/t-fail*
    ($.maestro.node.enter/assert-unqualified :FOO/bar)))
