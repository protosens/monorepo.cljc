(ns protosens.test.maestro.alias

  (:require [clojure.test                :as       T]
            [protosens.graph.dfs         :as-alias $.graph.dfs]
            [protosens.maestro           :as-alias $.maestro]
            [protosens.maestro.alias     :as       $.maestro.alias]
            [protosens.maestro.namespace :as       $.maestro.namespace]
            [protosens.maestro.node      :as-alias $.maestro.node]))


;;;;;;;;;; Preparations


(def -definition
     {:maestro/require [:m/b
                        :t/a]})



(def -node
     :m/a)



(def -state
     (-> {::$.maestro/deps-maestro-edn {:aliases {-node -definition}}}
         ($.maestro.node/init-state [:m/a])))


;;;;;;;;;; Tests


(T/deftest accept

  (let [-state-2 ($.maestro.alias/accept -state
                                         -node)]

    (T/is (= -state-2
             ($.maestro.alias/copy -state-2
                                   -node))
          "Was already copied")
  
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



(T/deftest copy

  (T/is (= -definition
           (-> -state
               ($.maestro.alias/copy -node)
               (get-in [::$.maestro/deps-edn
                        :aliases
                        -node])))))



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



(T/deftest include?

  (T/is (false? ($.maestro.alias/include? -state
                                          :foo/bar))
        "Namespace not included")

  (T/is (true? (-> -state
                   ($.maestro.node/init-state [:foo/bar])
                   ($.maestro.alias/include? :foo/bar)))
        "Namspace not included but is an input")

  (T/is (true? (-> -state
                   ($.maestro.namespace/init-state)
                   ($.maestro.namespace/include "foo")
                   ($.maestro.alias/include? :foo/bar)))
        "Namespace included"))
