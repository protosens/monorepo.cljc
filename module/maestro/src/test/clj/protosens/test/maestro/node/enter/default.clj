(ns protosens.test.maestro.node.enter.default

  (:require [clojure.test           :as    T]
            [protosens.maestro      :as    $.maestro]
            [protosens.test.maestro :refer [-t-path]]))


;;;;;;;;;;


(T/deftest enter


  (T/testing

    "Core assumptions about graph traversal"

    (-t-path "Empty"
             []
             {}
             [])

    (-t-path "No deps (but mode activated)"
             [:m/a]
             {:m/a {}}
             [[:m 0] [:m/a 0]])

    (-t-path "Single dep"
             [:m/a]
             {:m/a {:maestro/require [:m/b]}
              :m/b {}}
             [[:m 0] [:m/a 0] [:m/b 1]])

    (-t-path  "Input deduplication"
              [:m/a :m/a]
              {:m/a {:maestro/require [:m/b]}
               :m/b {}}
              [[:m 0] [:m/a 0] [:m/b 1]])

    (-t-path "Input deduplication after transitive processing"
             [:m/a :m/b]
             {:m/a {:maestro/require [:m/b]}
              :m/b {}}
             [[:m 0] [:m/a 0] [:m/b 1]])

    (-t-path "Transitive dep"
             [:m/a]
             {:m/a {:maestro/require [:m/b]}
              :m/b {:maestro/require [:m/c]}
              :m/c {}}
             [[:m 0] [:m/a 0] [:m/b 1] [:m/c 2]])

    (-t-path "Transitive dep with input deduplication"
             [:m/a :m/b]
             {:m/a {:maestro/require [:m/b]}
              :m/b {:maestro/require [:m/c]}
              :m/c {}}
             [[:m 0] [:m/a 0] [:m/b 1] [:m/c 2]])

    (-t-path "Transitive deps with transitive deduplication"
             [:m/a]
             {:m/a {:maestro/require [:m/b]}
              :m/b {:maestro/require [:m/c
                                      :m/d]}
              :m/c {:maestro/require [:m/d]}
              :m/d {}} 
             [[:m 0] [:m/a 0] [:m/b 1] [:m/c 2] [:m/d 3]])

    (-t-path "Dep but relevant mode not activated"
             [:m/a]
             {:m/a {:maestro/require [:t/a]}
              :t/a {}}
             [[:m 0] [:m/a 0]])

    (-t-path "Dep by preactivating relevant mode"
             [:t :m/a]
             {:m/a {:maestro/require [:t/a]}
              :t/a {}}
             [[:t 0] [:m 0] [:m/a 0] [:t/a 1]])

    (-t-path "Dep by postactivating a mode"
              [:m/a :t]
              {:m/a {:maestro/require [:t/a]}
               :t/a {}}
              [[:m 0] [:m/a 0] [:t 0]])
    
    (-t-path "Transitive deps on mode with further mode activation"
             [:t :m/a]
             {:m/a   {:maestro/require [:t/a]}
              :t/a   {}
              :t     {:maestro/require [:e
                                        :e/lib]}
              :e/lib {}}
             [[:t 0] [:e 1] [:e/lib 1] [:m 0] [:m/a 0] [:t/a 1]]))


  (T/testing

    "Circular deps allowed, processed without infinite loop"

    (doseq [[message
             def-dep+
             path]    [["Direct"
                        {:m/a {:maestro/require [:m/a
                                                 :m/b]}
                         :m/b {}}
                        [[:m 0] [:m/a 0] [:m/b 1]]]
                       ,
                       ["Transitive"
                        {:m/a {:maestro/require [:m/b
                                                 :m/c]}
                         :m/b {:maestro/require [:m/a]}
                         :m/c {}}
                        [[:m 0] [:m/a 0] [:m/b 1] [:m/c 1]]]]]
      (T/is (= path
               (-> (deref (future
                            ($.maestro/run [:m/a]
                                           {:aliases def-dep+}))
                          100
                          nil)
                   (::$.maestro/path)))
            message)))


  (T/testing

    "Throws when namespaced aliases are missing"

    (T/is (thrown? Exception
                   ($.maestro/run [:m/a]
                                  {}))
          "Empty")

    (T/is (thrown? Exception
                   ($.maestro/run [:m/a]
                                  {:aliases {:m/a {:maestro/require [:m/b]}}}))
          "Missing dep")))
