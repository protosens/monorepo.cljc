(ns protosens.test.maestro.plugin.bb

  (:require [clojure.test                :as       T]
            [protosens.maestro           :as-alias $.maestro]
            [protosens.maestro.plugin.bb :as       $.maestro.plugin.bb]))


;;;;;;;;;;


(def -bb-edn

  {:deps           {'foo/lib-1 {1 2}
                    'bar/lib-2 {3 4}}
   :min-bb-version "0.8.0"
   :paths          ["path/a"
                    "path/b"]
   :tasks          {'foo {:doc  "Foo"
                          :task '(foo/run)}}})



(def -bb-maestro-edn

  (dissoc -bb-edn
          :deps
          :paths))



(def -deps-edn

  {:aliases {:m/a {:extra-deps      {'foo/lib-1 {1 2}}
                   :extra-paths     ["path/a"]
                   :maestro/require [:m/b]}
             ,
             :m/b {:extra-deps      {'bar/lib-2 {3 4}}
                   :extra-paths     ["path/b"]}
             ,
             :m/c {:extra-paths     ["path/c"]}
             ,
             :local    {:maestro/require [:m]}
             :local/bb {:maestro/require [:m/a]}}})


;;;;;;;;;;


(T/deftest -sync

  (T/is (-> {::$.maestro.plugin.bb/bb.edn         -bb-edn
             ::$.maestro.plugin.bb/bb.maestro.edn -bb-maestro-edn
             ::$.maestro.plugin.bb/node           :local/bb
             ::$.maestro/deps.edn                 -deps-edn}
            ($.maestro.plugin.bb/-sync)
            (::$.maestro.plugin.bb/bb.edn)
            (nil?))
        "Nothing changed")

  (T/is (= (update -bb-edn
                   :paths
                   conj
                   "path/c")
           ,
           (-> {::$.maestro.plugin.bb/bb.edn         -bb-edn
                ::$.maestro.plugin.bb/bb.maestro.edn -bb-maestro-edn
                ::$.maestro.plugin.bb/node           :local/bb
                ::$.maestro/deps.edn                 (assoc-in -deps-edn
                                                               [:aliases
                                                                :m/b
                                                                :maestro/require]
                                                               [:m/c])}
               ($.maestro.plugin.bb/-sync)
               (::$.maestro.plugin.bb/bb.edn)))
        "Update"))
