(ns protosens.test.namespace

  (:require [clojure.string      :as string]
            [clojure.test        :as T]
            [protosens.namespace :as $.namespace]))


;;;;;;;;;; Private helpers


(defn- -ns+?

  [x]

  (and (seq x)
       (every? symbol?
               x)))


;;;;;;;;;; Tests


(T/deftest require-found

  ;; Note: Kaocha does something to the classpath and only test namespaces are available.

  (let [req (fn []
              ($.namespace/require-found #(when (= %
                                                   'protosens.test.namespace)
                                            '[protosens.test.namespace :as $.test.namespace])))]

    (T/is (= '([protosens.test.namespace :as $.test.namespace])
             (req))
          "Required namespaces returned")

    (T/is (= "(require [protosens.test.namespace :as $.test.namespace])"
             (string/trimr (with-out-str
                             (req))))
          "Prints what is being required")))



(T/deftest search

  (T/is (-ns+? ($.namespace/search)))

  (T/is (-ns+? ($.namespace/search ["module/namespace/src"]))))
