(ns protosens.task.release

  (:require [protosens.calver                   :as $.calver]
            [protosens.git                      :as $.git]
            [protosens.git.release              :as $.git.release]
            [protosens.maestro                  :as $.maestro]
            [protosens.maestro.plugin           :as $.maestro.plugin]
            [protosens.maestro.plugin.bb        :as $.maestro.plugin.bb]
            [protosens.maestro.plugin.changelog :as $.maestro.plugin.changelog]
            [protosens.maestro.plugin.clj-kondo :as $.maestro.plugin.clj-kondo]
            [protosens.maestro.plugin.gitlib    :as $.maestro.plugin.gitlib]
            [protosens.maestro.plugin.quickdoc  :as $.maestro.plugin.quickdoc]
            [protosens.maestro.plugin.readme    :as $.maestro.plugin.readme]
            [protosens.task.nvd                 :as $.task.nvd]))


;;;;;;;;;; Helpers


(defn- -intro

  [version]

  ($.maestro.plugin/step (format "Preparing new release `%s`"
                                 version))
  ($.maestro.plugin/step "This script will use Maestro plugins to:")
  (doseq [line ["Lint all modules with Clj-kondo"
                "Ensure `bb.edn` is in sync"
                "Look for vulnerabilities using NVD"
                "Expose public modules as gitlibs"
                "Generate API documentation"
                "Template changelogs with the new version"
                "Update `module/README.md`"
                "Update READMEs for all modules"]]
    ($.maestro.plugin/step 1
                           line))
  ($.maestro.plugin/done "Ready to proceed"))



(defn- -plugin+

  [version]

  (binding [*command-line-args* [":GOD"]]
    ($.maestro/sync))
  ($.maestro.plugin.clj-kondo/prepare)
  ($.maestro.plugin.clj-kondo/lint)
  ($.maestro.plugin.bb/check :module/task)
  ($.task.nvd/check)
  ($.maestro.plugin.gitlib/expose)
  ($.maestro.plugin.quickdoc/module+)
  ($.maestro.plugin.changelog/template {:next-release version})
  ($.maestro.plugin.readme/listing)
  ($.maestro.plugin.readme/module+))



(defn- -terminate

  [version]

  ($.maestro.plugin/safe
    (delay
      ($.maestro.plugin/intro "protosens.task.release/now (continue)")
      ($.maestro.plugin/step "Committing new changes")
      (when-not ($.git/add ["."])
        ($.maestro.plugin/fail "Unable to add new changes to Git"))
      (when-not ($.git/commit (format "Release `%s`"
                                      version))
        ($.maestro.plugin/fail "Unable to commit new changes"))
      (when-not ($.git.release/tag-add version)
        ($.maestro.plugin/fail "Unable to tag release commit with version"))
      (let [[sha
             tag] ($.git.release/latest)]
        ($.maestro.plugin/step (format "Release commit is `%s`"
                                       sha))
        ($.maestro.plugin/step (format "Release tag is `%s`"
                                       tag)))
      ($.maestro.plugin/done (format "Release `%s` is ready to push"
                                     version)))))


;;;;;;;;;;


(defn now

  []

  ($.maestro.plugin/intro "protosens.task.release/now")
  (let [version ($.calver/now)]
    (-intro version)
    (-plugin+ version)
    (-terminate version)))
