(ns protosens.test.deps.edn.diff.alias

  (:require [clojure.set                       :as       C.set]
            [clojure.test                      :as       T]
            [protosens.deps.edn.diff           :as-alias $.deps.edn.diff]
            [protosens.deps.edn.diff.alias     :as       $.deps.edn.diff.alias]
            [protosens.deps.edn.diff.rev       :as-alias $.deps.edn.diff.rev]
            [protosens.maestro.plugin          :as       $.maestro.plugin]
            [protosens.test.util.deps.edn.diff :as       $.test.util.deps.edn.diff]
            [protosens.test.util.maestro       :as       $.test.util.maestro]))


;;;;;;;;;;


(T/deftest init

 (T/is (= #{:a
            :b}
           (-> {::$.deps.edn.diff.alias/unprocessed [:a
                                                     :b]}
               ($.deps.edn.diff.alias/init)
               (::$.deps.edn.diff.alias/unprocessed)))
        "Unprocessed stored in a set")

 (T/is (= #{:a
            :b
            :c}
          (-> {::$.deps.edn.diff/new {:aliases {:a {}
                                                :b {}
                                                :c {}}}}
              ($.deps.edn.diff.alias/init)
              (::$.deps.edn.diff.alias/unprocessed)))))



(T/deftest mark-processed+

  (T/is (= #{:a}
           (-> {::$.deps.edn.diff.alias/unprocessed #{:a :b :c}}
               ($.deps.edn.diff.alias/mark-processed+ [:b
                                                      :c])
               (::$.deps.edn.diff.alias/unprocessed)))))



(T/deftest added

  (let [deps-edn-old {:aliases {:a {}
                                :b {}}}
        deps-edn-new {:aliases {:a {}
                                :c {}
                                :d {}}}]
    (T/is (= {::$.deps.edn.diff.alias/added       #{:c}
              ::$.deps.edn.diff.alias/unprocessed #{:a}
              ::$.deps.edn.diff/new               deps-edn-new
              ::$.deps.edn.diff/old               deps-edn-old}
             ,
             (-> {::$.deps.edn.diff.alias/unprocessed #{:a
                                                        :c}
                  ::$.deps.edn.diff/new               deps-edn-new
                  ::$.deps.edn.diff/old               deps-edn-old}
                 ($.deps.edn.diff.alias/added))))))



