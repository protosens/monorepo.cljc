(ns protosens.maestro.idiom.changelog

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


  ([]

   (main nil))


  ([proto-basis]

   (-> proto-basis
       ($.maestro/ensure-basis)
       (top)
       (module+))))
