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



(def -alias+

  (keys -alias->definition))



(def -path-src+

  ["module/a/"
   "module/b/"])



(def -path-test+

  ["test.release/a/"
   "test/a/"
   "test/b/"])



(def --qualifier+

  [:test
   :test.release])



(def --qualifier-str+

  (into #{}
        (map name)
        --qualifier+))


;;;;;;;;;; Test


(defn- -with-output-path

  [f]

  (let [dir  (bb.fs/create-temp-dir)
        path (str dir
                  "/a/b/c.edn")]
    (try
      (f path)
      (finally
        (bb.fs/delete-tree dir)))))



(T/deftest -write-config

  (-with-output-path
    (fn [path]
      (let [config {:foo ["a"
                          "b"]
                    :bar ["c"
                          "d"]}]
        (T/is (= config
                 (do
                   ($.maestro.plugin.kaocha/-write-config
                     {::$.maestro.plugin.kaocha/config config
                      ::$.maestro.plugin.kaocha/output path})
                   ($.edn.read/file path))))))))




(T/deftest -keep-path+

  (T/is (= -path-test+
           ($.maestro.plugin.kaocha/-keep-path+ -alias->definition
                                                -alias+
                                                --qualifier-str+))
        "Test paths")

  (T/is (= -path-src+
           ($.maestro.plugin.kaocha/-keep-path+ -alias->definition
                                                (keys -alias->definition)
                                                (comp not
                                                      --qualifier-str+)))
        "Source paths"))



(T/deftest -prepare-config

  (T/is (= {:kaocha/source-paths -path-src+
            :kaocha/test-paths   -path-test+}
           ($.maestro.plugin.kaocha/-prepare-config
             {:aliases -alias->definition}
             -alias+
             --qualifier-str+))))


;;;


(T/deftest -prepare-qualifier+

  ($.test.util.maestro/t-fail*
    ($.maestro.plugin.kaocha/-prepare-qualifier+
      {})
    "Missing")

  ($.test.util.maestro/t-fail*
    ($.maestro.plugin.kaocha/-prepare-qualifier+
      {::$.maestro.plugin.kaocha/qualifier+ 42})
    "Not sequential")

  ($.test.util.maestro/t-fail*
    ($.maestro.plugin.kaocha/-prepare-qualifier+
      {::$.maestro.plugin.kaocha/qualifier+ []})
    "Empty")

  ($.test.util.maestro/t-fail*
    ($.maestro.plugin.kaocha/-prepare-qualifier+
      {::$.maestro.plugin.kaocha/qualifier+ ['test]})
    "Not keywords")

  (T/is (= --qualifier-str+
           (-> {::$.maestro.plugin.kaocha/qualifier+ --qualifier+}
               ($.maestro.plugin.kaocha/-prepare-qualifier+)
               (::$.maestro.plugin.kaocha/qualifier+)))
        "Okay"))


;;;


(T/deftest sync

  (binding [*command-line-args* [":SHALLOW/test:test/graph.dfs"]]
    (-with-output-path
      (fn [output]
        (T/is (= {:kaocha/source-paths ["module/graph.dfs/src/main/clj/"]
                  :kaocha/test-paths   ["module/graph.dfs/src/test/clj/"]}
                 (do
                   ($.maestro.plugin.kaocha/sync output
                                                 [:test])
                   ($.edn.read/file output))))))))
