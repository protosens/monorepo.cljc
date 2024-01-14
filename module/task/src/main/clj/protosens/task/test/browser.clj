(ns protosens.task.test.browser

  (:require [clojure.java.browse      :as C.java.browse]
            [protosens.maestro.plugin :as $.maestro.plugin]
            [protosens.task.shadow    :as $.task.shadow]
            [protosens.term.style     :as $.term.style]))


;;;;;;;;;;


(defn- -compile

  [compilation-mode]

  ($.maestro.plugin/step "Preparing browser tests")
  ($.task.shadow/compile-test+ compilation-mode
                               ":test/browser")
  (println))



(defn- -open-tab

  []

  (let [file "./test/browser.html"]
    ($.maestro.plugin/step (format "Opening test file in your local browser: %s"
                                   file))
    (C.java.browse/browse-url file)))


;;;;;;;;;;


(defn run

  []

  ($.maestro.plugin/intro "protosens.task.test.browser/run")
  ($.maestro.plugin/safe
    (delay
      (-compile "compile")
      (-open-tab)
      ($.maestro.plugin/done "Tests are ready"))))



(defn watch

  []

  ($.maestro.plugin/intro "protosens.task.test.browser/watch")
  ($.maestro.plugin/safe
    (delay
      (-open-tab)
      ($.maestro.plugin/step (str $.term.style/bold
                                  "After the first compilation, refresh this page"
                                  $.term.style/reset))
      (-compile "watch"))))
