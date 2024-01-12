(ns protosens.test.deps.edn.alias.definition

  (:require [clojure.test                        :as T]
            [protosens.deps.edn.alias.definition :as $.deps.edn.alias.definition]
            [protosens.path                      :as $.path]))


;;;;;;;;;;


(T/deftest extra-dep+

  (T/is (= {'a {1 2}}
           ($.deps.edn.alias.definition/extra-dep+ {:extra-deps {'a {1 2}}})))

  (T/is (nil? ($.deps.edn.alias.definition/extra-dep+ nil)))

  (T/is (nil? ($.deps.edn.alias.definition/extra-dep+ {})))

  (T/is (nil? ($.deps.edn.alias.definition/extra-dep+ {:extra-deps nil})))

  (T/is (nil? ($.deps.edn.alias.definition/extra-dep+ {:extra-deps {}}))))



(T/deftest normalized-extra-path+

  (T/is (= (into #{}
                 (map $.path/from-string)
                 ["a/b"
                  "c/d"])
           ($.deps.edn.alias.definition/normalized-extra-path+ {:extra-paths ["./a////./b//"
                                                                              "./././c/d/../d//"]})))

  (T/is (= #{}
           ($.deps.edn.alias.definition/normalized-extra-path+ nil)))

  (T/is (= #{}
           ($.deps.edn.alias.definition/normalized-extra-path+ {})))

  (T/is (= #{}
           ($.deps.edn.alias.definition/normalized-extra-path+ {:extra-paths nil})))

  (T/is (= #{}
           ($.deps.edn.alias.definition/normalized-extra-path+ {:extra-paths []}))))


;;;;;;;;;;


(T/deftest =extra-dep+

  (T/is (true? ($.deps.edn.alias.definition/=extra-dep+ nil
                                                        nil)))

  (T/is (true? ($.deps.edn.alias.definition/=extra-dep+ {}
                                                        nil)))

  (T/is (true? ($.deps.edn.alias.definition/=extra-dep+ {:extra-deps {'a {1 2}}}
                                                        {:extra-deps {'a {1 2}}})))

  (T/is (false? ($.deps.edn.alias.definition/=extra-dep+ {:extra-deps {'a {1 2}}}
                                                         nil)))

  (T/is (false? ($.deps.edn.alias.definition/=extra-dep+ {:extra-deps {'a {1 2}}}
                                                         {:extra-deps {'b {1 2}}})))

  (T/is (false? ($.deps.edn.alias.definition/=extra-dep+ {:extra-deps {'a {1 2}}}
                                                         {:extra-deps {'a {1 42}}}))))



(T/deftest =extra-path+

  (T/is (true? ($.deps.edn.alias.definition/=extra-path+ nil
                                                         nil)))

  (T/is (true? ($.deps.edn.alias.definition/=extra-path+ {}
                                                         nil)))

  (T/is (true? ($.deps.edn.alias.definition/=extra-path+ {:extra-paths []}
                                                         nil)))

  (T/is (true? ($.deps.edn.alias.definition/=extra-path+ {:extra-paths ["a"
                                                                        "b"]}
                                                         {:extra-paths ["a"
                                                                        "b"]})))

  (T/is (true? ($.deps.edn.alias.definition/=extra-path+ {:extra-paths ["a"
                                                                        "b"]}
                                                         {:extra-paths ["././a/b/c/../../../a///"
                                                                        ".///b//////"]})))
  (T/is (false? ($.deps.edn.alias.definition/=extra-path+ {:extra-paths ["a"
                                                                         "b"]}
                                                          {:extra-paths ["a"
                                                                         "C"]}))))
