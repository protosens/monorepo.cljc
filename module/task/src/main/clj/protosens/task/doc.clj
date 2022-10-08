(ns protosens.task.doc

  (:require [babashka.fs       :as bb.fs]
            [clojure.java.io   :as java.io]
            [clojure.string    :as string]
            [protosens.maestro :as $.maestro]
            [protosens.string  :as $.string]))


;;;;;;;;;;


(defn module+

  []

  (with-open [writer (java.io/writer "module/README.md")]
    (binding [*out* writer]
      (println "# Modules")
      (println)
      (println "Publicly available as [Git dependencies](https://clojure.org/guides/deps_and_cli#_using_git_libraries) for [Clojure CLI](https://clojure.org/guides/deps_and_cli):")
      (println)
      (println "| Module | Description |")
      (println "|---|---|")
      (doseq [[artifact
               doc
               root]    (sort-by first
                                 (keep (fn [data]
                                         (when-some [artifact (:maestro.module.expose/name data)]
                                           [artifact
                                            (data :maestro/doc)
                                            (data :maestro/root)]))
                                       (-> ($.maestro/create-basis)
                                           (:aliases)
                                           (vals))))]
        (println (format "| [`%s`](./%s) | %s |"
                         (name artifact)
                         (str (bb.fs/relativize "module"
                                                root))
                         (let [line (first (string/split-lines doc))]
                           (cond->
                             line
                             (string/ends-with? line
                                                ".")
                             ($.string/trunc-right 1)))))))))
