(ns protosens.task.doc

  "Generates documentation and READMEs."

  (:require [protosens.maestro.idiom.changelog :as $.maestro.idiom.changelog]
            [protosens.maestro.idiom.stable    :as $.maestro.idiom.stable]))


;;;;;;;;;; Tasks


(defn changelog+

  []

  ($.maestro.idiom.changelog/main {:maestro.idiom.changelog/ctx
                                   (constantly {:next-release (-> ($.maestro.idiom.stable/latest)
                                                                  ($.maestro.idiom.stable/tag->date))})}))
