(ns protosens.maestro.plugin.readme.module

  (:require [babashka.fs      :as bb.fs]
            [protosens.string :as $.string]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;;


(defn body

  [alias-definition]

  (let [path-body (str (alias-definition :maestro/root)
                       "/doc/body.md")]
    (when (bb.fs/exists? path-body)
      (println)
      (println "---")
      (println)
      (println (slurp path-body)))))



(defn docstring

  [alias-definition]

  (when-some [docstring (alias-definition :maestro/doc)]
    (println ($.string/realign docstring))))



(defn gitlib

  [alias-definition exposed]

  (when-some [artifact (alias-definition :maestro.plugin.gitlib/name)]
    (println)
    (println "```clojure")
    (println ";; Add to dependencies in `deps.edn`:")
    (println ";;")
    (println artifact)
    (println (format "{:deps/root \"%s\""  (alias-definition :maestro/root)))
    (println (format " :git/sha   \"%s\""  (exposed :sha)))
    (println (format " :git/url   \"%s\"}" (exposed :url)))
    (println "```")))



(defn header

  [alias-definition]

  (let [root           (alias-definition :maestro/root)
        path-changelog (str root
                            "/doc/changelog.md")
        path-quickdoc  (alias-definition :maestro.plugin.quickdoc/output)]
    (println (format "# `%s`%s%s"
                     root
                     (if path-quickdoc
                       (format " - [API](%s)"
                               (bb.fs/relativize root
                                                 path-quickdoc))
                       "")
                     (if (bb.fs/exists? path-changelog)
                       (format " - [CHANGES](%s)"
                               (bb.fs/relativize root
                                                 path-changelog))
                       "")))))



(defn platform+

  [alias-definition]

  (when-some [platform+ (not-empty (alias-definition :maestro/platform+))]
    (println)
    (println "```clojure")
    (println ";; Supported platforms:")
    (println ";;")
    (println (vec (sort platform+)))
    (println "```")))



(defn warn-experimental

  [alias-definition]

  (when (:maestro/experimental? alias-definition)
    (println "**Attention, this is module is marked as experimental.**")
    (println)))


;;;;;;;;;;


(defn alias+

  [deps-edn]

  (for [[alias
         definition] (sort-by first
                              (deps-edn :aliases))
        :let         [root (:maestro/root definition)]
        :when        (not-empty root)]
    [alias
     definition]))
