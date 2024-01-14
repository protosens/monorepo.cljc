(ns protosens.test.maestro

  (:require [clojure.test                :as       T]
            [protosens.maestro           :as       $.maestro]
            [protosens.maestro.node      :as-alias $.maestro.node]
            [protosens.test.util.maestro :as       $.test.util.maestro]))


;;;;;;;;;;


(T/deftest run

  (let [deps-edn {:foo     :bar
                  42       24
                  :aliases {:m/a {:extra-deps      {'dep/a :dep/a}
                                  :extra-paths     ["path/a"]
                                  :maestro/doc     "Module A"
                                  :maestro/require [:m/b]}
                            ,
                            :m/b {:extra-deps      {'dep/b :dep/b}
                                  :maestro/doc     "Module B"
                                  :maestro/require [:m/c]}
                            ,
                            :m/c {:extra-paths     ["path/c"] 
                                  :maestro/doc     "Module C"}
                            ,
                            :m/d {:extra-deps      {'dep/d :dep/d}
                                  :extra-paths     ["path/d"]}}
                  :deps    {'dep/foo :dep/foo}
                  :paths   ["path/foo"]}]
    (T/is (= [[:m 0] [:m/a 0] [:m/b 1] [:m/c 2]]
             (-> ($.maestro/run [:m/a]
                                deps-edn)
                 (::$.maestro.node/path))))))



(T/deftest run-string

  ($.test.util.maestro/t-fail*
    ($.maestro/run-string "42"
                          {})
    "Not keywords"))
