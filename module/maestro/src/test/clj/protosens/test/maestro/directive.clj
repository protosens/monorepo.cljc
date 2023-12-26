(ns protosens.test.maestro.directive

  (:require [clojure.test                  :as       T]
            [protosens.maestro             :as-alias $.maestro]
            [protosens.maestro.directive   :as       $.maestro.directive]
            [protosens.maestro.walk        :as       $.maestro.walk]
            [protosens.test.maestro.assert :refer    [path]
                                           :rename   {path -t-path}]))


;;;;;;;;;; Preparation


(defmethod $.maestro.directive/run
           "test-directive*"

  [state _nspace nm]

  (cond->
    state
    (not nm)
    (update-in [::$.maestro/deps-maestro-edn
                ::i]
               inc)))


;;;;;;;;;; Tests


(T/deftest run

  (T/is (= 1
           (-> ($.maestro.walk/run-string ":test-directive*/foo:m/a"
                                          {:aliases
                                           {:m/a {:maestro/require [:test-directive*]}}
                                           ,
                                           ::i
                                           0})
               (get-in [::$.maestro/deps-maestro-edn
                        ::i])))))


;;;


(T/deftest EVERY

  (-t-path "All \"tests\" required"
           ":EVERY/t"
           {:m/a {}
            :m/b {}
            :t   {:maestro/require [:m]}
            :t/a {:maestro/require [:m/a]}
            :t/b {:maestro/require [:m/b]}}
           [[:EVERY 0] [:EVERY/t 0] [:t 0] [:m 1] [:t/a 0] [:m/a 1] [:t/b 0] [:m/b 1]]))



(T/deftest GOD

  (-t-path "Everything is required"
           ":GOD"
           {:m/a {:maestro/require [:m/b
                                    :t/a]}
            :m/b {:maestro/require [:t/b]}
            :t/a {}
            :t/b {}}
           [[:GOD 0] [:m 0] [:t 0] [:m/a 0] [:m/b 1] [:t/b 2] [:t/a 1]]))



(T/deftest SHALLOW

  ;; Also tests core assumptions about directive application.


  (-t-path "Activated at input"
           ":t:SHALLOW/t:t/a"
           {:t/a {:maestro/require [:m/a
                                    :t/b]}
            :t/b {}
            :t   {:maestro/require [:m]}
            :m/a {}}
           [[:t 0] [:m 1] [:SHALLOW 0] [:SHALLOW/t 0] [:t/a 0] [:m/a 1]])

  (-t-path "Activated at input (2)"
           ":SHALLOW/t:t/a"
           {:t/a {:maestro/require [:m/a
                                    :t/b]}
            :t/b {}
            :t   {:maestro/require [:m]}
            :m/a {}}
           [[:SHALLOW 0] [:SHALLOW/t 0] [:t 0] [:m 1] [:t/a 0] [:m/a 1]])

  (-t-path "Activated at input but too late"
           ":t/a:SHALLOW/t"
           {:t/a {:maestro/require [:m/a
                                    :t/b]}
            :t/b {}
            :t   {:maestro/require [:m]}
            :m/a {}}
           [[:t 0] [:m 1] [:t/a 0] [:m/a 1] [:t/b 1] [:SHALLOW 0] [:SHALLOW/t 0]])

  (-t-path "Activated transitively"
           ":t/a"
           {:t/a {:maestro/require [:m/a
                                    :SHALLOW/t
                                    :t/b]}
            :t/b {}
            :t   {:maestro/require [:m]}
            :m/a {}}
           [[:t 0] [:m 1] [:t/a 0] [:m/a 1] [:SHALLOW 1] [:SHALLOW/t 1]])

  (-t-path "Activated transitively twice but initialized once"
           ":t/a"
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
           [[:t 0] [:m 1] [:d 1] [:t/a 0] [:m/a 1] [:SHALLOW 1] [:SHALLOW/t 1] [:SHALLOW/d 1]]))
