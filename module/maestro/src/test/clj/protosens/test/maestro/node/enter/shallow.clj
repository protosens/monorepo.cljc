(ns protosens.test.maestro.node.enter.shallow

  (:require [clojure.test                :as T]
            [protosens.test.util.maestro :as $.test.util.maestro]))


;;;;;;;;;;


(T/deftest enter

  ($.test.util.maestro/t-path
    [:t :SHALLOW/t :t/a]
    {:t/a {:maestro/require [:m/a
                             :t/b]}
     :t/b {}
     :t   {:maestro/require [:m]}
     :m/a {}}
    [[:t 0] [:m 1] [:SHALLOW 0] [:SHALLOW/t 0] [:t/a 0] [:m/a 1]]
    "Activated at input")
  
  ($.test.util.maestro/t-path
    [:SHALLOW/t :t/a]
    {:t/a {:maestro/require [:m/a
                             :t/b]}
     :t/b {}
     :t   {:maestro/require [:m]}
     :m/a {}}
    [[:SHALLOW 0] [:SHALLOW/t 0] [:t 0] [:m 1] [:t/a 0] [:m/a 1]]
    "Activated at input (2)")
  
  ($.test.util.maestro/t-path
    [:t/a :SHALLOW/t]
    {:t/a {:maestro/require [:m/a
                             :t/b]}
     :t/b {}
     :t   {:maestro/require [:m]}
     :m/a {}}
    [[:t 0] [:m 1] [:t/a 0] [:m/a 1] [:t/b 1] [:SHALLOW 0] [:SHALLOW/t 0]]
    "Activated at input but too late")
  
  ($.test.util.maestro/t-path
    [:t/a]
    {:t/a {:maestro/require [:m/a
                             :SHALLOW/t
                             :t/b]}
     :t/b {}
     :t   {:maestro/require [:m]}
     :m/a {}}
    [[:t 0] [:m 1] [:t/a 0] [:m/a 1] [:SHALLOW/t 1]]
    "Activated transitively")
  
  ($.test.util.maestro/t-path
    [:t/a]
    {:t/a {:maestro/require [:m/a
                             :SHALLOW/t
                             :SHALLOW/d
                             :t/b
                             :d/a]}
     :t/b {}
     :t   {:maestro/require [:m
                             :d]}
     :d/a {}
     :m/a {}}
    [[:t 0] [:m 1] [:d 1] [:t/a 0] [:m/a 1] [:SHALLOW/t 1] [:SHALLOW/d 1]]
    "Activated transitively twice but initialized once"))
