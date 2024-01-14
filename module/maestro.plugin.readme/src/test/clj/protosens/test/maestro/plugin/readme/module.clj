(ns protosens.test.maestro.plugin.readme.module

  (:import (java.io StringWriter))
  (:require [clojure.test                           :as T]
            [protosens.edn.read                     :as $.edn.read]
            [protosens.maestro.plugin.readme.module :as $.maestro.plugin.readme.module]))


;;;;;;;;;; Helpers


(def ^:private -maestro-definition

  (-> ($.edn.read/file "./deps.edn")
      (get-in [:aliases
               :module/maestro])))



(defn- -t-captured

  [d*]

  (let [writer (StringWriter.)]
    (binding [*out* writer]
      (T/is (string?
              (do
                @d*
                (str *out*)))))))

;;;;;;;;;; Tests


(T/deftest body

  (T/is (nil? ($.maestro.plugin.readme.module/body {})))
  
  (-t-captured (delay
                 (-> -maestro-definition
                     ($.maestro.plugin.readme.module/body)))))



(T/deftest docstring

  (T/is (nil? ($.maestro.plugin.readme.module/docstring {})))

  (-t-captured (delay
                 (-> -maestro-definition
                     ($.maestro.plugin.readme.module/docstring)))))



(T/deftest gitlib

  (T/is (nil? ($.maestro.plugin.readme.module/gitlib {}
                                                     nil)))

  (-t-captured (delay
                 ($.maestro.plugin.readme.module/gitlib
                   -maestro-definition
                   {:sha "some-sha"
                    :url "some-url"}))))



(T/deftest header

  (-t-captured (delay
                 (-> -maestro-definition
                     ($.maestro.plugin.readme.module/header)))))



(T/deftest platform+

  (T/is (nil? ($.maestro.plugin.readme.module/platform+ {})))

  (-t-captured (delay
                 (-> -maestro-definition
                     ($.maestro.plugin.readme.module/platform+)))))



(T/deftest warn-experimental

  (T/is (-> -maestro-definition
            ($.maestro.plugin.readme.module/warn-experimental)
            (nil?)))

  (-t-captured (delay
                 (-> -maestro-definition
                     (assoc :maestro/experimental?
                            true)
                     ($.maestro.plugin.readme.module/warn-experimental)))))


;;;;;;;;;;


(T/deftest alias+

  (T/is (= (list [:c
                  {:maestro/root "module/c"}])
           (-> {:aliases {:a nil
                          :b {}
                          :c {:maestro/root "module/c"}}}
               ($.maestro.plugin.readme.module/alias+)))))
