(ns protosens.test.classpath

  (:require [clojure.test        :as T]
            [protosens.classpath :as $.classpath]
            [protosens.string    :as $.string]))


;;;;;;;;;; Values


(def ^:private -cp

  ;; Fake classpath.

  (format "foo%sbar"
          ($.classpath/separator)))



(defn- -cp?

  ;; Tests if `x` looks like a classpath.

  [x]

  (T/is (string? x))
  (T/is (seq ($.classpath/split x))))


;;;;;;;;;;


(T/deftest compute

  (-cp? ($.classpath/compute nil)))



(T/deftest current

  (-cp? ($.classpath/current)))



(T/deftest pprint

  (T/is (= (str "bar"
                ($.string/newline)
                "foo"
                ($.string/newline))
           (with-out-str
             ($.classpath/pprint -cp)))))



(T/deftest split

  (T/is (= '("foo"
             "bar")
           ($.classpath/split -cp))))
