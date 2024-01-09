(ns protosens.shadow.runner.log

  {:dev/always true}

  (:require [shadow.test     :as S.test]
            [shadow.test.env :as S.test.env]))


;;;;;;;;;;


(defn start

  []

  (-> (S.test.env/get-test-data)
      (S.test.env/reset-test-data!))
  (S.test/run-all-tests))



(defn stop

  [done]

  (done))


;;;


(defn ^:export init

  []

  (start))
