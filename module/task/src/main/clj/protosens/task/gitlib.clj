(ns protosens.task.gitlib

  (:require [protosens.maestro.plugin.gitlib :as $.maestro.plugin.gitlib]))


;;;;;;;;;; Data


(defn expose

  []

  ($.maestro.plugin.gitlib/run {:protosens.maestro.plugin.gitlib/url
                                "https://github.com/protosens/monorepo.cljc"}))



(defn expose-local

  []

  ($.maestro.plugin.gitlib/run {:protosens.maestro.plugin.gitlib/url
                                (System/getProperty "user.dir")}))
