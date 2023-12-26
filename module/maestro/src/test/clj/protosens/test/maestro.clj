(ns protosens.test.maestro

  (:require [clojure.test      :as T]
            [protosens.maestro :as $.maestro]))


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
           (-> ($.maestro/-run input
                               {:aliases alias-def+})
               (::$.maestro/path)))
        message))


;;;;;;;;;; Tests


(T/deftest directive


  (T/testing

    "Directive initialization"

    (T/is (= 1
             (-> ($.maestro/-run ":test-directive*/foo:m/a"
                                 {:aliases {:m/a {:maestro/require [:test-directive*]}}
                                  ::i      0})
                 (get-in [::$.maestro/deps
                          ::i])))))


  (T/testing

   "`:SHALLOW` (and core assumptions)"

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


  (T/testing

    "GOD"

    (-t-path "Everything is required"
             ":GOD"
             {:m/a {:maestro/require [:m/b
                                      :t/a]}
              :m/b {:maestro/require [:t/b]}
              :t/a {}
              :t/b {}}
             [[:GOD 0] [:m 0] [:t 0] [:m/a 0] [:m/b 1] [:t/b 2] [:t/a 1]]))


  (T/testing

    "EVERY"

    (-t-path "All \"tests\" required"
             ":EVERY/t"
             {:m/a {}
              :m/b {}
              :t   {:maestro/require [:m]}
              :t/a {:maestro/require [:m/a]}
              :t/b {:maestro/require [:m/b]}}
             [[:EVERY 0] [:EVERY/t 0] [:t 0] [:m 1] [:t/a 0] [:m/a 1] [:t/b 0] [:m/b 1]])))



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
             [[:m 0] [:m/a 0]])

    (-t-path "Single dep"
             ":m/a"
             {:m/a {:maestro/require [:m/b]}
              :m/b {}}
             [[:m 0] [:m/a 0] [:m/b 1]])

    (-t-path  "Input deduplication"
              ":m/a:m/a"
              {:m/a {:maestro/require [:m/b]}
               :m/b {}}
              [[:m 0] [:m/a 0] [:m/b 1]])

    (-t-path "Input deduplication after transitive processing"
             ":m/a:m/b"
             {:m/a {:maestro/require [:m/b]}
              :m/b {}}
             [[:m 0] [:m/a 0] [:m/b 1]])

    (-t-path "Transitive dep"
             ":m/a"
             {:m/a {:maestro/require [:m/b]}
              :m/b {:maestro/require [:m/c]}
              :m/c {}}
             [[:m 0] [:m/a 0] [:m/b 1] [:m/c 2]])

    (-t-path "Transitive dep with input deduplication"
             ":m/a:m/b"
             {:m/a {:maestro/require [:m/b]}
              :m/b {:maestro/require [:m/c]}
              :m/c {}}
             [[:m 0] [:m/a 0] [:m/b 1] [:m/c 2]])

    (-t-path "Transitive deps with transitive deduplication"
             ":m/a"
             {:m/a {:maestro/require [:m/b]}
              :m/b {:maestro/require [:m/c
                                      :m/d]}
              :m/c {:maestro/require [:m/d]}
              :m/d {}} 
             [[:m 0] [:m/a 0] [:m/b 1] [:m/c 2] [:m/d 3]])

    (-t-path "Dep but relevant mode not activated"
             ":m/a"
             {:m/a {:maestro/require [:t/a]}
              :t/a {}}
             [[:m 0] [:m/a 0]])

    (-t-path "Dep by preactivating relevant mode"
             ":t:m/a"
             {:m/a {:maestro/require [:t/a]}
              :t/a {}}
             [[:t 0] [:m 0] [:m/a 0] [:t/a 1]])

    (-t-path "Dep by postactivating a mode"
              ":m/a:t"
              {:m/a {:maestro/require [:t/a]}
               :t/a {}}
              [[:m 0] [:m/a 0] [:t 0]])
    
    (-t-path "Transitive deps on mode with further mode activation"
             ":t:m/a"
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
                            ($.maestro/-run ":m/a"
                                            {:aliases def-dep+}))
                          100
                          nil)
                   (::$.maestro/path)))
            message)))


  (T/testing

    "Missing namespaced aliases throw"

    (T/is (thrown? Exception
                   ($.maestro/-run ":m/a"
                                   {}))
          "Empty")

    (T/is (thrown? Exception
                   ($.maestro/-run ":m/a"
                                   {:aliases {:m/a {:maestro/require [:m/b]}}}))
          "Missing dep"))


  (T/testing

    "Alias definitions are flattened and everything from root keys is present"

    (let [dep+ {:foo     :bar
                42       24
                :aliases {:m/a {:extra-deps      {'dep/a :dep/a}
                                :extra-paths     ["path/a"]
                                :maestro/doc     "Module A"
                                :maestro/require [:m/b]}
                          ,
                          :m/b {:extra-deps      {'dep/b :dep/b}
                                :maestro/doc     "Module B"
                                :maestro/require [:m/c]}
                          ,
                          :m/c {:extra-paths     ["path/c"] 
                                :maestro/doc     "Module C"}}}]
      (T/is (= (-> dep+
                   (assoc :deps  {'dep/a :dep/a
                                  'dep/b :dep/b}
                          :paths #{"path/a"
                                   "path/c"})
                   (assoc-in [:aliases
                              :m]
                             nil))
               (-> ($.maestro/-run ":m/a"
                                   dep+)
                   (::$.maestro/result)
                   (update :paths
                           set)))))))
