(ns protosens.test.maestro.alias

  (:require [clojure.test                :as       T]
            [protosens.graph.dfs         :as       $.graph.dfs]
            [protosens.maestro           :as-alias $.maestro]
            [protosens.maestro.alias     :as       $.maestro.alias]
            [protosens.maestro.node      :as       $.maestro.node]
            [protosens.maestro.qualifier :as       $.maestro.qualifier]))


;;;;;;;;;; Preparations


(def -definition
     {:maestro/require [:m/b
                        :t/a]})



(def -node
     :m/a)



(def -state
     (-> {::$.maestro/deps.edn {:aliases {-node -definition}}}
         ($.maestro.node/init-state [:m/a])))


;;;;;;;;;; Tests


(T/deftest accept

  (let [-state-2 ($.maestro.alias/accept -state
                                         -node)]
  
    (T/is (= '((:m/b :t/a))
             ($.graph.dfs/frontier -state-2))
          "Required nodes scheduled")
  
    (T/is ($.maestro.alias/defined? -state-2
                                    -node)
          "Original definition still there")
  
    (T/is (= -definition
             ($.maestro.alias/definition -state
                                         -node))
          "Original definition did not change")))



(T/deftest accepted

  (T/is (= '(:a :b :c)
           (-> {::$.maestro/deps.edn {:aliases {:a {}
                                                :b {}
                                                :c {}}}}
               ($.maestro.node/init-state [])
               ($.maestro.alias/accept :a)
               ($.maestro.alias/accept :b)
               ($.maestro.alias/accept :c)
               ($.maestro.alias/accepted)))))



(T/deftest defined?

  (T/is (true? ($.maestro.alias/defined? -state
                                         -node)))

  (T/is (false? ($.maestro.alias/defined? -state
                                          :nope))))



(T/deftest definition

  (T/is (= -definition
           ($.maestro.alias/definition -state
                                       -node)))

  (T/is (nil? ($.maestro.alias/definition -state
                                          :nope))))



(T/deftest dependent+

  (T/is (= [:M/a :N/b :L/c]
           (-> {::$.maestro/deps.edn
                 {:aliases (sorted-map 
                             :M/a {:maestro/require [:N/b
                                                     :M/d]}
                             :N/b {:maestro/require [:N/b
                                                     :L/c
                                                     :M/d]}
                             :L/c {:maestro/require [:M/e]}
                             :M/d {}
                             :M/e {})}}
               ($.maestro.alias/dependent+ [:M/d
                                            :M/e])))
        "Without `visit?`")

  (T/is (= [:a]
           (let [state {::$.maestro/deps.edn
                         {:aliases (sorted-map
                                     :a {:maestro/require [:b
                                                           :c]}
                                     :b {:maestro/require [:c]}
                                     :c {})}}]
             (-> state
                 ($.maestro.alias/dependent+ [:c]
                                             (fn [state-2 node]
                                               (T/is (= (state-2 ::$.maestro/deps.edn)
                                                        (state ::$.maestro/deps.edn))
                                                     "State passed as expected")
                                               (not= node
                                                     :b))))))
        "With `visit?`"))



(T/deftest include?

  (T/is (false? ($.maestro.alias/include? -state
                                          :foo/bar))
        "Namespace not included")

  (T/is (true? (-> -state
                   ($.maestro.node/init-state [:foo/bar])
                   ($.maestro.alias/include? :foo/bar)))
        "Namspace not included but is an input")

  (T/is (true? (-> -state
                   ($.maestro.qualifier/init-state)
                   ($.maestro.qualifier/include "foo")
                   ($.maestro.alias/include? :foo/bar)))
        "Namespace included"))



(T/deftest inverted-graph

  (T/is (= {:b [:a
                :b]
            :c [:b]
            :d [:a
                :b]
            :e [:c]}
           ($.maestro.alias/inverted-graph {::$.maestro/deps.edn
                                              {:aliases (sorted-map 
                                                          :a {:maestro/require [:b
                                                                                :d]}
                                                          :b {:maestro/require [:b
                                                                                :c
                                                                                :d]}
                                                          :c {:maestro/require [:e]}
                                                          :d {}
                                                          :e {})}}))))


