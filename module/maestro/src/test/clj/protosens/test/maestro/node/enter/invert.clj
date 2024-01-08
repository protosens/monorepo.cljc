(ns protosens.test.maestro.node.enter.invert

  (:require [clojure.test                :as    T]
            [protosens.maestro           :as    $.maestro]
            [protosens.test.util.maestro :as $.test.util.maestro]))


;;;;;;;;;;


(T/deftest enter

  ($.test.util.maestro/t-path
    [:m/b :INVERT]
    {:m/a {:maestro/require [:m/b]}
     :m/b {:maestro/require [:m/c]}
     :m/c {}
     :m/d {:maestro/require [:m/c
                             :m/f]}
     :m/e {:maestro/require [:m/d]}
     :m/f {}}
    [[:m 0] [:m/b 0] [:m/c 1] [:INVERT 0] [:m/a 1] [:m/d 1] [:m/f 2] [:m/e 1]]
    "Dependents are required (and their own dependencies)")

  ($.test.util.maestro/t-path
    [:m/b :t :INVERT]
    {:m/a {:maestro/require [:m/b]}
     :m/b {:maestro/require [:m/c
                             :t/b]}
     :m/c {}
     :t/a {:maestro/require [:m/a]}
     :t/b {:maestro/require [:m/b]}}
    [[:m 0] [:m/b 0] [:m/c 1] [:t 0] [:INVERT 0] [:m/a 1] [:t/a 1] [:t/b 1]]
    "Dependents that were already rejected are required nonetheless")

  ($.test.util.maestro/t-fail*
    ($.maestro/run [:INVERT/qualified]
                   {})
    "Should not be namespaced"))
