(ns protosens.maestro.plugin.kaocha

  "Maestro plugin for the [Kaocha](https://github.com/lambdaisland/kaocha) test runner.
  
   Reliably computes source and test paths for aliases you are working with.
   No need to maintain several test suites manually."

  (:require [babashka.fs       :as bb.fs]
            [protosens.maestro :as $.maestro]))


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

  [deps]

  (println)
  (println "---")
  (println)
  (println "[maestro.plugin.kaocha]")
  (println)
  (if-not (some #(= %
                   'lambdaisland/kaocha)
                (keys (deps :deps)))
    ;;
    (println "- Kaocha is not required, nothing will be done")
    ;;
    (let [alias+ (deps :aliases)
          path   (deps :maestro.plugin.kaocha/path)
          for+   (deps :maestro.plugin.kaocha/for)]
      (println "- Kaocha is required, proceeding")
      (when-not path
        ($.maestro/fail "Kaocha plugin for Maestro requires a path!"))
      (println (format "- File to reference in your Kaocha test file is `%s`"
                       path))
      (bb.fs/create-dirs (bb.fs/parent path))
      (when (or (not (coll? for+))
                (empty? for+))
        ($.maestro/fail "Kaocha plugin for Maestro requires a collection of alias namespaces to test!"))
      (let [for-2+ (reduce (fn [for-2+ kw]
                             (when-not (qualified-keyword? kw)
                               ($.maestro/fail (format "Only qualified keywords can be used to select aliases, got: %s"
                                                       (pr-str kw))))
                             (conj for-2+
                                   (namespace kw)))
                           #{}
                           for+)]
        (println "- Aliases providing test paths are namespaced with:")
        (doseq [nspace (sort for-2+)]
          (println (format "    - :%s/..."
                           nspace)))
        (spit path
              (let [alias+ (deps :aliases)]
                {:kaocha/source-paths (into []
                                            (mapcat (fn [[alias definition]]
                                                      (when-not (contains? for-2+
                                                                           (namespace alias))
                                                        (:extra-paths definition))))
                                            alias+)
                 :kaocha/test-paths   (into []
                                            (mapcat (fn [[alias definition]]
                                                      (when (contains? for-2+
                                                                       (namespace alias))
                                                        (:extra-paths definition))))
                                            alias+)})))
      (println "- Done, ready for testing")))
  deps)
