(ns protosens.maestro.plugin.readme

  (:require [babashka.fs                             :as bb.fs]
            [clojure.java.io                         :as C.java.io]
            [protosens.git                           :as $.git]
            [protosens.maestro.plugin                :as $.maestro.plugin]
            [protosens.maestro.plugin.readme.listing :as $.maestro.plugin.readme.listing]
            [protosens.maestro.plugin.readme.module  :as $.maestro.plugin.readme.module]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;; Tasks


(defn listing


  []

  ($.maestro.plugin/intro "maestro.plugin.readme/listing")
  ($.maestro.plugin/safe
    (delay
      ($.maestro.plugin/step "Generating `module/README.md`")
      (let [alias->definition (-> ($.maestro.plugin/read-deps-edn)
                                  (:aliases))
            path              "module/README.md"]
        (bb.fs/create-dirs "module")
        (with-open [writer (C.java.io/writer path)]
          (binding [*out* writer]
            (println "# Modules")
            (println)
            (println "---")
            (println)
            ($.maestro.plugin.readme.listing/exposed alias->definition)
            (println)
            (println "---")
            (println)
            ($.maestro.plugin.readme.listing/internal alias->definition))))
      ($.maestro.plugin/done "`module/README.md` is ready"))))



(defn module+

  []

  ($.maestro.plugin/intro "maestro.plugin.readme/module+")
  ($.maestro.plugin/safe
    (delay
      ($.maestro.plugin/step "Generating READMEs for all modules:")
      (let [deps-edn ($.maestro.plugin/read-deps-edn)
            exposed  {:sha (or (deps-edn :maestro.plugin.gitlib/sha)
                               ($.git/commit-sha 0))
                      :url (deps-edn :maestro.plugin.gitlib/url)}]
        (doseq [[alias
                 definition] ($.maestro.plugin.readme.module/alias+ deps-edn)
                :let         [path-readme (str (definition :maestro/root)
                                               "/README.md")]]
          ($.maestro.plugin/step 1
                                 (format "%s  ->  %s"
                                         alias
                                         path-readme))
          (with-open [writer (C.java.io/writer path-readme)]
            (binding [*out* writer]
              ($.maestro.plugin.readme.module/header definition)
              (println)
              ($.maestro.plugin.readme.module/warn-experimental definition)
              ($.maestro.plugin.readme.module/docstring definition)
              ($.maestro.plugin.readme.module/gitlib definition
                                                     exposed)
              ($.maestro.plugin.readme.module/platform+ definition)
              ($.maestro.plugin.readme.module/body definition)))))
      ($.maestro.plugin/done "READMEs ready for all modules"))))
