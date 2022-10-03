(ns protosens.test.maestro

  (:require [clojure.test      :as T]
            [protosens.maestro :as $.maestro]))


;;;;;;;;;;


(T/deftest cli-arg+

  (let [basis {:maestro/alias+   [:a]
               :maestro/profile+ ['a]}]

    (T/is (= {:maestro/alias+   [:b :a]
              :maestro/profile+ ['a]}
             ($.maestro/cli-arg+ basis
                                 [":b"]))
          "One alias")

    (T/is (= {:maestro/alias+   [:a]
              :maestro/profile+ ['b 'a]}
             ($.maestro/cli-arg+ basis
                                 ["b"]))
          "One profile")

    (T/is (= {:maestro/alias+   [:b :c :a]
              :maestro/profile+ ['b 'c 'a]}
             ($.maestro/cli-arg+ basis
                                 ["[:b b c :c]"]))
          "Vector of aliases and profiles")

    (T/is (= {:maestro/alias+   [:b :a]
              :maestro/mode     :m
              :maestro/profile+ ['a]}
             ($.maestro/cli-arg+ basis
                                 [":m" ":b"]))
          "Alias with a mode")))



(T/deftest create-basis

  (T/is (map? ($.maestro/create-basis)))

  (T/is (= ($.maestro/create-basis)
           ($.maestro/create-basis {:maestro/project "./deps.edn"}))))


;;;;;;;;;;


(T/deftest search

  (let [basis     ($.maestro/search '{:aliases          {:a {:extra-paths     ["./a"]
                                                             :maestro/env     {:a a}
                                                             :maestro/require [:b
                                                                               :c
                                                                               {profile-direct :direct}]}
                                                         :b {:extra-paths     ["./b"]
                                                             :maestro/env     {:b b}
                                                             :maestro/require [{profile-direct :not-direct}]}
                                                         :c {:extra-paths     ["./c"]
                                                             :maestro/env     {:c c}
                                                             :maestro/require [:d
                                                                               {profile-1 :e
                                                                                profile-2 :f}
                                                                               {profile-3 :g}
                                                                               {default   :h
                                                                                profile-4 :i}
                                                                               {default :e}]}
                                                         :d {:extra-paths ["./d"]
                                                             :maestro/env {:d d}}
                                                         :e {:extra-paths ["./e"]
                                                             :maestro/env {:e e}}
                                                         :f {:extra-paths ["./f"]
                                                             :maestro/env {:f f}}
                                                         :g {:extra-paths ["./g"]
                                                             :maestro/env {:g g}}
                                                         :h {:extra-paths ["./h"]
                                                             :maestro/env {:h h}}
                                                         :i {:extra-paths ["./i"]
                                                             :maestro/env {:i i}}
                                                         ;;
                                                         :direct     {:extra-paths ["./direct"]
                                                                      :maestro/env {:direct direct}}
                                                         :not-direct {:extra-paths ["./not-direct"]
                                                                      :maestro/env {:non-direct non-direct}}}
                                      :maestro/alias+   [:a]
                                      :maestro/mode     :some-mode
                                      :maestro/mode+    {:some-mode {:maestro/profile+ [profile-1]}}
                                      :maestro/profile+ [profile-1
                                                         ^:direct? profile-direct]})
        required+ (basis :maestro/require)]

    (T/is (= [:b :d :e :h :c :direct :a]
             required+)
          "Aliases are required in the right order")

    (T/is (= '{:a      a
               :b      b
               :c      c
               :d      d
               :direct direct
               :e      e
               :h      h}
             (basis :maestro/env))
          "Envs from required aliases are merged")

    (T/is (= '{default        #{:a :b :c :d :e :h}
               profile-1      #{:e}
               profile-direct #{:direct}}
             (basis :maestro/profile->alias+))
          "Keeps track of which profiles resulted in selecting which aliases")

    (T/is (= #{"./a" "./b" "./c" "./d" "./e" "./h"}
             (set ($.maestro/extra-path+ basis
                                         ($.maestro/by-profile+ basis
                                                                '[default]))))
          "Retrieve paths for a desired profile")))


;;;;;;;;;;


(def profile->alias+
     {:maestro/profile->alias+ '{p-1 #{:a
                                       :b}
                                 p-2 #{:c
                                       :d}
                                 p-3 #{:e
                                       :f}
                                 p-4 #{:g
                                       :h}}})

(def p-1+p-2
     #{:a
       :b
       :c
       :d})


(def p-3+p-4
     #{:e
       :f
       :g
       :h})


(T/deftest by-profile+

  (T/is (= p-1+p-2
           ($.maestro/by-profile+ profile->alias+
                                  '[p-1
                                    p-2])))

  (T/is (= p-3+p-4
           ($.maestro/by-profile+ profile->alias+
                                  '[p-3
                                    p-4]))))


(T/deftest not-by-profile+

  (T/is (= p-3+p-4
           ($.maestro/not-by-profile+ profile->alias+
                                      '[p-1
                                        p-2])))
 
  (T/is (= p-1+p-2
           ($.maestro/not-by-profile+ profile->alias+
                                      '[p-3
                                        p-4]))))


;;;;;;;;;;


(T/deftest extra-paths+

  (T/is (= '("./a-1"
             "./a-2"
             "./c-1"
             "./c-2")
           ($.maestro/extra-path+ {:aliases {:a {:extra-paths ["./a-1"
                                                               "./a-2"]}
                                             :b {:extra-paths ["./b-1"
                                                               "./b-2"]} 
                                             :c {:extra-paths ["./c-1"
                                                               "./c-2"]}
                                             :d {}}}
                                  [:a
                                   :c]))))

(T/deftest stringify-required

  (T/is (= ""
           ($.maestro/stringify-required {:maestro/require []}))
        "No alias")

  (T/is (= ":a"
           ($.maestro/stringify-required {:maestro/require [:a]}))
        "One alias")

  (T/is (= ":a:b:c"
           ($.maestro/stringify-required {:maestro/require [:a
                                                            :b
                                                            :c]}))
        "Several aliases"))
