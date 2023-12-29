(ns protosens.test.maestro.node

  (:require [clojure.test           :as T]
            [protosens.maestro      :as $.maestro]
            [protosens.maestro.node :as $.maestro.node]))


;;;;;;;;;; Preparation


(defmethod $.maestro.node/enter
           "UNIT_TEST"

  [state node]

  (update-in state
             [::$.maestro/deps-maestro-edn
              ::result]
             conj
             node))


;;;;;;;;;;


(T/deftest enter

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
