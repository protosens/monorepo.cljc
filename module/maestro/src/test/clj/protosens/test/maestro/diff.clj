(ns protosens.test.maestro.diff

  (:require [babashka.fs                 :as       bb.fs]
            [clojure.set                 :as       C.set]
            [clojure.test                :as       T]
            [protosens.edn.read          :as       $.edn.read]
            [protosens.git               :as       $.git]
            [protosens.maestro.diff      :as       $.maestro.diff]
            [protosens.maestro.diff.deps :as-alias $.maestro.diff.deps]
            [protosens.maestro.diff.rev  :as-alias $.maestro.diff.rev]
            [protosens.maestro.plugin    :as       $.maestro.plugin]
            [protosens.path              :as       $.path]))


;;;;;;;;;;


(T/deftest -rev

  (T/is (nil? ($.maestro.diff/-rev {}
                                   :rev))
        "No rev")

  (T/is (thrown? clojure.lang.ExceptionInfo
                 ($.maestro.diff/-rev {:rev "AAAAAAAAAAAA"}
                                      :rev))
        "Inexistent rev")

  (T/is (= ($.git/commit-sha 0)
           ($.maestro.diff/-rev {:rev "HEAD"}
                                :rev))
        "Actual rev"))



(defn- -with-new-deps-maestro-edn

  [f]

  (let [path     "./deps.maestro.edn"
        saved    (slurp path)
        modified (-> saved
                     ($.edn.read/string)
                     (update :aliases
                             #(-> %
                                  (assoc-in [:dev
                                             :extra-paths]
                                            ["test"])
                                  (assoc ::test
                                         {}))))]
    (try
      ;;
      (spit path
            modified)
      (f ($.maestro.plugin/read-deps-maestro-edn "HEAD")
         modified)
      ;;
      (finally
        (spit path
              saved)))))



(T/deftest init


  (T/is (= #{:a
             :b}
           (-> ($.maestro.diff/init {::$.maestro.diff/unprocessed [:a
                                                                   :b]})
               (::$.maestro.diff/unprocessed)))
        "Unprocessed stored in a set")


  (-with-new-deps-maestro-edn
    (fn [deps-maestro-edn-old deps-maestro-edn-new]
      (let [state-default ($.maestro.diff/init)
            sha-head      ($.git/commit-sha 0)]

        (T/testing

          "Defaults"

          (T/is (= (-> (deps-maestro-edn-new :aliases)
                       (keys)
                       (set))
                   (state-default ::$.maestro.diff/unprocessed))
                "Aim to process all aliases")

          (T/is (= sha-head
                   (state-default ::$.maestro.diff.rev/old))
                "Aim to diff against head")

          (T/is (= deps-maestro-edn-old
                   (state-default ::$.maestro.diff.deps/old))
                "Checked out old `deps.maestro.edn`")

          (T/is (nil? (state-default ::$.maestro.diff.rev/new))
                "AIm to diff from working tree")

          (T/is (= deps-maestro-edn-new
                   (state-default ::$.maestro.diff.deps/new))
                "Checked out new `deps.maestro.edn`"))))))



