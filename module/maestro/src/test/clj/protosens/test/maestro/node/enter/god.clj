(ns protosens.test.maestro.node.enter.god

  (:require [clojure.test                :as T]
            [protosens.maestro           :as $.maestro]
            [protosens.test.util.maestro :as $.test.util.maestro]))


;;;;;;;;;;


(T/deftest enter

  ($.test.util.maestro/t-path
    [:GOD]
    {:m/a {:maestro/require [:m/b
                             :t/a]}
     :m/b {:maestro/require [:t/b]}
     :t/a {}
     :t/b {}}
    [[:GOD 0] [:m 1] [:t 1] [:m/a 1] [:m/b 2] [:t/b 3] [:t/a 2]]
    "Everything is required")

  ($.test.util.maestro/t-path
    [:m/a :GOD]
    {:m/a {:maestro/require [:m/b
                             :t/a]}
     :m/b {:maestro/require [:d/b]}
     :d/b {}
     :t/a {}}
    [[:m 0] [:m/a 0] [:m/b 1] [:GOD 0] [:d 1] [:t 1] [:d/b 1] [:t/a 1]]
    "Previously ignored nodes are visited")

  ($.test.util.maestro/t-fail*
    ($.maestro/run [:GOD/qualified]
                   {})
    "Should not be namespaced"))
