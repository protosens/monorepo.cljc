(ns protosens.test.maestro.plugin.bb

  (:require [clojure.test                :as T]
            [protosens.maestro.plugin.bb :as $.maestro.plugin.bb]))


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



(def -deps-maestro-edn

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

  (T/is (nil? ($.maestro.plugin.bb/-sync :local/bb
                                         -bb-edn
                                         -bb-maestro-edn
                                         -deps-maestro-edn))
        "Nothing changed")

  (T/is (= (update -bb-edn
                   :paths
                   conj
                   "path/c")
           ,
           (-> ($.maestro.plugin.bb/-sync :local/bb
                                          -bb-edn
                                          -bb-maestro-edn
                                          (assoc-in -deps-maestro-edn
                                                    [:aliases
                                                     :m/b
                                                     :maestro/require]
                                                    [:m/c]))
               (first)))
        "Update"))
