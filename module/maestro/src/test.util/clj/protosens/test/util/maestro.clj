(ns protosens.test.util.maestro

  (:require [clojure.string           :as       C.string]
            [clojure.test             :as       T]
            [protosens.maestro        :as       $.maestro]
            [protosens.maestro.node   :as-alias $.maestro.node]
            [protosens.maestro.plugin :as       $.maestro.plugin]))


;;;;;;;;;;


(defmacro t-fail*


  ([form]

   `(t-fail* ~form
             nil))


  ([form message]

   `(T/is (= ::$.maestro.plugin/failure
             (binding [$.maestro.plugin/-*exit-on-fail?* false]
               (try
                  ~form
                  nil
                  (catch Exception ex#
                    (:type (ex-data ex#))))))
          ~message)))



(defn t-path

  [input alias-def+ path message]

  (let [run (fn [f input-2]
              (-> (f input-2
                     {:aliases alias-def+})
                  (::$.maestro.node/path)))]
    (T/is (= path
             (run $.maestro/run
                  input)
             (run $.maestro/run-string
                  (C.string/join input)))
        message)))
