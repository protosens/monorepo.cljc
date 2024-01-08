(ns protosens.test.maestro.plugin.kaocha

  (:refer-clojure :exclude [sync])
  (:require [babashka.fs                     :as bb.fs]
            [clojure.test                    :as T]
            [protosens.edn.read              :as $.edn.read]
            [protosens.maestro.plugin.kaocha :as $.maestro.plugin.kaocha]
            [protosens.test.util.maestro     :as $.test.util.maestro]))


;;;;;;;;;; Preparations


(def -alias->definition

  (sorted-map
    :module/a       {:extra-paths ["module/a/"]}
    :module/b       {:extra-paths ["module/b/"]}
    :test/a         {:extra-paths ["test/a/"]}
    :test/b         {:extra-paths ["test/b/"]}
    :test.release/a {:extra-paths ["test.release/a/"]}))



(def -ns-test+
  
  #{"test"
    "test.release"})



(def -path-src+

  ["module/a/"
   "module/b/"])



(def -path-test+

  ["test/a/"
   "test/b/"
   "test.release/a/"])



(def -path-kaocha+

  {:kaocha/source-paths -path-src+
   :kaocha/test-paths   -path-test+})



(def --selector+

  [:test/_
   :test.release/foo])


;;;;;;;;;; Helpers


(defn- -output

  []

  (let [dir (str (bb.fs/temp-dir)
                 "/a/b/")]
    [dir
     (str dir
          "c.edn")]))


;;;;;;;;;; Tests


(T/deftest -keep-path+

  (T/is (= -path-test+
           ($.maestro.plugin.kaocha/-keep-path+ -alias->definition
                                                -ns-test+))
        "Test paths")

  (T/is (= -path-src+
           ($.maestro.plugin.kaocha/-keep-path+ -alias->definition
                                                (comp not
                                                      -ns-test+)))
        "Source paths"))



(T/deftest -path+

  (T/is (= -path-kaocha+
           ($.maestro.plugin.kaocha/-path+ {:aliases -alias->definition}
                                           -ns-test+))))


;;;


(T/deftest -output-path

  ($.test.util.maestro/t-fail*
    ($.maestro.plugin.kaocha/-output-path {})
    "Missing")

  (T/is (let [[dir
               path] (-output)]
          ($.maestro.plugin.kaocha/-output-path {:maestro.plugin.kaocha/path path})
          (bb.fs/exists? dir))
        "Directories created"))



(T/deftest -selector+

  ($.test.util.maestro/t-fail*
    ($.maestro.plugin.kaocha/-selector+ {})
    "Missing (1)")

  ($.test.util.maestro/t-fail*
    ($.maestro.plugin.kaocha/-selector+ {:maestro.plugin.kaocha/selector+ nil})
    "Missing (2)")

  ($.test.util.maestro/t-fail*
    ($.maestro.plugin.kaocha/-selector+ {:maestro.plugin.kaocha/selector+ 42})
    "Not sequential")

  ($.test.util.maestro/t-fail*
    ($.maestro.plugin.kaocha/-selector+ {:maestro.plugin.kaocha/selector+ []})
    "Empty")

  ($.test.util.maestro/t-fail*
    ($.maestro.plugin.kaocha/-selector+ {:maestro.plugin.kaocha/selector+ [42]})
    "Not keywords")

  ($.test.util.maestro/t-fail*
    ($.maestro.plugin.kaocha/-selector+ {:maestro.plugin.kaocha/selector+ [:foo]})
    "Not qualified keywords")

  (T/is (= #{"test"
             "test.release"}
           ($.maestro.plugin.kaocha/-selector+ {:maestro.plugin.kaocha/selector+ --selector+}))
        "Okay"))


;;;


(T/deftest -kaocha-required?

  (T/is (false? ($.maestro.plugin.kaocha/-kaocha-required? {:deps {'foo {}}})))

  (T/is (true? ($.maestro.plugin.kaocha/-kaocha-required? {:deps {'foo                 {}
                                                                  'lambdaisland/kaocha {}}}))))



(defn- --kaocha-required

  [f]

  (let [[_dir
         path] (-output)]

    (f {:aliases                         -alias->definition
        :maestro.plugin.kaocha/path      path
        :maestro.plugin.kaocha/selector+ --selector+})

    (T/is (= -path-kaocha+
             ($.edn.read/file path)))))



(T/deftest -kaocha-required

  (--kaocha-required $.maestro.plugin.kaocha/-kaocha-required))


;;;


(T/deftest sync

  (--kaocha-required $.maestro.plugin.kaocha/sync))
