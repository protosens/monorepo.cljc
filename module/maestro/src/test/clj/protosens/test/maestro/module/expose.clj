(ns protosens.test.maestro.module.expose

  "Tests `$.maestro.module.expose`."

  (:require [clojure.test                    :as T]
            [protosens.maestro.module.expose :as $.maestro.module.expose]))


;;;;;;;;;;


(def basis
     {:aliases
      (sorted-map :module/a  {:extra-paths                ["a/src"]
                              :maestro/require            [:dev/a
                                                           {'release :release/a}]
                              :maestro/root               "a"
                              :maestro.module.expose/name 'org/a}
                  :dev/a     {:extra-paths     ["a/dev"]
                              :maestro/require [:module/b]}
                  :release/a {:extra-deps  {'release/a {:mvn/version "0.0.0"}}
                              :extra-paths ["a/release"]}
                  :module/b  {:extra-paths                ["b/src"]
                              :maestro/require            [:ext/c]
                              :maestro/root               "b"
                              :maestro.module.expose/name 'org/b}
                  :ext/c     {:extra-deps {'ext/c {:mvn/version "0.0.0"}}}
                  :module/d  {:extra-deps  {'d/d {:mvn/version "0.0.0"}}
                              :extra-paths ["d/src"]})
      ;;
      :maestro.module.expose/url
      "GIT-URL"})



(T/deftest -expose

  (let [*deps-edn+ (atom {})]

    (T/is (= {:module/a {:maestro/require                        [:ext/c :module/b :dev/a :release/a :module/a]
                         ::$.maestro.module.expose/deps.edn.path "a/deps.edn"}
              :module/b {:maestro/require                        [:ext/c :module/b]
                         ::$.maestro.module.expose/deps.edn.path "b/deps.edn"}}
             ($.maestro.module.expose/-expose "GIT-SHA"
                                              (assoc basis
                                                     :maestro.module.expose/write
                                                     (fn [path deps-edn]
                                                       (swap! *deps-edn+
                                                              assoc
                                                              path
                                                              deps-edn)))))
          "Expected result")

    (T/is (= {"a/deps.edn" {:deps  {'ext/c     {:mvn/version "0.0.0"}
                                    'org/b     {:deps/root "b"
                                                :git/sha   "GIT-SHA"
                                                :git/url   "GIT-URL"}
                                    'release/a {:mvn/version "0.0.0"}}
                            :paths ["dev"
                                    "release"
                                    "src"]}
              "b/deps.edn" {:deps  {'ext/c {:mvn/version "0.0.0"}}
                            :paths ["src"]}}
             @*deps-edn+)
          "Expected `deps.edn` files")))
