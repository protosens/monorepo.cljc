(ns protosens.maestro.plugin.kaocha

  "Maestro plugin for the [Kaocha](https://github.com/lambdaisland/kaocha) test runner.
  
   Reliably computes source and test paths for aliases you are working with.
   No need to maintain several test suites manually."

  (:refer-clojure :exclude [sync])
  (:require [babashka.fs              :as bb.fs]
            [protosens.maestro.plugin :as $.maestro.plugin]))


;;;;;;;;;; Private helpers


(defn ^:no-doc -keep-path+

  [alias->definition select-namespace?]

  (into []
        (mapcat (fn [[alias definition]]
                  (when (select-namespace? (namespace alias))
                    (:extra-paths definition))))
        alias->definition))



(defn ^:no-doc -path+

  [deps-edn namespace+]

  (let [alias->definition (deps-edn :aliases)]
    {:kaocha/source-paths (-keep-path+ alias->definition
                                       (comp not
                                             namespace+))
     :kaocha/test-paths   (-keep-path+ alias->definition
                                       namespace+)}))


;;;


(defn ^:no-doc -output-path

  [deps-edn]

  (let [path (deps-edn :maestro.plugin.kaocha/path)]
    (when-not path
      ($.maestro.plugin/fail "Output path not given"))
    ($.maestro.plugin/step (format "File to reference in your Kaocha test file being prepared at `%s`"
                                   path))
    (bb.fs/create-dirs (bb.fs/parent path))
    path))



(defn ^:no-doc -selector+

  [deps-edn]

  (let [selector+ (deps-edn :maestro.plugin.kaocha/selector+)]
    ;;
    ;; Validate.
    (some->
      (or (when (nil? selector+)
            "Selectors not provided, need to pick aliases providing test paths")
          (when-not (vector? selector+)
            "Selectors must be in a vector")
          (when (empty? selector+)
            "Vector of selectors is empty")
          (when-some [x (some #(when-not (qualified-keyword? %)
                                 %)
                              selector+)]
            (format "Only qualified keywords can be used as selectors, got: %s"
                    (pr-str x))))
      ($.maestro.plugin/fail))
    ;;
    ;; Compute.
    (reduce (fn [namespace+ selector]
              (conj namespace+
                    (namespace selector)))
            #{}
            selector+)))


;;;


(defn ^:no-doc -kaocha-required?

  [deps-edn]

  (-> deps-edn
      (:deps)
      (keys)
      (->> (some #(= %
                    'lambdaisland/kaocha)))
      (boolean)))


;;;


(defn- -kaocha-not-required

  []

  ($.maestro.plugin/done "Kaocha is not required"))



(defn ^:no-doc -kaocha-required

  [deps-edn]

  ($.maestro.plugin/step "Kaocha is required, proceeding")
  (let [namespace+ (-selector+ deps-edn)]
    ($.maestro.plugin/step "Aliases providing test paths are namespaced with:")
    (doseq [nspace (sort namespace+)]
      ($.maestro.plugin/step 1
                             (format "`:%s/...`"
                                     nspace)))
    (spit (-output-path deps-edn)
          (-path+ deps-edn
                  namespace+)))
  ($.maestro.plugin/done "Ready for testing"))


;;;;;;;;;; Task


(defn sync

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

  [deps-edn]

  ($.maestro.plugin/intro "maestro.plugin.kaocha/sync")
  ($.maestro.plugin/safe
    (delay
      (if (-kaocha-required? deps-edn)
        (-kaocha-required deps-edn)
        (-kaocha-not-required))))
  deps-edn)
