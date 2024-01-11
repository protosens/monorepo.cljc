(ns protosens.task.test.browser

  (:require [clojure.java.browse   :as C.java.browse]
            [protosens.task.shadow :as $.task.shadow]))


;;;;;;;;;;


(defn- -compile

  [compilation-mode]

  ($.task.shadow/run [compilation-mode
                      ":test/browser"]))



(defn- -open-tab

  []

  (C.java.browse/browse-url "test/browser.html"))


;;;;;;;;;;


(defn run

  []

  (-compile "compile")
  (-open-tab))



(defn watch

  []

  (-open-tab)
  (-compile "watch"))
