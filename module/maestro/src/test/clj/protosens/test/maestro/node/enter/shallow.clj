(ns protosens.test.maestro.node.enter.shallow

  (:require [clojure.test           :as    T]
            [protosens.test.maestro :refer [-t-path]]))


;;;;;;;;;;


(T/deftest enter

  (-t-path "Activated at input"
           [:t :SHALLOW/t :t/a]
           {:t/a {:maestro/require [:m/a
                                    :t/b]}
            :t/b {}
            :t   {:maestro/require [:m]}
            :m/a {}}
           [[:t 0] [:m 1] [:SHALLOW 0] [:SHALLOW/t 0] [:t/a 0] [:m/a 1]])
  
  (-t-path "Activated at input (2)"
           [:SHALLOW/t :t/a]
           {:t/a {:maestro/require [:m/a
                                    :t/b]}
            :t/b {}
            :t   {:maestro/require [:m]}
            :m/a {}}
           [[:SHALLOW 0] [:SHALLOW/t 0] [:t 0] [:m 1] [:t/a 0] [:m/a 1]])
  
  (-t-path "Activated at input but too late"
           [:t/a :SHALLOW/t]
           {:t/a {:maestro/require [:m/a
                                    :t/b]}
            :t/b {}
            :t   {:maestro/require [:m]}
            :m/a {}}
           [[:t 0] [:m 1] [:t/a 0] [:m/a 1] [:t/b 1] [:SHALLOW 0] [:SHALLOW/t 0]])
  
  (-t-path "Activated transitively"
           [:t/a]
           {:t/a {:maestro/require [:m/a
                                    :SHALLOW/t
                                    :t/b]}
            :t/b {}
            :t   {:maestro/require [:m]}
            :m/a {}}
           [[:t 0] [:m 1] [:t/a 0] [:m/a 1] [:SHALLOW/t 1]])
  
  (-t-path "Activated transitively twice but initialized once"
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
           [[:t 0] [:m 1] [:d 1] [:t/a 0] [:m/a 1] [:SHALLOW/t 1] [:SHALLOW/d 1]]))
