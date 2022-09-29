(ns protosens.test.maestro.git.lib

  "Testing the generation of `deps.edn` files for modules meant to be exposed as git libraries."

  (:require [clojure.test              :as T]
            [protosens.maestro.git.lib :as $.maestro.git.lib]))


;;;;;;;;;;


(def basis
     {:aliases
      (sorted-map :module/a  {:extra-paths          ["a/src"]
                              :maestro/require      [:dev/a
                                                     {'release :release/a}]
                              :maestro/root         "a"
                              :maestro.git.lib/name 'org/a}
                  :dev/a     {:extra-paths     ["a/dev"]
                              :maestro/require [:module/b]}
                  :release/a {:extra-deps  {'release/a {:mvn/version "0.0.0"}}
                              :extra-paths ["a/release"]}
                  :module/b  {:extra-paths          ["b/src"]
                              :maestro/require      [:ext/c]
                              :maestro/root         "b"
                              :maestro.git.lib/name 'org/b}
                  :ext/c     {:extra-deps {'ext/c {:mvn/version "0.0.0"}}}
                  :module/d  {:extra-deps  {'d/d {:mvn/version "0.0.0"}}
                              :extra-paths ["d/src"]})})



(T/deftest expose

  (let [*deps-edn+ (atom {})]

    (T/is (= {:module/a {:maestro/require               [:ext/c :module/b :dev/a :release/a :module/a]
                         :maestro.git.lib.path/deps.edn "a/deps.edn"}
              :module/b {:maestro/require               [:ext/c :module/b]
                         :maestro.git.lib.path/deps.edn "b/deps.edn"}}
             ($.maestro.git.lib/expose (merge basis
                                              {:maestro.git.lib/write (fn [path deps-edn]
                                                                        (swap! *deps-edn+
                                                                               assoc
                                                                               path
                                                                               deps-edn))})))
          "Expected result")

    (T/is (= {"a/deps.edn" {:deps  {'ext/c     {:mvn/version "0.0.0"}
                                    'org/b     {:local/root "../b"}
                                    'release/a {:mvn/version "0.0.0"}}
                            :paths ["dev"
                                    "release"
                                    "src"]}
              "b/deps.edn" {:deps  {'ext/c {:mvn/version "0.0.0"}}
                            :paths ["src"]}}
             @*deps-edn+)
          "Expected `deps.edn` files")))
