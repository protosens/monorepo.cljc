(ns protosens.test.graph.dfs

  (:require [clojure.test        :as T]
            [protosens.graph.dfs :as $.graph.dfs]))


;;;;;;;;;; Reusable assertions


(defn- -deeper

  [state node]

  ($.graph.dfs/deeper state
                      (get-in state
                              [::graph
                               node])))



(defn- -track-path

  [state x]

  (update state
          ::path
          conj
          x))


;;;


(defn- -t-enter

  [graph path root+ message]

  (T/is (= path
           (-> ($.graph.dfs/walk {::graph graph
                                  ::path  []}
                                 (fn enter [state]
                                   (let [node ($.graph.dfs/node state)]
                                     (-> state
                                         (-track-path node)
                                         (-deeper node))))
                                 root+)
               (::path)))
        message))



(defn- -t-exit

  [graph path root+ message]

  (T/is (= path
           (-> ($.graph.dfs/walk {::graph graph
                                  ::path  []}
                                 (fn enter [state]
                                   (let [node ($.graph.dfs/node state)]
                                     (-> state
                                         (-track-path node)
                                         (-deeper node))))
                                 (fn exit [state]
                                   (-track-path state
                                                [($.graph.dfs/node state)
                                                 2]))
                                 root+)
               (::path)))
        message))


;;;;;;;;;; Tests


(T/deftest deeper

  ;; Tested more extensively in [[walk]].

  (T/is (= {::$.graph.dfs/stack '([:a :b])}
           ($.graph.dfs/deeper {::$.graph.dfs/stack '()}
                               [:a :b])))

  (T/is (= {::$.graph.dfs/stack '(nil)}
           ($.graph.dfs/deeper {::$.graph.dfs/stack '()}
                               nil))))



(T/deftest node

  ;; Tested more extensively in [[walk]].

  (T/is (= :a
           ($.graph.dfs/node {::$.graph.dfs/stack '([:a])})))

  (T/is (nil? ($.graph.dfs/node {::$.graph.dfs/stack '()}))))



(T/deftest walk


  (T/testing

    "Entering"

    (T/is (= {::some :state}
             ($.graph.dfs/walk {::some :state}
                               identity
                               []))
          "No nodes to process")

    (-t-enter {}
              [:a]
              [:a]
              "1 node")

    (-t-enter {}
              [:a :b :c]
              [:a :b :c]
              "1 level, all leaves")

    (-t-enter {:a [:b]}
              [:a :b]
              [:a]
              "2 levels")

    (-t-enter {:a [:b
                   :f]
               :b [:c
                   :d]
               :c []
               :d [:e]
               :e []
               :f [:g]}
              [:a :b :c :d :e :f :g]
              [:a]
              "Several level, depth-first")

    (-t-enter {:a [:b
                   :c]
               :b [:c]}
              [:a :b :c :c]
              [:a]
              "No deduplication")

    (-t-enter {:a [:b]
               :b [:c]}
              [:a :b :c :b :c]
              [:a :b]
              "Several roots"))


  (T/testing

    "Exiting"

    (-t-exit {}
             [:a [:a 2]]
             [:a]
             "1 node")

    (-t-exit {}
             [:a [:a 2] :b [:b 2] :c [:c 2]]
             [:a :b :c]
             "1 level, all leaves")

    (-t-exit {:a [:b]}
             [:a :b [:b 2] [:a 2]]
             [:a]
             "2 levels")

    (-t-exit {:a [:b
                  :f]
              :b [:c
                  :d]
              :c []
              :d [:e]
              :e []
              :f [:g]}
             [:a :b :c [:c 2] :d :e [:e 2] [:d 2] [:b 2] :f :g [:g 2] [:f 2] [:a 2]]
             [:a]
             "Several level, depth-first")

    (-t-exit {:a [:b
                  :c]
              :b [:c]}
             [:a :b :c [:c 2] [:b 2] :c [:c 2] [:a 2]]
             [:a]
             "No deduplication")
    (-t-exit {:a [:b]
              :b [:c]}
             [:a :b :c [:c 2] [:b 2] [:a 2] :b :c [:c 2] [:b 2]]
             [:a :b]
             "Several roots")))
