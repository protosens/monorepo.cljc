(ns protosens.test.maestro.required

  (:refer-clojure :exclude [print])
  (:require [clojure.test               :as T]
            [protosens.maestro.alias    :as $.maestro.alias]
            [protosens.maestro.required :as $.maestro.required]))


;;;;;;;;;;


(T/deftest create-basis

  (T/is (map? ($.maestro.required/create-basis)))

  (T/is (= ($.maestro.required/create-basis)
           ($.maestro.required/create-basis {:maestro/project "./deps.edn"}))))


(T/deftest cli-arg

  (let [basis ($.maestro.required/cli-arg {:maestro/alias+   []
                                           :maestro/profile+ []}
                                          "[:a :b c ^:direct? d]")]
    
    (T/is (= {:maestro/alias+   [:a :b]
              :maestro/profile+ '[c d]}
             basis)
          "Aliases and profiles properly parsed")

    (let [[c
           d] (basis :maestro/profile+)]
      (T/testing "Accounts for metadata on profiles"

        (T/is (nil? (meta c)))

        (T/is (= {:direct? true}
                 (meta d)))))))


;;;;;;;;;;


(T/deftest search

  (let [basis     ($.maestro.required/search '{:aliases          {:a {:extra-paths     ["./a"]
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

    (T/is (= ($.maestro.alias/stringify+ required+)
             (with-out-str
               ($.maestro.required/print basis)))
          "Printing required aliases")

    (T/is (= #{"./a" "./b" "./c" "./d" "./e" "./h"}
             (set ($.maestro.alias/extra-path+ basis
                                               ($.maestro.required/by-profile+ basis
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
           ($.maestro.required/by-profile+ profile->alias+
                                           '[p-1
                                             p-2])))

  (T/is (= p-3+p-4
           ($.maestro.required/by-profile+ profile->alias+
                                           '[p-3
                                             p-4]))))


(T/deftest not-by-profile+

  (T/is (= p-3+p-4
           ($.maestro.required/not-by-profile+ profile->alias+
                                               '[p-1
                                                 p-2])))
 
  (T/is (= p-1+p-2
           ($.maestro.required/not-by-profile+ profile->alias+
                                               '[p-3
                                                 p-4])))) 

;;;;;;;;;;


(T/deftest print

  (T/is (= ":a:b:c"
           (with-out-str
             ($.maestro.required/print {:maestro/require [:a
                                                          :b
                                                          :c]})))))
