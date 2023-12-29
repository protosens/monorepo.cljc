(ns protosens.test.maestro.namespace

  (:require [clojure.test                :as T]
            [protosens.maestro.namespace :as $.maestro.namespace]))


;;;;;;;;;; Preparations



(def -state
     ($.maestro.namespace/init-state {}))


;;;;;;;;;;


(T/deftest exclude

  (T/is (false? (-> -state
                    ($.maestro.namespace/include :foo)
                    ($.maestro.namespace/exclude :foo)
                    ($.maestro.namespace/included? :foo)))
        "Excluded even if included before")

  (T/is (false? (-> -state
                    ($.maestro.namespace/exclude :foo)
                    ($.maestro.namespace/include :foo)
                    ($.maestro.namespace/included? :foo)))
        "Exclusion beats inclusion"))



(T/deftest force-include

  (T/is (true? (-> -state
                   ($.maestro.namespace/exclude :foo)
                   ($.maestro.namespace/force-include :foo)
                   ($.maestro.namespace/included? :foo)))
        "Forces inclusion over exclusion"))



(T/deftest force-include+

  (T/is (= ($.maestro.namespace/force-include -state
                                              :foo)
           ($.maestro.namespace/force-include+ -state
                                               [:foo])))

  (T/is (every? (partial $.maestro.namespace/included?
                         (-> (reduce $.maestro.namespace/exclude
                                     -state
                                     [:a :b :c])
                             ($.maestro.namespace/force-include+ [:a :b :c])))
                [:a :b :c])))



(T/deftest include

  (T/is (true? (-> -state
                   ($.maestro.namespace/include "foo")
                   ($.maestro.namespace/included? "foo")))))



(T/deftest include+

  (T/is (= ($.maestro.namespace/include -state
                                        :foo)
           ($.maestro.namespace/include+ -state
                                         [:foo])))

  (T/is (every? (partial $.maestro.namespace/included?
                         ($.maestro.namespace/include+ -state
                                                       [:a :b :c]))
                [:a :b :c])))



(T/deftest included?

  (T/is (false? ($.maestro.namespace/included? -state
                                               :nope))))



(T/deftest uninclude

  (T/is (false? (-> -state
                    ($.maestro.namespace/include :foo)
                    ($.maestro.namespace/uninclude :foo)
                    ($.maestro.namespace/included? :foo)))))



(T/deftest unexclude

  (T/is (true? (-> -state
                   ($.maestro.namespace/include :foo)
                   ($.maestro.namespace/exclude :foo)
                   ($.maestro.namespace/unexclude :foo)
                   ($.maestro.namespace/included? :foo)))))



(T/deftest unexclude+

  (let [-state-2 ($.maestro.namespace/exclude -state
                                              :foo)]
    (T/is (= ($.maestro.namespace/unexclude -state-2
                                            :foo)
             ($.maestro.namespace/unexclude+ -state-2
                                             [:foo]))))

  (T/is (every? (partial $.maestro.namespace/included?
                         (-> -state
                             ($.maestro.namespace/include+ [:a :b :c])
                             ($.maestro.namespace/unexclude+ [:a :b :c])))
                [:a :b :c])))
