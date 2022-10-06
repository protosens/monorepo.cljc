(ns protosens.test.namespace

  (:require [clojure.string      :as string]
            [clojure.test        :as T]
            [protosens.namespace :as $.namespace]))


;;;;;;;;;; Tests


(T/deftest from-filename

  (let [filename "foo/bar.clj"
        test     (fn [sym]
                   (T/is (= 'foo.bar
                            sym))
                   (T/is (= {:protosens.namespace/extension ".clj"}
                            (meta sym))))]

    (T/testing
      "Without root"
      (test ($.namespace/from-filename "foo/bar.clj")))

    (T/testing
      "With root"
      (let [root "root/"]
        (test ($.namespace/from-filename root
                                         (str root
                                              filename)))))))



(T/deftest in-cp-dir+

  (T/is (some (partial =
                       'protosens.test.namespace)
              ($.namespace/in-cp-dir+))))



(defn- -extension-remembered+

  ;; See [[-in-path]].

  [extension+ ns+]

  (T/is (= extension+
           (map (comp :protosens.namespace/extension
                      meta)
                ns+))
        "Extensions remembered"))



(defn- -in-path

  ;; Reused for [[in-path]] and [[in-path+]].

  [f-in-path f-path]

  (T/testing

    "Default options"

    (let [ns+ (sort (f-in-path (f-path "module/namespace/resrc/test")))]

      (T/is (= '[a
                 b
                 c]
               ns+)
            "All namespaces found")

      (-extension-remembered+ [".clj"
                               ".cljc"
                               ".cljs"]
                              ns+)))

  (T/testing

    "With given extensions"

    (let [ns+ (sort (f-in-path (f-path "module/namespace/resrc/test")
                               {:extension+ [".cljc"
                                             ".cljs"]}))]

      (T/is (= '[b
                 c]
               ns+)
            "Namespaces for extensions found")

      (-extension-remembered+ [".cljc"
                               ".cljs"]
                              ns+))))



(T/deftest in-path

  (-in-path $.namespace/in-path
            identity))



(T/deftest in-path+

  (-in-path $.namespace/in-path+
            vector))



(T/deftest require-cp-dir+

  ;; Note: Kaocha does something to the classpath and only test namespaces are available.

  (let [req (fn []
              ($.namespace/require-cp-dir+ #(when (= %
                                                     'protosens.test.namespace)
                                              '[protosens.test.namespace :as $.test.namespace])))]

    (T/is (= '([protosens.test.namespace :as $.test.namespace])
             (req))
          "Required namespaces returned")

    (T/is (= "(require [protosens.test.namespace :as $.test.namespace])"
             (string/trimr (with-out-str
                             (req))))
          "Prints what is being required")))



(T/deftest to-filename

  (T/is (= "a/b/c.clj"
           ($.namespace/to-filename 'a.b.c
                                    ".clj"))
        "Without root")

  (T/is (= "root/a/b/c.clj"
           ($.namespace/to-filename "root"
                                    'a.b.c
                                    ".clj"))
        "With root"))
