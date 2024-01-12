(ns protosens.test.deps.edn

  "Testing `$.deps.edn`."
  
  (:refer-clojure :exclude [flatten
                            read])
  (:require [clojure.test       :as T]
            [protosens.deps.edn :as $.deps.edn]))


;;;;;;;;;; Private


(def ^:private -root

  ;; Root of the directory containing the test project with its `deps.edn`.

  "module/deps.edn/resrc/test")



(def ^:no-doc -d*deps-edn

  ;; Test `deps.edn` file.

  (delay
    ($.deps.edn/read -root)))


;;;;;;;;;; Tests


(T/deftest alias->path+

  (let [deps-edn {:aliases {:a {:extra-paths ["./a/"]}
                            :b {:extra-paths ["./b/"]}
                            :c {:extra-paths []}
                            :d {:extra-paths nil}
                            :e {}}}]
    (T/is (= {:a ["./a/"]
              :b ["./b/"]}
             ($.deps.edn/alias->path+ deps-edn))
          "All aliases")

    (T/is (= {:a ["./a/"]}
             ($.deps.edn/alias->path+ deps-edn
                                      [:a
                                       :c]))
          "Selected aliases")))



(T/deftest flatten

  (T/is (= {:deps  {}
            :paths []}
           ($.deps.edn/flatten {}))
        "Empty")


  (let [alias->definition {:b {:extra-deps  {'dep/b {3 4}}}
                           :c {:extra-paths ["path/c"]}
                           :d {:extra-deps  {'dep/d   {5 6}
                                             'dep/d-2 {7 8}}
                               :extra-paths ["path/d"
                                             "path/d-2"]}}
        deps-edn          {:aliases alias->definition
                           :deps    {'dep/a {1 2}}
                           :foo     :bar
                           :paths   ["path/a"]}]

    (T/is (= {:aliases alias->definition
              :deps    {'dep/a   {1 2}
                        'dep/b   {3 4}
                        'dep/d   {5 6}
                        'dep/d-2 {7 8}}
              :foo     :bar
              :paths   ["path/a"
                        "path/c"
                        "path/d"
                        "path/d-2"]}
             ($.deps.edn/flatten deps-edn))
          "All aliases")

    (T/is (= {:aliases alias->definition
              :deps    {'dep/a   {1 2}
                        'dep/d   {5 6}
                        'dep/d-2 {7 8}}
              :foo     :bar
              :paths   ["path/a"
                        "path/d"
                        "path/d-2"
                        "path/c"]}
             ($.deps.edn/flatten deps-edn
                                 [:d
                                  :c]))
          "Selected aliases (in order)")))



(T/deftest namespace+

  (T/is (= '(main)
           ($.deps.edn/namespace+ @-d*deps-edn))
        "Without aliases")

  (T/is (= '(extra
             main)
           (sort ($.deps.edn/namespace+ @-d*deps-edn
                                        {:alias+ [:extra]})))
        "With aliases"))



(T/deftest path+

  ;; Also tests [[extra-path+]].

  (T/is (= [(str -root
                 "/src/main")]
           ($.deps.edn/path+ @-d*deps-edn))
        "Without aliases")

  (T/is (= [(str -root
                 "/src/extra")
            (str -root
                 "/src/main")]
           (sort ($.deps.edn/path+ @-d*deps-edn
                                   [:extra])))
        "With aliases"))



(T/deftest read

  (T/is (= {:aliases   {:extra {:extra-paths ["src/extra"]}
                        :fail  {:extra-paths ["src/fail"]}}
            :deps/root -root
            :paths     ["src/main"]}
           @-d*deps-edn)))
