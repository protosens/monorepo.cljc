(ns protosens.test.maestro.module.expose

  "Tests `$.maestro.module.expose`."

  (:require [clojure.test                    :as T]
            [protosens.maestro.module.expose :as $.maestro.module.expose]))


;;;;;;;;;; `deps.edn` files for test


(def -deps-edn-root

  {:aliases
     (sorted-map ;; Sorted for predictability in tests.
       :m/a    {:extra-paths                ["module/a/src"]
                :maestro/require            [:ext/a
                                             :m/b
                                             :m/c
                                             :test/a]
                :maestro/root               "module/a"
                :maestro.module.expose/name 'acme/a}
       ,
       :m/b    {:extra-paths                ["module/b/src"]
                :maestro/require            [:ext/b
                                             :m/c
                                             :test/b]
                :maestro/root               "module/b"
                :maestro.module.expose/name 'acme/b}
       ,
       :m/c    {:extra-paths                ["module/c/src"]
                :maestro/require            [:test/c]
                :maestro/root               "module/c"
                :maestro.module.expose/name 'acme/c}
       ,
       :m      {:maestro/require [:ext]}
       ,
       :ext/a  {:extra-deps {'foo/lib-1 {1 2}
                             'bar/lib-2 {3 4}}}
       ,
       :ext/b  {:extra-deps {'baz/lib-3 {5 6}}}
       ,
       :test/a {}
       :test/b {}
       :test/c {})
     ,
   :maestro.module.expose/url
     "SOME_URL"})


;; Based on the root one above.


(def -deps-edn-local-a

  {:deps  {'acme/b    {:deps/root "module/b"
                       :git/sha   "SOME_SHA"
                       :git/url   "SOME_URL"}
           'acme/c    {:deps/root "module/c"
                       :git/sha   "SOME_SHA"
                       :git/url   "SOME_URL"}
           'foo/lib-1 {1 2}
           'bar/lib-2 {3 4}
           'baz/lib-3 {5 6}}
   :paths ["src"]})



(def -deps-edn-local-b

  {:deps  {'acme/c    {:deps/root "module/c"
                       :git/sha   "SOME_SHA"
                       :git/url   "SOME_URL"}
           'baz/lib-3 {5 6}}
   :paths ["src"]})



(def -deps-edn-local-c

  {:deps  {}
   :paths ["src"]})


;;;;;;;;;; Tests


(T/deftest -expose

  (let [*log (atom [])]
    (T/is (= [["module/a/deps.edn"
               -deps-edn-local-a]
              ,
              ["module/b/deps.edn"
               -deps-edn-local-b]
              ,
              ["module/c/deps.edn"
               -deps-edn-local-c]]
             ;
             (do
               ($.maestro.module.expose/-expose "SOME_SHA"
                                                (assoc -deps-edn-root
                                                       :maestro.module.expose/write
                                                       (fn [path deps-edn]
                                                         (swap! *log
                                                                conj
                                                                [path
                                                                 deps-edn]))))
               @*log)))))



(T/deftest -prepare-deps-edn

  (T/is (= {::$.maestro.module.expose/file    -deps-edn-local-a
            ::$.maestro.module.expose/path    "module/a/deps.edn"
            ::$.maestro.module.expose/require [:m/b :m/c]}
           ,
           ($.maestro.module.expose/-prepare-deps-edn -deps-edn-root
                                                      "SOME_SHA"
                                                      :m/a))
        "Success")

  (T/is (thrown? Exception
                 (-> -deps-edn-root
                     (update-in [:aliases
                                 :m/a]
                                dissoc
                                :maestro/root)
                     ($.maestro.module.expose/-prepare-deps-edn "SOME_SHA"
                                                                :m/a)))
        "Missing root directory")

  (T/is (thrown? Exception
                 (-> -deps-edn-root
                     (assoc-in [:aliases
                                :m/a
                                :extra-paths]
                               ["../foo"])
                     ($.maestro.module.expose/-prepare-deps-edn "SOME_SHA"
                                                                :m/a)))
        "Path outside of module")

   (T/is (thrown? Exception
                 (-> -deps-edn-root
                     (update-in [:aliases
                                 :m/b]
                                dissoc
                                :maestro.module.expose/name)
                     ($.maestro.module.expose/-prepare-deps-edn "SOME_SHA"
                                                                :m/a)))
        "Require local modules that is not exposed"))
