(ns protosens.test.maestro.node.enter.default

  (:require [clojure.test                :as       T]
            [protosens.maestro           :as       $.maestro]
            [protosens.maestro.node      :as-alias $.maestro.node]
            [protosens.test.util.maestro :as       $.test.util.maestro]))


;;;;;;;;;;


(T/deftest enter


  (T/testing

    "Core assumptions about graph traversal"

    ($.test.util.maestro/t-path
      [:m/a]
      {:m/a {}}
      [[:m 0] [:m/a 0]]
      "No deps (but mode activated)")

    ($.test.util.maestro/t-path
      [:m/a]
      {:m/a {:maestro/require [:m/b]}
       :m/b {}}
      [[:m 0] [:m/a 0] [:m/b 1]]
      "Single dep")

    ($.test.util.maestro/t-path
      [:m/a :m/a]
      {:m/a {:maestro/require [:m/b]}
       :m/b {}}
      [[:m 0] [:m/a 0] [:m/b 1]]
      "Input deduplication")

    ($.test.util.maestro/t-path
      [:m/a :m/b]
      {:m/a {:maestro/require [:m/b]}
       :m/b {}}
      [[:m 0] [:m/a 0] [:m/b 1]]
      "Input deduplication after transitive processing")

    ($.test.util.maestro/t-path
      [:m/a]
      {:m/a {:maestro/require [:m/b]}
       :m/b {:maestro/require [:m/c]}
       :m/c {}}
      [[:m 0] [:m/a 0] [:m/b 1] [:m/c 2]]
      "Transitive dep")

    ($.test.util.maestro/t-path
      [:m/a :m/b]
      {:m/a {:maestro/require [:m/b]}
       :m/b {:maestro/require [:m/c]}
       :m/c {}}
      [[:m 0] [:m/a 0] [:m/b 1] [:m/c 2]]
      "Transitive dep with input deduplication")

    ($.test.util.maestro/t-path
      [:m/a]
      {:m/a {:maestro/require [:m/b]}
       :m/b {:maestro/require [:m/c
                               :m/d]}
       :m/c {:maestro/require [:m/d]}
       :m/d {}} 
      [[:m 0] [:m/a 0] [:m/b 1] [:m/c 2] [:m/d 3]]
      "Transitive deps with transitive deduplication")

    ($.test.util.maestro/t-path
      [:m/a]
      {:m/a {:maestro/require [:t/a]}
       :t/a {}}
      [[:m 0] [:m/a 0]]
      "Dep but relevant mode not activated")

    ($.test.util.maestro/t-path
      [:t :m/a]
      {:m/a {:maestro/require [:t/a]}
       :t/a {}}
      [[:t 0] [:m 0] [:m/a 0] [:t/a 1]]
      "Dep by preactivating relevant mode")

    ($.test.util.maestro/t-path
      [:m/a :t]
      {:m/a {:maestro/require [:t/a]}
       :t/a {}}
      [[:m 0] [:m/a 0] [:t 0]]
      "Dep by postactivating a mode")

    ($.test.util.maestro/t-path
      [:t :m/a]
      {:m/a   {:maestro/require [:t/a]}
       :t/a   {}
       :t     {:maestro/require [:e
                                 :e/lib]}
       :e/lib {}}
      [[:t 0] [:e 1] [:e/lib 1] [:m 0] [:m/a 0] [:t/a 1]]
      "Transitive deps on mode with further mode activation"))


  (T/testing

    "Circular deps allowed, processed without infinite loop"

    (doseq [[message
             def-dep+
             path]    [["Direct"
                        {:m/a {:maestro/require [:m/a
                                                 :m/b]}
                         :m/b {}}
                        [[:m 0] [:m/a 0] [:m/b 1]]]
                       ,
                       ["Transitive"
                        {:m/a {:maestro/require [:m/b
                                                 :m/c]}
                         :m/b {:maestro/require [:m/a]}
                         :m/c {}}
                        [[:m 0] [:m/a 0] [:m/b 1] [:m/c 1]]]]]
      (T/is (= path
               (-> (deref (future
                            ($.maestro/run [:m/a]
                                           {:aliases def-dep+}))
                          100
                          nil)
                   (::$.maestro.node/path)))
            message)))


  (T/testing

    "Throws when namespaced aliases are missing"

    ($.test.util.maestro/t-fail*
      ($.maestro/run [:m/a]
                     {})
      "Empty")

    ($.test.util.maestro/t-fail*
      ($.maestro/run [:m/a]
                     {:aliases {:m/a {:maestro/require [:m/b]}}})
      "Missing dep")))