(T/deftest =definition?

  (T/is (true? ($.deps.edn.diff.alias/=definition nil
                                                  {})))

  (T/is (true? ($.deps.edn.diff.alias/=definition {:extra-deps {'a {1 2}}}
                                                  {:extra-deps {'a {1 2}}})))

  (T/is (true? ($.deps.edn.diff.alias/=definition {:extra-paths ["a"
                                                                 "b"]}
                                                  {:extra-paths ["a"
                                                                 "b"]})))

  (T/is (true? ($.deps.edn.diff.alias/=definition {:extra-paths ["a"
                                                                 ".///b///c/../../b//"]}
                                                  {:extra-paths ["a//////b/..///"
                                                                  "././b///"]})))

  (T/is (false? ($.deps.edn.diff.alias/=definition {:extra-deps {'a {1 2}}}
                                                   {:extra-deps {'a {100 200}}})))

  (T/is (false? ($.deps.edn.diff.alias/=definition {:extra-paths ["a"]}
                                                   {:extra-paths ["b"]}))))



(T/deftest modified-definition

  (let [deps-edn-old {:aliases {:a {:extra-paths ["a"]}
                                :b {:extra-paths ["b"]}
                                :c {:extra-paths ["c"]}
                                :d {:custom      "d"}}}
        deps-edn-new (update deps-edn-old
                             :aliases
                             #(-> %
                                  (assoc-in [:b
                                             :extra-paths]
                                            ["b-2"])
                                  (assoc-in [:d
                                             :custom]
                                            "d-2")))
        =definition  (fn [definition-old definition-new]
                       (= (definition-old :custom)
                          (definition-new :custom)))]
    (T/is (= {::$.deps.edn.diff.alias/=definition         =definition
              ::$.deps.edn.diff.alias/modified-definition #{:b
                                                            :d}
              ::$.deps.edn.diff.alias/unprocessed         #{:a}
              ::$.deps.edn.diff/old                       deps-edn-old
              ::$.deps.edn.diff/new                       deps-edn-new}
             ,
             (-> {::$.deps.edn.diff.alias/=definition =definition
                  ::$.deps.edn.diff.alias/unprocessed #{:a :b :d}
                  ::$.deps.edn.diff/old               deps-edn-old
                  ::$.deps.edn.diff/new               deps-edn-new}
                 ($.deps.edn.diff.alias/modified-definition))))))



(T/deftest modified-path+

  ($.test.util.deps.edn.diff/with-touched-path+
    (fn [_dir+ _file+]
      (let [deps-maestro-edn ($.maestro.plugin/read-deps-maestro-edn)]
        (T/is (= {::$.deps.edn.diff.alias/clean          (C.set/difference (-> (deps-maestro-edn :aliases)
                                                                               (keys)
                                                                               (set))
                                                                           #{:module/maestro
                                                                             :test/maestro})
                  ::$.deps.edn.diff.alias/modified-path+ #{:module/maestro
                                                           :test/maestro}
                  ::$.deps.edn.diff.rev/old              "HEAD"
                  ::$.deps.edn.diff/old                  deps-maestro-edn
                  ::$.deps.edn.diff/new                  deps-maestro-edn}
                 ,
                 (-> {::$.deps.edn.diff.alias/unprocessed (-> (deps-maestro-edn :aliases)
                                                              (keys)
                                                              (set))
                      ::$.deps.edn.diff.rev/old           "HEAD"
                      ::$.deps.edn.diff/old               deps-maestro-edn
                      ::$.deps.edn.diff/new               deps-maestro-edn}
                     ($.deps.edn.diff.alias/init)
                     ($.deps.edn.diff.alias/modified-path+))))))))



(T/deftest augmented

  ($.test.util.deps.edn.diff/with-touched-path+
    (fn [_dir+ _file+]
      ($.test.util.maestro/with-new-deps-maestro-edn
        (fn [deps-maestro-edn-old deps-maestro-edn-new]
          (let [unprocessed (-> (deps-maestro-edn-new :aliases)
                                (keys)
                                (set))]
            (T/is (= {::$.deps.edn.diff.alias/clean               (C.set/difference unprocessed
                                                                                    #{:added-for-testing
                                                                                      :dev
                                                                                      :module/maestro
                                                                                      :test/maestro})
                      ::$.deps.edn.diff.alias/added               #{:added-for-testing}
                      ::$.deps.edn.diff.alias/modified-definition #{:dev}
                      ::$.deps.edn.diff.alias/modified-path+      #{:module/maestro
                                                                    :test/maestro}
                      ::$.deps.edn.diff.rev/old                   "HEAD"
                      ::$.deps.edn.diff/old                       deps-maestro-edn-old
                      ::$.deps.edn.diff/new                       deps-maestro-edn-new}
                     ,
                     (-> {::$.deps.edn.diff.alias/unprocessed unprocessed
                          ::$.deps.edn.diff.rev/old           "HEAD"
                          ::$.deps.edn.diff/old               deps-maestro-edn-old
                          ::$.deps.edn.diff/new               deps-maestro-edn-new}
                         ($.deps.edn.diff.alias/augmented))))))))))



(T/deftest dirty

  (T/is (= #{:a :b :c :d :e :f}
           (-> {::$.deps.edn.diff.alias/added               #{:a :b}
                ::$.deps.edn.diff.alias/modified-definition #{:c :d}
                ::$.deps.edn.diff.alias/modified-path+      #{:e :f}}
               ($.deps.edn.diff.alias/dirty)
               (set)))))
