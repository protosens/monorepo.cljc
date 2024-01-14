(ns protosens.test.maestro.qualifier

  (:require [clojure.test                :as T]
            [protosens.maestro.qualifier :as $.maestro.qualifier]))


;;;;;;;;;; Preparations



(def -state
     ($.maestro.qualifier/init-state {}))


;;;;;;;;;;


(T/deftest exclude

  (T/is (false? (-> -state
                    ($.maestro.qualifier/include :foo)
                    ($.maestro.qualifier/exclude :foo)
                    ($.maestro.qualifier/included? :foo)))
        "Excluded even if included before")

  (T/is (false? (-> -state
                    ($.maestro.qualifier/exclude :foo)
                    ($.maestro.qualifier/include :foo)
                    ($.maestro.qualifier/included? :foo)))
        "Exclusion beats inclusion"))



(T/deftest force-include

  (T/is (true? (-> -state
                   ($.maestro.qualifier/exclude :foo)
                   ($.maestro.qualifier/force-include :foo)
                   ($.maestro.qualifier/included? :foo)))
        "Forces inclusion over exclusion"))



(T/deftest force-include+

  (T/is (= ($.maestro.qualifier/force-include -state
                                              :foo)
           ($.maestro.qualifier/force-include+ -state
                                               [:foo])))

  (T/is (every? (partial $.maestro.qualifier/included?
                         (-> (reduce $.maestro.qualifier/exclude
                                     -state
                                     [:a :b :c])
                             ($.maestro.qualifier/force-include+ [:a :b :c])))
                [:a :b :c])))



(T/deftest include

  (T/is (true? (-> -state
                   ($.maestro.qualifier/include "foo")
                   ($.maestro.qualifier/included? "foo")))))



(T/deftest include+

  (T/is (= ($.maestro.qualifier/include -state
                                        :foo)
           ($.maestro.qualifier/include+ -state
                                         [:foo])))

  (T/is (every? (partial $.maestro.qualifier/included?
                         ($.maestro.qualifier/include+ -state
                                                       [:a :b :c]))
                [:a :b :c])))



(T/deftest included?

  (T/is (false? ($.maestro.qualifier/included? -state
                                               :nope))))



(T/deftest uninclude

  (T/is (false? (-> -state
                    ($.maestro.qualifier/include :foo)
                    ($.maestro.qualifier/uninclude :foo)
                    ($.maestro.qualifier/included? :foo)))))



(T/deftest unexclude

  (T/is (true? (-> -state
                   ($.maestro.qualifier/include :foo)
                   ($.maestro.qualifier/exclude :foo)
                   ($.maestro.qualifier/unexclude :foo)
                   ($.maestro.qualifier/included? :foo)))))



(T/deftest unexclude+

  (let [-state-2 ($.maestro.qualifier/exclude -state
                                              :foo)]
    (T/is (= ($.maestro.qualifier/unexclude -state-2
                                            :foo)
             ($.maestro.qualifier/unexclude+ -state-2
                                             [:foo]))))

  (T/is (every? (partial $.maestro.qualifier/included?
                         (-> -state
                             ($.maestro.qualifier/include+ [:a :b :c])
                             ($.maestro.qualifier/unexclude+ [:a :b :c])))
                [:a :b :c])))
