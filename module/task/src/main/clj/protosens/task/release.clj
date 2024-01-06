(ns protosens.task.release

  (:require [protosens.calver                   :as $.calver]
            [protosens.git                      :as $.git]
            [protosens.git.release              :as $.git.release]
            [protosens.maestro.plugin           :as $.maestro.plugin]
            [protosens.maestro.plugin.bb        :as $.maestro.plugin.bb]
            [protosens.maestro.plugin.changelog :as $.maestro.plugin.changelog]
            [protosens.maestro.plugin.gitlib    :as $.maestro.plugin.gitlib]
            [protosens.maestro.plugin.quickdoc  :as $.maestro.plugin.quickdoc]
            [protosens.maestro.plugin.readme    :as $.maestro.plugin.readme]))


;;;;;;;;;; Helpers


(defn- -intro

  [version]

  ($.maestro.plugin/step (format "Preparing new release `%s`"
                                 version))
  ($.maestro.plugin/step "This script will use Maestro plugins to:")
  ($.maestro.plugin/step 1
                         "Ensure `bb.edn` is in sync")
  ($.maestro.plugin/step 1
                         "Expose public modules as gitlibs")
  ($.maestro.plugin/step 1
                         "Generate API documentation")
  ($.maestro.plugin/step 1
                         "Template changelogs with the new version")
  ($.maestro.plugin/step 1
                         "Update `module/README.md`")
  ($.maestro.plugin/step 1
                         "Update READMEs for all modules")
  ($.maestro.plugin/done "Ready to proceed"))



(defn- -plugin+

  [version]

  ($.maestro.plugin.bb/check :module/task)
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
