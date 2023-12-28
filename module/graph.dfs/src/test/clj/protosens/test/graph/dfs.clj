(ns protosens.test.graph.dfs

  (:require [clojure.test        :as T]
            [protosens.graph.dfs :as $.graph.dfs]))


;;;;;;;;;; Helpers and reusable assertions


(defn- -deeper

  [state node]

  ($.graph.dfs/deeper state
                      (get-in state
                              [::graph
                               node])))



(defn- -track-path

  ([state x]

   (update state
           ::path
           conj
           x)))


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



(defn- -t-path

  [graph path root+ f message]

  (T/is (= path
           (-> ($.graph.dfs/walk {::graph graph
                                  ::path  []}
                                 (fn enter [state]
                                   (let [node ($.graph.dfs/node state)]
                                     (-> state
                                         (-track-path [node
                                                       (f state)])
                                         (-deeper node))))
                                 (fn exit [state]
                                   (-track-path state
                                                [($.graph.dfs/node state)
                                                 (f state)]))
                                 root+)
               (::path)))
        message))


;;;;;;;;;; Tests


(T/deftest deeper

  ;; Tested more extensively throughout this namespace.

  (T/is (= {::$.graph.dfs/stack '([:a :b])}
           ($.graph.dfs/deeper {::$.graph.dfs/stack '()}
                               [:a :b])))

  (T/is (= {::$.graph.dfs/stack '(nil)}
           ($.graph.dfs/deeper {::$.graph.dfs/stack '()}
                               nil))))



(T/deftest depth

  (T/is (= -1
           ($.graph.dfs/depth {})
           ($.graph.dfs/depth {::$.graph.dfs/stack '()}))
        "Works but should not happen since the stack is never handed out empty")

  (T/is (= 0
           ($.graph.dfs/depth {::$.graph.dfs/stack '((:a))}))
        "Root")

  (T/is (= 1
           ($.graph.dfs/depth {::$.graph.dfs/stack '((:a) (:b))}))
        "After root")

  (-t-path {:a [:b
                :c]
            :b []
            :c [:d]}
           [[:a 0] [:b 1] [:b 1] [:c 1] [:d 2] [:d 2] [:c 1] [:a 0]]
           [:a]
           $.graph.dfs/depth
           "While walking"))



(T/deftest frontier

  (T/is (= '()
           ($.graph.dfs/frontier {::$.graph.dfs/stack '()}))
        "Empty")

  (T/is (= '((:a))
           ($.graph.dfs/frontier {::$.graph.dfs/stack '((:a))}))
        "1 node")

  (-t-path {:a [:b
                :c]
            :b []
            :c []}
           [[:a '((:a))]
            [:b '((:b :c) (:a))]
            [:b '((:b :c) (:a))]
            [:c '((:c) (:a))]
            [:c '((:c) (:a))]
            [:a '((:a))]]
           [:a]
           $.graph.dfs/frontier
           "While walking"))



(T/deftest node

  ;; Tested more extensively throughout this namespace.

  (T/is (= :a
           ($.graph.dfs/node {::$.graph.dfs/stack '([:a])})
           ($.graph.dfs/node {::$.graph.dfs/stack '([:a] [:b])})))

  (T/is (nil? ($.graph.dfs/node {::$.graph.dfs/stack '()}))))



(T/deftest parent

  (T/is (nil? ($.graph.dfs/parent {})))

  (T/is (= :not-found
           ($.graph.dfs/parent {}
                               :not-found)))

  (T/testing

    "No parent at the root"

    (T/is (nil? ($.graph.dfs/parent {::$.graph.dfs/stack '((:a))})))

    (T/is (= :not-found
             ($.graph.dfs/parent {::$.graph.dfs/stack '((:a))}
                                 :not-found))))

  (T/is (= :a
           ($.graph.dfs/parent {::$.graph.dfs/stack '((:b) (:a))})
           ($.graph.dfs/parent {::$.graph.dfs/stack '((:b) (:a))}
                               :not-found)))

  (-t-path {:a [:b
                :c]
            :b []
            :c [:d]
            :d []}
           [[:a :not-found] [:b :a] [:b :a] [:c :a] [:d :c] [:d :c] [:c :a] [:a :not-found]]
           [:a]
           #($.graph.dfs/parent %
                                :not-found)
           "While walking"))



(T/deftest path

  (T/is (= '()
           ($.graph.dfs/path {}))
        "Empty")

  (T/is (= '(:a)
           ($.graph.dfs/path {::$.graph.dfs/stack '((:a 1))}))
        "Root")

  (T/is (= '(:b :a)
           ($.graph.dfs/path {::$.graph.dfs/stack '((:b 1) (:a 1))}))
        "Child")

  (-t-path {:a [:b
                :c]
            :b []
            :c [:d]
            :d []}
           ,
           [[:a '(:a)]
            [:b '(:b :a)]
            [:b '(:b :a)]
            [:c '(:c :a)]
            [:d '(:d :c :a)]
            [:d '(:d :c :a)]
            [:c '(:c :a)]
            [:a '(:a)]]
           ,
           [:a]
           $.graph.dfs/path
           "While walking"))






(T/deftest pending-sibling+

  (T/is (= '()
           ($.graph.dfs/pending-sibling+ {})
           ($.graph.dfs/pending-sibling+ {::$.graph.dfs/stack '()}))
        "Empty")

  (T/is (= '()
           ($.graph.dfs/pending-sibling+ {::$.graph.dfs/stack '((:a))}))
        "Root node without siblings")

  (T/is (= '(:b :c)
           ($.graph.dfs/pending-sibling+ {::$.graph.dfs/stack '((:a :b :c))}))
        "Root node")

  (T/is (= '()
           ($.graph.dfs/pending-sibling+ {::$.graph.dfs/stack '((:d) (:a :b :c))}))
        "Non-root without siblings")

  (T/is (= '(:e :f)
           ($.graph.dfs/pending-sibling+ {::$.graph.dfs/stack '((:d :e :f) (:a :b :c))}))
        "Non-root node")

  (-t-path {:a [:b
                :c]
            :b []
            :c [:d]
            :d []}
           [[:a '()] [:b '(:c)] [:b '(:c)] [:c '()] [:d '()] [:d '()] [:c '()] [:a '()]]
           [:a]
           $.graph.dfs/pending-sibling+
           "While walking"))



(T/deftest stop

  (T/is (= {:some :state}
           ($.graph.dfs/stop {:some               :state
                              ::$.graph.dfs/stack '((:a))})))

  (T/is (= [:a :b]
           (-> ($.graph.dfs/walk {::graph {:a [:b]
                                           :b [:c
                                               :d]
                                           :c [:e]
                                           :d []
                                           :e []}
                                  ::path  []}
                                 (fn enter [state]
                                   (let [node ($.graph.dfs/node state)]
                                     (if (= node
                                            :c)
                                       ($.graph.dfs/stop state)
                                       (-> state
                                           (-track-path node)
                                           (-deeper node)))))
                                 [:a])
               (::path)))
        "While walking, on enter")

  (T/is (= [:a :b :c :e [:e 2]]
           (-> ($.graph.dfs/walk {::graph {:a [:b]
                                           :b [:c
                                               :d]
                                           :c [:e]
                                           :d []
                                           :e []}
                                  ::path  []}
                                 (fn enter [state]
                                   (let [node ($.graph.dfs/node state)]
                                     (-> state
                                         (-track-path node)
                                         (-deeper node))))
                                 (fn exit [state]
                                   (let [node ($.graph.dfs/node state)]
                                     (if (= node
                                            :c)
                                       ($.graph.dfs/stop state)
                                       (-track-path state
                                                    [node
                                                     2]))))
                                 [:a])
               (::path)))
        "While walking, on exit"))



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
