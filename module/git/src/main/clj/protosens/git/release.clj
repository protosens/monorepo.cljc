(ns protosens.git.release

  (:require [protosens.git     :as $.git]
            [protosens.process :as $.process]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;;


(defn latest


  ([]

   (latest nil))


  ([option+]

    (when-some [sha (-> ($.git/exec ["rev-list"
                                     "--tags=release/*"
                                     "--max-count=1"]
                                    option+)
                        ($.process/out))]
      [sha
       (-> ($.git/exec ["describe"
                        "--tags"
                        sha]
                       option+)
           ($.process/out))])))



(defn tag-add

  
  ([version]

   (tag-add version
            nil))


  ([version option+]

   ($.git/tag-add (str "release/"
                       version)
                  option+)))
