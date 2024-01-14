(ns protosens.test.maestro.diff

  (:require [clojure.set                       :as       C.set]
            [clojure.test                      :as       T]
            [protosens.deps.edn.diff           :as-alias $.deps.edn.diff]
            [protosens.deps.edn.diff.alias     :as-alias $.deps.edn.diff.alias]
            [protosens.deps.edn.diff.rev       :as-alias $.deps.edn.diff.rev]
            [protosens.git                     :as       $.git]
            [protosens.maestro.diff            :as       $.maestro.diff]
            [protosens.test.util.deps.edn.diff :as       $.test.util.deps.edn.diff]
            [protosens.test.util.maestro       :as       $.test.util.maestro]))


;;;;;;;;;;


(T/deftest =definition

  (T/is (true? ($.maestro.diff/=definition {:maestro/platform+ [:a
                                                                :b]}
                                           {:maestro/platform+ [:a
                                                                :b]})))

  (T/is (false? ($.maestro.diff/=definition {:maestro/platform+ [:a
                                                                 :b]}
                                            {:maestro/platform+ [:a
                                                                 :c]}))))



(T/deftest resolve-rev

  (T/is (nil? ($.maestro.diff/resolve-rev {}
                                          :rev))
        "No rev")

  (T/is (thrown? clojure.lang.ExceptionInfo
                 ($.maestro.diff/resolve-rev {:rev "AAAAAAAAAAAA"}
                                             :rev))
        "Inexistent rev")

  (T/is (= ($.git/commit-sha 0)
           ($.maestro.diff/resolve-rev {:rev "HEAD"}
                                       :rev))
        "Actual rev"))



(T/deftest init

  ($.test.util.maestro/with-new-deps-edn
    (fn [deps-edn-old deps-edn-new]
      (let [state-default ($.maestro.diff/init)
            sha-head      ($.git/commit-sha 0)]

        (T/testing

          "Defaults"

          (T/is (= (-> (deps-edn-new :aliases)
                       (keys)
                       (set))
                   (state-default ::$.deps.edn.diff.alias/unprocessed))
                "Aim to process all aliases")

          (T/is (= sha-head
                   (state-default ::$.deps.edn.diff.rev/old))
                "Aim to diff against head")

          (T/is (= deps-edn-old
                   (state-default ::$.deps.edn.diff/old))
                "Checked out old `deps.maestro.edn`")

          (T/is (nil? (state-default ::$.deps.edn.diff.rev/new))
                "AIm to diff from working tree")

          (T/is (= deps-edn-new
                   (state-default ::$.deps.edn.diff/new))
                "Checked out new `deps.maestro.edn`"))))))



(T/deftest augmented

  ($.test.util.deps.edn.diff/with-touched-path+
    (fn [_dir+ _file+]
      ($.test.util.maestro/with-new-deps-edn
        (fn [deps-edn-old deps-edn-new]
          (T/is (= {::$.deps.edn.diff.alias/=definition         $.maestro.diff/=definition
                    ::$.deps.edn.diff.alias/clean               (C.set/difference (-> (deps-edn-new :aliases)
                                                                                      (keys)
                                                                                      (set))
                                                                                  #{:added-for-testing
                                                                                    :dev
                                                                                    :module/maestro
                                                                                    :test/maestro})
                    ::$.deps.edn.diff.alias/added               #{:added-for-testing}
                    ::$.deps.edn.diff.alias/modified-definition #{:dev}
                    ::$.deps.edn.diff.alias/modified-path+      #{:module/maestro
                                                                  :test/maestro}
                    ::$.deps.edn.diff.rev/old                   ($.git/commit-sha 0)
                    ::$.deps.edn.diff/old                       deps-edn-old
                    ::$.deps.edn.diff/new                       deps-edn-new}
                   ,
                   ($.maestro.diff/augmented))))))))
