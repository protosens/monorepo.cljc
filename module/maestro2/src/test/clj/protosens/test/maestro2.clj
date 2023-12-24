(ns protosens.test.maestro2

  (:require [clojure.test       :as T]
            [protosens.maestro2 :as $.maestro]))


;;;;;;;;;; Preparation


(defmethod $.maestro/directive
           "test-directive*"

  [state _nspace nm]

  (cond->
    state
    (not nm)
    (update-in [::$.maestro/deps
                ::i]
               inc)))


;;;;;;;;;; Reusable assertions


(defn- -t-path

  [message input alias-def+ path]

  (T/is (= path
           (-> ($.maestro/run input
                              {:aliases alias-def+})
               (::$.maestro/path)))
        message))


;;;;;;;;;; Tests


(T/deftest run


  (T/testing

    "Core assumptions about graph traversal"

    (-t-path "Empty"
             ""
             {}
             [])

    (-t-path "No deps (but mode activated)"
             ":m/a"
             {:m/a {}}
             [:m :m/a])

    (-t-path "Single dep"
             ":m/a"
             {:m/a {:maestro/require [:m/b]}
              :m/b {}}
             [:m :m/a :m/b])

    (-t-path  "Input deduplication"
              ":m/a:m/a"
              {:m/a {:maestro/require [:m/b]}
               :m/b {}}
              [:m :m/a :m/b])

    (-t-path "Input deduplication after transitive processing"
             ":m/a:m/b"
             {:m/a {:maestro/require [:m/b]}
              :m/b {}}
             [:m :m/a :m/b])

    (-t-path "Transitive dep"
             ":m/a"
             {:m/a {:maestro/require [:m/b]}
              :m/b {:maestro/require [:m/c]}
              :m/c {}}
             [:m :m/a :m/b :m/c])

    (-t-path "Transitive dep with input deduplication"
             ":m/a:m/b"
             {:m/a {:maestro/require [:m/b]}
              :m/b {:maestro/require [:m/c]}
              :m/c {}}
             [:m :m/a :m/b :m/c])

    (-t-path "Transitive deps with transitive deduplication"
             ":m/a"
             {:m/a {:maestro/require [:m/b]}
              :m/b {:maestro/require [:m/c
                                      :m/d]}
              :m/c {:maestro/require [:m/d]}
              :m/d {}} 
             [:m :m/a :m/b :m/c :m/d])

    (-t-path "Dep but relevant mode not activated"
             ":m/a"
             {:m/a {:maestro/require [:t/a]}
              :t/a {}}
             [:m :m/a])

    (-t-path "Dep by preactivating relevant mode"
             ":t:m/a"
             {:m/a {:maestro/require [:t/a]}
              :t/a {}}
             [:t :m :m/a :t/a])

    (-t-path "Dep by postactivating a mode"
              ":m/a:t"
              {:m/a {:maestro/require [:t/a]}
               :t/a {}}
              [:m :m/a :t])
    
    (-t-path "Transitive deps on mode with further mode activation"
             ":t:m/a"
             {:m/a   {:maestro/require [:t/a]}
              :t/a   {}
              :t     {:maestro/require [:e
                                        :e/lib]}
              :e/lib {}}
             [:t :e :e/lib :m :m/a :t/a])

    (T/testing

      "Directives (with `:shallow*` as example)"

      (-t-path "Activated at input"
               ":t:shallow*/t:t/a"
               {:t/a {:maestro/require [:m/a
                                        :t/b]}
                :t/b {}
                :t   {:maestro/require [:m]}
                :m/a {}}
               [:t :m :shallow* :shallow*/t :t/a :m/a])

      (-t-path "Activated at input but too late"
               ":t/a:shallow*/t"
               {:t/a {:maestro/require [:m/a
                                        :t/b]}
                :t/b {}
                :t   {:maestro/require [:m]}
                :m/a {}}
               [:t :m :t/a :m/a :t/b :shallow* :shallow*/t])

      (-t-path "Activated at input counteracted afterwards"
               ":shallow*/t:t/a"
               {:t/a {:maestro/require [:m/a
                                        :t/b]}
                :t/b {}
                :t   {:maestro/require [:m]}
                :m/a {}}
               [:shallow* :shallow*/t :t :m :t/a :m/a :t/b])

      (-t-path "Activated transitively"
               ":t/a"
               {:t/a {:maestro/require [:m/a
                                        :shallow*/t
                                        :t/b]}
                :t/b {}
                :t   {:maestro/require [:m]}
                :m/a {}}
               [:t :m :t/a :m/a :shallow* :shallow*/t])

      (-t-path "Activated transitively twice but initialized once"
               ":t/a"
               {:t/a {:maestro/require [:m/a
                                        :shallow*/t
                                        :shallow*/d
                                        :t/b
                                        :d/a]}
                :t/b {}
                :t   {:maestro/require [:m
                                        :d]}
                :d/a {}
                :m/a {}}
               [:t :m :d :t/a :m/a :shallow* :shallow*/t :shallow*/d])))


  (T/testing

    "Directive initialization"

    (T/is (= 1
             (-> ($.maestro/run ":test-directive*/foo:m/a"
                                {:aliases {:m/a {:maestro/require [:test-directive*]}}
                                 ::i      0})
                 (get-in [::$.maestro/deps
                          ::i])))))


  (T/testing

    "Circular deps allowed, processed without infinite loop"

    (doseq [[message
             def-dep+
             path]    [["Direct"
                        {:m/a {:maestro/require [:m/a
                                                 :m/b]}
                         :m/b {}}
                        [:m :m/a :m/b]]
                       ,
                       ["Transitive"
                        {:m/a {:maestro/require [:m/b
                                                 :m/c]}
                         :m/b {:maestro/require [:m/a]}
                         :m/c {}}
                        [:m :m/a :m/b :m/c]]]]
      (T/is (= path
               (-> (deref (future
                            ($.maestro/run ":m/a"
                                           {:aliases def-dep+}))
                          100
                          nil)
                   (::$.maestro/path)))
            message)))


  (T/testing

    "Missing namespaced aliases throw"

    (T/is (thrown? Exception
                   ($.maestro/run ":m/a"
                                  {}))
          "Empty")

    (T/is (thrown? Exception
                   ($.maestro/run ":m/a"
                                  {:aliases {:m/a {:maestro/require [:m/b]}}}))
          "Missing dep")))
