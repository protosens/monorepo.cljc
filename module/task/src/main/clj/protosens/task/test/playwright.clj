(ns protosens.task.test.playwright

  (:refer-clojure :exclude [compile])
  (:require [protosens.maestro.plugin :as $.maestro.plugin]
            [protosens.task.shadow    :as $.task.shadow]))


;;;;;;;;;;


(defn compile

  [compilation-mode]

  ($.maestro.plugin/intro "protosens.task.test.playwright/compile")
  ($.maestro.plugin/safe
    (delay
      ($.maestro.plugin/step "Preparing for headless browser testing with Playwright")
      ($.task.shadow/compile-test+ compilation-mode
                                   nil
                                   ":test/playwright")
      ($.maestro.plugin/done "Tests are ready, see `test:headless:*` tasks"))))
