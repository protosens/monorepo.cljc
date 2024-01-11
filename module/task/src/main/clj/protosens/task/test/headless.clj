(ns protosens.task.test.headless

  (:require [protosens.maestro.plugin :as $.maestro.plugin]
            [protosens.process        :as $.process]))


;;;;;;;;;;


(defn- -run

  [browser-type]

  ($.maestro.plugin/intro (format "protosens.task.test.headless/%s"
                                  browser-type))
  ($.maestro.plugin/safe
    (delay
      ($.maestro.plugin/step "Running pre-compiled headless browser tests")
      (println)
      (if (-> ($.process/shell [(format "./module/task/src/main/bash/test/headless/%s.sh"
                                        browser-type)])
              ($.process/success?))
        ($.maestro.plugin/done "All tests passed")
        ($.maestro.plugin/fail "Some tests failed")))))


;;;


(defn all

  []

  (-run "all"))



(defn chromium

  []

  (-run "chromium"))



(defn firefox

  []

  (-run "firefox"))



(defn webkit

  []

  (-run "webkit"))
