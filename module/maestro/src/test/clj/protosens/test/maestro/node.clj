(ns protosens.test.maestro.node

  (:require [clojure.test           :as T]
            [protosens.graph.dfs    :as $.graph.dfs]
            [protosens.maestro      :as $.maestro]
            [protosens.maestro.node :as $.maestro.node]))


;;;;;;;;;; Preparation


(def -state
     ($.maestro.node/init-state {}
                                [:some-input]))



(defmethod $.maestro.node/enter
           "UNIT_TEST"

  [state node]

  (update-in state
             [::$.maestro/deps-maestro-edn
              ::result]
             conj
             node))


;;;;;;;;;; Tests


(T/deftest enter

  ;; Core assumptions are tested more extensively in [[protosens.test.maestro.node.enter.shallow]].

  (T/is (= [:UNIT_TEST
            :UNIT_TEST/foo]
           ,
           (-> ($.maestro/run-string ":UNIT_TEST/foo:m/a"
                                     {:aliases
                                      {:m/a {}}
                                      ,
                                      ::result
                                      []})
               (get-in [::$.maestro/deps-maestro-edn
                        ::result])))))


;;;


(T/deftest accept


  (let [-t (fn [state]
             (T/is (true? ($.maestro.node/accepted? state
                                                    :foo)))

             (T/is (false? ($.maestro.node/rejected? state
                                                     :foo)))

             (T/is (false? ($.maestro.node/input? state
                                                  :foo))
                   "Being an input has nothing to do with being accepted")

             (T/is (true? ($.maestro.node/visited? state
                                                   :foo))))]

    (T/testing

      "Without children"

      (-t ($.maestro.node/accept -state
                                 :foo)))

    (T/testing

      "With children"

      (let [-state-2 ($.maestro.node/accept -state
                                            :foo
                                            [:a :b])]

        (-t -state-2)

        (T/is (= '([:a :b])
                 ($.graph.dfs/frontier -state-2))
              "Children scheduled")))))



(T/deftest accepted?
  
  (T/is (false? ($.maestro.node/accepted? -state
                                          :inexistent)))

  (T/is (false? ($.maestro.node/accepted? -state
                                          :some-input))))



(T/deftest input?

  (T/is (true? ($.maestro.node/input? -state
                                      :some-input)))

  (T/is (false? ($.maestro.node/input? -state
                                       :inexistent)))

  (T/is (false? (-> -state
                    ($.maestro.node/accept :foo)
                    ($.maestro.node/input? :foo)))))



(T/deftest reject

  (let [-state-2 ($.maestro.node/reject -state
                                        :foo)]

    (T/is (false? ($.maestro.node/accepted? -state-2
                                            :foo)))

    (T/is (false? ($.maestro.node/input? -state-2
                                         :foo)))

    (T/is (true? ($.maestro.node/rejected? -state-2
                                           :foo)))

    (T/is (true? ($.maestro.node/visited? -state-2
                                          :foo)))))



(T/deftest unreject+

  (let [-state-2 (-> (reduce $.maestro.node/reject
                             -state
                             [:a :b :c])
                     ($.maestro.node/unreject+ [:a :b]))]

    (T/is (false? ($.maestro.node/rejected? -state-2
                                            :a)))

    (T/is (false? ($.maestro.node/visited? -state-2
                                           :a)))

    (T/is (false? ($.maestro.node/rejected? -state-2
                                            :b)))

    (T/is (false? ($.maestro.node/visited? -state-2
                                           :b)))

    (T/is (true? ($.maestro.node/rejected? -state-2
                                           :c)))

    (T/is (true? ($.maestro.node/visited? -state-2
                                          :c)))))
