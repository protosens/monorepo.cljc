(ns protosens.maestro.idiom.changelog

  "Templating changelogs.

   Done using the [Selmer library](https://github.com/yogthos/Selmer)

   Biased towards a dual setup: a top changelog informs about which modules were impacted while
   each module maintains its own changelog containing details (if required).
  
   A notable usecase for templating changelogs are releases. One can have a placeholder like
   `{{ next-release }}` in those files and when releasing, template the actual tag of the next
   release everywhere.
  
   See [[main]]."

  (:require [babashka.fs       :as bb.fs]
            [protosens.maestro :as $.maestro]
            [selmer.parser     :as selmer.parser]))


;;;;;;;;;; Private


(def ^:private -dir

  ;; Current directory.

  (System/getProperty "user.dir"))



(defn- -templ

  ;; Templates a changelog file in-place.

  [path basis]

  (spit path
        (selmer.parser/render-file path
                                   ((or (basis :maestro.idiom.changelog/ctx)
                                        ($.maestro/fail "Missing function for producing a Selmer context"))
                                    basis)
                                   {:custom-resource-path -dir})))


;;;;;;;;;; Public


(defn module+

  "Templates module changelogs.
  
   Each module that needs to document changes publicly should maintain its own changelog
   containing details only relevant to that module.

   See [[main]] about options."

  
  ([]

   (module+ nil))


  ([proto-basis]

   (let [basis         ($.maestro/ensure-basis proto-basis)
         path-relative (or (basis :maestro.idiom.changelog.path/module)
                           "doc/changelog.md")]
     (doseq [[alias
              data] (sort-by first
                             (basis :aliases))
             :let   [root (data :maestro/root)]
             :when  root
             :let   [path-changelog (str root
                                         "/"
                                         path-relative)]]
       (when (bb.fs/exists? path-changelog)
         (println (format "%s -> %s"
                          alias
                          path-changelog))
         (-templ path-changelog
                 (assoc basis
                        :maestro.idiom.changelog/alias
                        alias))))
     basis)))



(defn top

  "Templates the top changelog.
  
   A repository should have a general changelog informing about what modules where impacted between
   releases.
  
   Modules should maintain their own changelogs containing details (see [[module+]]).

   See [[main]] about options."


  ([]

   (top nil))


  ([proto-basis]

   (let [basis ($.maestro/ensure-basis proto-basis)]
     (-templ (or (basis :maestro.idiom.changelog.path/top)
                 "doc/changelog.md")
             basis)
     basis)))


;;;


(defn main

  "Templates all changelogs.
  
   Successively calls [[top]] and [[module+]].

   `proto-basis` may contain the following options:

   | Key                                    | Value                                               | Mandatory? | Default                |
   |----------------------------------------|-----------------------------------------------------|------------|------------------------|
   | `:maestro.idiom.changelog/ctx`         | Function taking a basis and return a Selmer context | Yes        | `nil`                  |
   | `:maestro.idiom.changelog.path/module` | Path to module changelog in each `:maestro/root`    | No         | `\"doc/changelog.md\"` |
   | `:maestro.idiom.changelog.path/top`    | Path to top changelog                               | No         | `\"doc/changelog.md\"` |"


  ([]

   (main nil))


  ([proto-basis]

   (-> proto-basis
       ($.maestro/ensure-basis)
       (top)
       (module+))))
