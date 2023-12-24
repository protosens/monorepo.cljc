(ns protosens.maestro.plugin.kaocha

  "Maestro plugin for the [Kaocha](https://github.com/lambdaisland/kaocha) test runner.
  
   Reliably computes source and test paths for aliases you are working with.
   No need to maintain several test suites manually."

  (:require [babashka.fs        :as bb.fs]
            [protosens.deps.edn :as $.deps.edn]
            [protosens.maestro2 :as $.maestro]))


;;;;;;;;;;


(defn run

  "Produces and EDN file supplementing the Kaocha EDN configuraton file.

   Assumes `basis` already went through [[protosens.maestro/search]].

   This EDN file will contain all source and tests paths for aliases in `:maestro/require`.
   More precisely, test paths are deduced from aliases that were required by activing the `test` profile.
   Source paths are all the remaining paths.
  
   The path for that EDN file must be provided under `:maestro.plugin.kaocha/path`. It should be
   ignored in `.gitignore` as it is not meant to be checked out.

   Your Kaocha configuration file can then refer to it (substituting `<PATH>`):

   ```clojure
   #kaocha/v1
   {:tests [#meta-merge [{:id                 :unit
                          :kaocha/ns-patterns [\".+\"]}
                         #include \"<PATH>\"]]}
   ```"

  [deps path test-set]

  (if (some #(= %
                'lambdaisland/kaocha)
            (keys (deps :deps)))
    (let [alias+ (deps :aliases)]
      (println "Preparing Kaocha...")
      (bb.fs/create-dirs (bb.fs/parent path))
      (spit path
            (let [alias+ (deps :aliases)]
              {:kaocha/source-paths (into []
                                          (mapcat (fn [[alias definition]]
                                                    (when-not (contains? test-set
                                                                         (namespace alias))
                                                      (:extra-paths definition))))
                                          alias+)
               :kaocha/test-paths   (into []
                                          (mapcat (fn [[alias definition]]
                                                    (when (contains? test-set
                                                                     (namespace alias))
                                                      (:extra-paths definition))))
                                          alias+)}))
      (println (format "Test paths prepared in '%'."
                       path)))
    (println "Kaocha not required."))
  deps)
