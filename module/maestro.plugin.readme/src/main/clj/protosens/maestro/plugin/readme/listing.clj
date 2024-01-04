(ns protosens.maestro.plugin.readme.listing

  (:require [babashka.fs                     :as bb.fs]
            [clojure.java.io                 :as C.java.io]
            [clojure.string                  :as C.string]
            [protosens.maestro.plugin        :as $.maestro.plugin]
            [protosens.maestro.plugin.gitlib :as $.maestro.plugin.gitlib]
            [protosens.string                :as $.string]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;;


(defn docstring

  [alias-definition]

  (if-some [doc (not-empty (alias-definition :maestro/doc))]
    (let [doc-2 ($.string/first-line doc)]
      (cond->
        doc-2
        (C.string/ends-with? doc-2
                             ".")
        ($.string/trunc-right 1)))
    "No description"))



(defn root

  [alias-definition]

  (-> (bb.fs/relativize "module"
                        (alias-definition :maestro/root))
      (str)))



(defn table

  [module+]

  (println "| Module | Description |")
  (println "|---|---|")
  (doseq [[alias
           definition] (sort-by first
                                module+)]
    (println (format "| [`%s`](./%s) | %s |"
                     alias
                     (root definition)
                     (docstring definition)))))


;;;


(defn exposed

  [alias->definition]

  (when-some [module+ (seq (filter (fn [[_alias definition]]
                                     ($.maestro.plugin.gitlib/exposed? definition))
                                   alias->definition))]
    (println "Publicly available as [Git dependencies](https://clojure.org/guides/deps_and_cli#_using_git_libraries)")
    (println "for [Clojure CLI](https://clojure.org/guides/deps_and_cli):")
    (println)
    (table module+)))



(defn internal

  [alias->definition]

  (when-some [module+ (seq (filter (fn [[_alias definition]]
                                     (and (:maestro/root definition)
                                          (not ($.maestro.plugin.gitlib/exposed? definition))))
                                   alias->definition))]
    (println "Internal:")
    (println)
    (table module+)))
