(ns protosens.maestro.idiom.readme

  "Generating READMEs for modules.
  
   This is what the Protosens monorepo uses for generating module READMEs that contain
   much needed information: how to use them as Git dependencies, which platforms they
   support, etc.

   These functions are opinionated and not meant for any situation. However, the general
   idea of general of printing these READMEs is somewhat flexible should anyone need to
   print anything else.

   See [[main]]."

  (:require [babashka.fs                    :as bb.fs]
            [clojure.java.io                :as java.io]
            [protosens.maestro              :as $.maestro]
            [protosens.maestro.idiom.stable :as $.maestro.idiom.stable]
            [protosens.git                  :as $.git]
            [protosens.string               :as $.string]
            [protosens.symbol               :as $.symbol]))


;;;;;;;;;;


(defn body

  "Prints a body of text.

   Some READMEs require only the generic information printed by other functions.
   Others require examples and explanations carefully written by a human.
  
   This function prints the file under `./doc/body.md` relative to the `maestro/root`
   of the alias if it exists."

  [alias-data]

  (let [path-body (str (alias-data :maestro/root)
                       "/doc/body.md")]
    (when (bb.fs/exists? path-body)
      (println)
      (println "---")
      (println)
      (println (slurp path-body)))))



(defn doc

  "Prints `:maestro/doc`.
  
   After realigning it."

  [alias-data]

  (println ($.string/realign (alias-data :maestro/doc))))



(defn git-dependency

  "Prints how to consume the alias as a Git dependency in `deps.edn.`.
  
   This leverages a preparation step donc in [[main]]. It merges into the input
   a delay under `:maestro.module/d*expose`. This delay resolves to a map
   containing what is necessary for specifying a full Git dependency:

   - `:maestro.module.expose/sha`
   - `:maestro.module.expose/tag`
   - `:maestro.module.expose/url`"

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

  "Prints the first line of the README.
  
   A main title mentioning the `:maestro/root` with a link to the module API
   (see the `maestro.plugin.quickdoc` plugin) and a link to the changelog.

   Changelog is presumed to be under `./doc/changelog.md` by default. The basis
   or indiviual alias data can contain `:maestro.idiom.changelog.path/module`
   specifying an alternative path."

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

  "Prints `:maestro/platform+`.
  
   Informing users which platforms this alias supports."

  [alias-data]

  (when-some [platform+ (not-empty (alias-data :maestro/platform+))]
    (println)
    (println "```clojure")
    (println ";; Supported platforms:")
    (println ";;")
    (println (vec (sort platform+)))
    (println "```")))



(defn warn-lab

  "Prints a warning if this module is experimental.
  
   An experimental module has a `:maestro.module.expose/name` such that its `name` starts
   wich `lab.`"

  [alias-data]

  (when (some-> (alias-data :maestro.module.expose/name)
                (-> (name)
                    ($.symbol/starts-with? 'lab.)))
    (println "**Attention, this is an experimental module subject to breaking changes and removal.**")
    (println)))
    

;;;


(defn default

  "Default README printer.
  
   Used by [[main]] unless overwritten.

   Successively calls:

   - [[header]]
   - [[warn-lab]]
   - [[doc]]
   - [[git-dependency]]
   - [[platform+]]
   - [[body]]"

  [alias-data]

  (header alias-data)
  (println)
  (warn-lab alias-data)
  (doc alias-data)
  (git-dependency alias-data)
  (platform+ alias-data)
  (println)
  (body alias-data))



(defn main

  "Generates READMEs for all modules.
  
   More precisely, all aliases that have a `:maestro/root` (where their README will be
   printed).

   READMEs are printed using [[default]] by default. An alternative printer function can
   be provided under `:maestro.idiom.readme/print`. It is called for each alias after
   binding `*out*` to the relevant file writer, taking only one argument: the basis merged
   with the alias date of the currently handled alias."


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
