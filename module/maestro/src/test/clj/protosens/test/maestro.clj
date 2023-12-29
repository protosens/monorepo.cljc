(ns protosens.test.maestro

  (:require [clojure.string         :as C.string]
            [clojure.test           :as T]
            [protosens.maestro      :as $.maestro]
            [protosens.maestro.node :as $.maestro.node]))


;;;;;;;;;; Reusable assertions


(defn -t-path

  [message input alias-def+ path]

  (let [run (fn [f input-2]
              (-> (f input-2
                     {:aliases alias-def+})
                  (::$.maestro/path)))]
    (T/is (= path
             (run $.maestro/run
                  input)
             (run $.maestro/run-string
                  (C.string/join input)))
        message)))


;;;;;;;;;; Tests


(T/deftest run


  (T/testing

    "Alias definitions are flattened and everything from root keys is present"

    (let [dep+ {:foo     :bar
                42       24
                :aliases {:m/a {:extra-deps      {'dep/a :dep/a}
                                :extra-paths     ["path/a"]
                                :maestro/doc     "Module A"
                                :maestro/require [:m/b]}
                          ,
                          :m/b {:extra-deps      {'dep/b :dep/b}
                                :maestro/doc     "Module B"
                                :maestro/require [:m/c]}
                          ,
                          :m/c {:extra-paths     ["path/c"] 
                                :maestro/doc     "Module C"}}}]
      (T/is (= (-> dep+
                   (assoc :deps  {'dep/a :dep/a
                                  'dep/b :dep/b}
                          :paths #{"path/a"
                                   "path/c"})
                   (assoc-in [:aliases
                              :m]
                             nil))
               (-> ($.maestro/run [:m/a]
                                  dep+)
                   (::$.maestro/deps-edn)
                   (update :paths
                           set)))))))
