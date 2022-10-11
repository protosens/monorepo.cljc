(ns protosens.maestro.idiom.readme

  (:require [babashka.fs                    :as bb.fs]
            [clojure.java.io                :as java.io]
            [protosens.maestro              :as $.maestro]
            [protosens.maestro.idiom.stable :as $.maestro.idiom.stable]
            [protosens.git                  :as $.git]
            [protosens.string               :as $.string]))


;;;;;;;;;;


(defn body

  [alias-data]

  (let [path-body (str (alias-data :maestro/root)
                       "/doc/body.md")]
    (when (bb.fs/exists? path-body)
      (println)
      (println "---")
      (println)
      (println (slurp path-body)))))



(defn doc

  [alias-data]

  (println ($.string/realign (alias-data :maestro/doc))))



(defn git-dependency

  [alias-data]

  (when-some [artifact (alias-data :maestro.module.expose/name)]
    (let [expose @(alias-data :maestro.module/d*expose)]
      (println)
      (println "```clojure")
      (println ";; Add to dependencies in `deps.edn`:")
      (println ";;")
      (println artifact)
      (println (format "{:deps/root \"%s\""  (alias-data :maestro/root)))
      (println (format " :git/sha   \"%s\""  (expose :maestro.module.expose/sha)))
      (println (format " :git/tag   \"%s\""  (expose :maestro.module.expose/tag)))
      (println (format " :git/url   \"%s\"}" (expose :maestro.module.expose/url)))
      (println "```"))))



(defn header

  [alias-data]

  (let [root           (alias-data :maestro/root)
        path-changelog (or (alias-data :maestro.idiom.changelog.path/module)
                           (str root
                                "/doc/changelog.md"))
        path-quickdoc  (alias-data :maestro.plugin.quickdoc.path/output)]
    (println (format "# `%s`%s %s"
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

  [alias-data]

  (when-some [platform+ (not-empty (alias-data :maestro/platform+))]
    (println)
    (println "```clojure")
    (println ";; Supported platforms:")
    (println ";;")
    (println (vec (sort platform+)))
    (println "```")))
    

;;;


(defn default

  [alias-data]

  (header alias-data)
  (println)
  (doc alias-data)
  (git-dependency alias-data)
  (platform+ alias-data)
  (println)
  (body alias-data))



(defn main


  ([]

   (main nil))


  ([proto-basis]

  (let [basis     ($.maestro/ensure-basis proto-basis)
        basis-2   (assoc basis
                         :maestro.module/d*expose
                         (delay
                           (let [stable-tag (or (basis :maestro.module.expose/tag)
                                                ($.maestro.idiom.stable/latest))
                                 stable-sha ($.git/resolve stable-tag)]
                             (when (and stable-tag
                                        (not stable-sha))
                               ($.maestro/fail (str "Unable to resolve tag to a SHA: "
                                                    stable-tag)))
                             {:maestro.module.expose/sha (or (some-> stable-sha
                                                                     ($.git/shorten-sha))
                                                             "...")
                              :maestro.module.expose/tag (or stable-tag
                                                             "...")
                              :maestro.module.expose/url (basis :maestro.module.expose/url)})))
          printer (or (basis-2 :maestro.idiom.readme/print)
                      default)]
      (doseq [[alias
               alias-data] (sort-by first
                                    (basis :aliases))
              :let         [root (alias-data :maestro/root)]
              :when        root
              :let         [path-readme (str root
                                             "/README.md")]]
        (println (format "%s -> %s"
                         alias
                         path-readme))
        (with-open [writer (java.io/writer path-readme)]
          (binding [*out* writer]
            (printer (merge basis-2
                            alias-data))))))))
