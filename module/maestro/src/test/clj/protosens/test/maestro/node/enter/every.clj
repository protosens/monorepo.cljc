(ns protosens.test.maestro.node.enter.every

  (:require [clojure.test                :as    T]
            [protosens.test.util.maestro :as $.test.util.maestro]))


;;;;;;;;;;


(T/deftest enter

  ($.test.util.maestro/t-path
    [:EVERY/t]
    {:m/a {}
     :m/b {}
     :t   {:maestro/require [:m]}
     :t/a {:maestro/require [:m/a]}
     :t/b {:maestro/require [:m/b]}}
    [[:EVERY 0] [:EVERY/t 0] [:t 1] [:m 2] [:t/a 1] [:m/a 2] [:t/b 1] [:m/b 2]]
    "All \"tests\" required")

  ($.test.util.maestro/t-path
    [:m/a :EVERY/t]
    {:m/a {:maestro/require [:m/b
                             :t/a]}
     :m/b {:maestro/require [:t/b]}
     :t/a {}
     :t/b {}}
    [[:m 0] [:m/a 0] [:m/b 1] [:EVERY 0] [:EVERY/t 0] [:t 1] [:t/a 1] [:t/b 1]]
    "Ignored \"tests\" revisited"))
