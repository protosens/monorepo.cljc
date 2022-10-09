(ns protosens.task.doc

  "Generates documentation and READMEs."

  (:require [babashka.fs       :as bb.fs]
            [clojure.java.io   :as java.io]
            [clojure.string    :as string]
            [protosens.maestro :as $.maestro]
            [protosens.string  :as $.string]))


;;;;;;;;;;


(defn module

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



(defn readme+

  []

  (let [basis      ($.maestro/create-basis)
        git-url    (basis :maestro.module.expose/url)
        stable-sha (not-empty (slurp "meta/stable/sha.txt"))
        stable-tag (not-empty (slurp "meta/stable/tag.txt"))]
    (doseq [data  (vals (basis :aliases))
            :let  [root (data :maestro/root)]
            :when root
            :let  [artifact      (data :maestro.module.expose/name)
                   doc           (data :maestro/doc)
                   path-quickdoc (data :maestro.plugin.quickdoc.path/output)
                   path-readme   (str root
                                      "/doc/README.md")]]
      (with-open [writer (java.io/writer (str root
                                              "/README.md"))]
        (binding [*out* writer]
          (println (format "# `%s`%s"
                           root
                           (if path-quickdoc
                             (format " - [API](%s)"
                                     (bb.fs/relativize root
                                                       path-quickdoc))
                             "")))
          (println)
          (println ($.string/realign doc))
          (when artifact
            (println)
            (println "```clojure")
            (println ";; Add to dependencies in `deps.edn`:")
            (println ";;")
            (println artifact)
            (println (format "{:deps/root \"%s\""  root))
            (println (format " :git/sha   \"%s\""  (or (some-> stable-sha
                                                               ($.string/cut-out 0
                                                                                 7))
                                                       "...")))
            (println (format " :git/tag   \"%s\""  (or stable-tag
                                                       "...")))
            (println (format " :git/url   \"%s\"}" git-url))
            (println "```"))
          (println)
          (when (bb.fs/exists? path-readme)
            (println)
            (println "---")
            (println)
            (println (slurp path-readme))))))))
