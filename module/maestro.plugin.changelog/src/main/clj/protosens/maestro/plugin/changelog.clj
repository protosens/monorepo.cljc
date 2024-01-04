(ns protosens.maestro.plugin.changelog

  (:require [babashka.fs              :as bb.fs]
            [protosens.maestro.plugin :as $.maestro.plugin]
            [selmer.parser            :as selmer.parser]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;; Private


(def ^:private -dir

  ;; Current directory.

  (System/getProperty "user.dir"))



(defn ^:no-doc -module-path+

  [deps-maestro-edn]

  (into []
        (keep (fn [[alias definition]]
                (when-some [root (:maestro/root definition)]
                  (let [path-changelog (format "%s/doc/changelog.md"
                                               root)]
                    (when (bb.fs/exists? path-changelog)
                      [alias
                       path-changelog]))))
        (deps-maestro-edn :aliases))))



(defn- -template

  ;; Templates a file in-place.

  [path context]

  (spit path
        (selmer.parser/render-file path
                                   context
                                   {:custom-resource-path -dir}))
  context)


;;;;;;;;;; Task


(defn- -module+

  [context deps-maestro-edn]

  (doseq [[alias
           path-changelog] (-module-path+ deps-maestro-edn)]
    ($.maestro.plugin/step 1
                           (format "%s  ->  %s"
                                   alias
                                   path-changelog))
    (-template path-changelog
               context)))



(defn- -top

  [context]

  (let [path "doc/changelog.md"]
    ($.maestro.plugin/step 1
                           path)
    (-template path
               context)))


;;;


(defn template


  ([context]

   (template context
             nil))


  ([context deps-maestro-edn]

   ($.maestro.plugin/intro "maestro.plugin.changelog/template")
   ($.maestro.plugin/safe
     (delay
       ($.maestro.plugin/step "Templating changelogs:")
       (-top context)
       (-module+ context
                 (or deps-maestro-edn
                     ($.maestro.plugin/read-deps-maestro-edn)))
       ($.maestro.plugin/done "Changelogs are ready")))))
