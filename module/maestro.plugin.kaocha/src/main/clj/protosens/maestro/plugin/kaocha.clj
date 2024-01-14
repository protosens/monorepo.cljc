(ns protosens.maestro.plugin.kaocha

  "Maestro plugin for the [Kaocha](https://github.com/lambdaisland/kaocha) test runner.
  
   Reliably computes source and test paths for aliases you are working with.
   No need to maintain several test suites manually."

  (:refer-clojure :exclude [sync])
  (:require [babashka.fs                     :as       bb.fs]
            [clojure.java.io                 :as       C.java.io]
            [clojure.pprint                  :as       C.pprint]
            [protosens.maestro               :as       $.maestro]
            [protosens.maestro.alias         :as       $.maestro.alias]
            [protosens.maestro.plugin        :as       $.maestro.plugin]
            [protosens.maestro.plugin.kaocha :as-alias $.maestro.plugin.kaocha]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;; Private helpers


(defn- -terminate

  [state]

  ($.maestro.plugin/done "Ready for testing")
  (state ::$.maestro.plugin.kaocha/alias+))



(defn ^:no-doc -write-config

  [state]

  (let [output (state ::$.maestro.plugin.kaocha/output)]

    ($.maestro.plugin/step (format "File to reference in your Kaocha test file being prepared at %s"
                                   (pr-str output)))
    (bb.fs/create-dirs (bb.fs/parent output))
    (with-open [file (C.java.io/writer output)]
      (-> (state ::$.maestro.plugin.kaocha/config)
          (update-vals (comp vec
                             sort))
          (C.pprint/pprint file))))
  state)



(defn ^:no-doc -keep-path+

  [alias->definition alias+ select-qualifier?]

  (-> (mapcat (fn [alias]
                (when (select-qualifier? (namespace alias))
                  (get-in alias->definition
                          [alias
                           :extra-paths])))
              alias+)
      (sort)
      (vec)))



(defn ^:no-doc -prepare-config


  ([state]

   (let [deps-edn      ($.maestro.plugin/read-deps-edn)
         alias+        (or (first *command-line-args*)
                           ":GOD")
         maestro-state (binding [$.maestro.plugin/*print-path?* true]
                         ($.maestro/run-string alias+
                                               deps-edn))
         alias-2+      ($.maestro.alias/accepted maestro-state)]
     (assoc state
            ,
            ::$.maestro.plugin.kaocha/alias+
            alias-2+
            ,
            ::$.maestro.plugin.kaocha/config
            (-prepare-config deps-edn
                             alias-2+
                             (state ::$.maestro.plugin.kaocha/qualifier+)))))


  ([deps-edn alias+ qualifier+]

   (let [alias->definition (deps-edn :aliases)]
     {:kaocha/source-paths (-keep-path+ alias->definition
                                        alias+
                                        (comp not
                                              qualifier+))
      :kaocha/test-paths   (-keep-path+ alias->definition
                                        alias+
                                        qualifier+)})))



(defn ^:no-doc -prepare-qualifier+

  [state]

  (let [qualifier+ (state ::$.maestro.plugin.kaocha/qualifier+)]
    (some->
      (or (when (nil? qualifier+)
            "Qualifiers not provided, need to pick aliases providing test paths")
          (when-not (vector? qualifier+)
            "Qualifiers must be in a vector")
          (when (empty? qualifier+)
            "Vector of qualifiers is empty")
          (when-some [x (some #(when-not (keyword? %)
                                 %)
                              qualifier+)]
            (format "Given qualifier is not a keyword, got: %s"
                    (pr-str x))))
      ($.maestro.plugin/fail))
    ;;
    ;; Compute.
    (let [qualifier-2+ (reduce (fn [qualifier-2+ qualifier]
                                 (conj qualifier-2+
                                       (name qualifier)))
                               #{}
                               qualifier+)]
      ($.maestro.plugin/step "Aliases providing test paths are qualified with:")
      (doseq [qualifier (sort qualifier-2+)]
        ($.maestro.plugin/step 1
                               (format ":%s/..."
                                       qualifier)))
      (assoc state
             ::$.maestro.plugin.kaocha/qualifier+
             qualifier-2+))))


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

  [output qualifier+]

  ($.maestro.plugin/intro "maestro.plugin.kaocha/sync")
  ($.maestro.plugin/safe
    (delay
      (-> {::$.maestro.plugin.kaocha/output     output
           ::$.maestro.plugin.kaocha/qualifier+ qualifier+}
          (-prepare-qualifier+)
          (-prepare-config)
          (-write-config)
          (-terminate)))))