(T/deftest mark-processed+

  (T/is (= #{:a}
           (-> {::$.maestro.diff/unprocessed #{:a :b :c}}
               ($.maestro.diff/mark-processed+ [:b
                                                :c])
               (::$.maestro.diff/unprocessed)))))



(T/deftest added

  (let [deps-maestro-edn-old {:aliases {:a {}
                                        :b {}}}
        deps-maestro-edn-new {:aliases {:a {}
                                        :c {}
                                        :d {}}}]
    (T/is (= {::$.maestro.diff/added       #{:c}
              ::$.maestro.diff/unprocessed #{:a}
              ::$.maestro.diff.deps/old    deps-maestro-edn-old
              ::$.maestro.diff.deps/new    deps-maestro-edn-new}
             ,
             (-> {::$.maestro.diff/unprocessed #{:a :c}
                  ::$.maestro.diff.deps/old    deps-maestro-edn-old
                  ::$.maestro.diff.deps/new    deps-maestro-edn-new}
                 ($.maestro.diff/added))))))



(T/deftest =definition?

  (T/is (true? ($.maestro.diff/=definition? nil
                                            {})))

  (T/is (true? ($.maestro.diff/=definition? {:extra-deps {'a {1 2}}}
                                            {:extra-deps {'a {1 2}}})))

  (T/is (true? ($.maestro.diff/=definition? {:extra-paths ["a"
                                                           "b"]}
                                            {:extra-paths ["a"
                                                           "b"]})))

  (T/is (true? ($.maestro.diff/=definition? {:extra-paths ["a"
                                                           ".///b///c/../../b//"]}
                                            {:extra-paths ["a//////b/..///"
                                                           "././b///"]})))

  (T/is (false? ($.maestro.diff/=definition? {:extra-deps {'a {1 2}}}
                                             {:extra-deps {'a {100 200}}})))

  (T/is (false? ($.maestro.diff/=definition? {:extra-paths ["a"]}
                                             {:extra-paths ["b"]})))

  (T/is (false? ($.maestro.diff/=definition? {:maestro/platform+ [:a
                                                                  :b]}
                                             {:maestro/platform+ [:a
                                                                  :c]}))))



(T/deftest modified-definition

  (let [deps-maestro-edn-old {:aliases {:a {:extra-paths ["a"]}
                                        :b {:extra-paths ["b"]}
                                        :c {:extra-paths ["c"]}}}
        deps-maestro-edn-new (assoc-in deps-maestro-edn-old
                                       [:aliases
                                        :b
                                        :extra-paths]
                                       ["b-2"])]
    (T/is (= {::$.maestro.diff/modified-definition #{:b}
              ::$.maestro.diff/unprocessed         #{:a}
              ::$.maestro.diff.deps/old            deps-maestro-edn-old
              ::$.maestro.diff.deps/new            deps-maestro-edn-new}
             ,
             (-> {::$.maestro.diff/unprocessed #{:a :b}
                  ::$.maestro.diff.deps/old    deps-maestro-edn-old
                  ::$.maestro.diff.deps/new    deps-maestro-edn-new}
                 ($.maestro.diff/modified-definition))))))




(defn- -with-touched-path+

  [f]

  (let [dir      "module/maestro/src/"
        dir-main (str dir
                      "main/clj/")
        dir-test (str dir
                      "test/clj/")
        foo      (str dir-main
                      "foo")
        bar      (str dir-test
                      "bar")]
    (try
      ;;
      (doseq [file [foo
                    bar]]
        (spit file
              "Test"))
      ($.git/add [foo
                  bar])
      (f [dir-main
          dir-test]
         [foo
          bar])
      ;;
      (finally
        (doseq [file [foo
                      bar]]
          (bb.fs/delete-if-exists file)
          ($.git/exec ["reset"
                       "--"
                       file]))))))



(T/deftest diff-path+

  (-with-touched-path+
    (fn [dir+ file+]
      (T/is (= (map $.path/from-string
                    file+)
               ($.maestro.diff/diff-path+ {::$.maestro.diff.rev/old "HEAD"}
                                          dir+))))))



(T/deftest modified-path+

  (-with-touched-path+
    (fn [_dir+ _file+]
      (let [deps-maestro-edn ($.maestro.plugin/read-deps-maestro-edn)]
        (T/is (= {::$.maestro.diff/clean          (C.set/difference (-> (deps-maestro-edn :aliases)
                                                                        (keys)
                                                                        (set))
                                                                    #{:module/maestro
                                                                      :test/maestro})
                  ::$.maestro.diff/modified-path+ #{:module/maestro
                                                    :test/maestro}
                  ::$.maestro.diff.deps/old       deps-maestro-edn
                  ::$.maestro.diff.deps/new       deps-maestro-edn
                  ::$.maestro.diff.rev/old        ($.git/commit-sha 0)}
                 ,
                 (-> ($.maestro.diff/init)
                     ($.maestro.diff/modified-path+))))))))



(T/deftest run

  (-with-touched-path+
    (fn [_dir+ _file+]
      (-with-new-deps-maestro-edn
        (fn [deps-maestro-edn-old deps-maestro-edn-new]
          (T/is (= {::$.maestro.diff/clean               (C.set/difference (-> (deps-maestro-edn-new :aliases)
                                                                               (keys)
                                                                               (set))
                                                                           #{:dev
                                                                             :module/maestro
                                                                             :test/maestro
                                                                             ::test})
                    ::$.maestro.diff/added               #{::test}
                    ::$.maestro.diff/modified-definition #{:dev}
                    ::$.maestro.diff/modified-path+      #{:module/maestro
                                                           :test/maestro}
                    ::$.maestro.diff.deps/old            deps-maestro-edn-old
                    ::$.maestro.diff.deps/new            deps-maestro-edn-new
                    ::$.maestro.diff.rev/old             ($.git/commit-sha 0)}
                   ,
                   ($.maestro.diff/run))))))))



(T/deftest dirty

  (T/is (= #{:a :b :c :d :e :f}
           (-> {::$.maestro.diff/added               #{:a :b}
                ::$.maestro.diff/modified-definition #{:c :d}
                ::$.maestro.diff/modified-path+      #{:e :f}}
               ($.maestro.diff/dirty)))))
