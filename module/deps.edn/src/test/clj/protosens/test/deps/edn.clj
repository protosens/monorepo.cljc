(ns protosens.test.deps.edn

  "Testing `$.deps.edn`."
  
  (:refer-clojure :exclude [read])
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
