(ns protosens.task.test.node

  (:require [protosens.maestro.plugin :as $.maestro.plugin]
            [protosens.process        :as $.process]
            [protosens.task.shadow    :as $.task.shadow]))


;;;;;;;;;; Compilation


(defn- -shadow

  [task-name arg+ compilation-mode]

  ($.maestro.plugin/intro (format "protosens.task.test.node/%s"
                                  task-name))
  ($.maestro.plugin/safe
    (delay
      ($.maestro.plugin/step "Compiling CLJS tests for Node with Shadow-CLJS")
      ($.maestro.plugin/step (format "Compilation mode is `%s`"
                                     compilation-mode))
      (println)
      ($.task.shadow/run (concat arg+
                                 [compilation-mode
                                  ":test/node"])))))



(defn -compile

  [task-name compilation-mode]

  (-shadow task-name
           nil
           compilation-mode)
  ($.maestro.plugin/done "Tests are compiled and can be run with task `test:node`"))



(defn advanced

  []

  (-compile "advanced"
            "release"))



(defn simple

  []

  (-compile "simple"
            "compile"))


;;;;;;;;;; Running


(defn run

  []

  ($.maestro.plugin/intro "protosens.task.test.node/run")
  ($.maestro.plugin/safe
    (delay
      (let [js-file "./private/tmp/test/node.js"]
        ($.maestro.plugin/step (format "Running compiled CLJS tests: %s"
                                       js-file))
        (println)
        (if (-> ($.process/shell ["node"
                                  js-file])
                ($.process/success?))
          ($.maestro.plugin/done "All tests passed")
          ($.maestro.plugin/fail "Some tests failed"))))))



(defn watch

  []

  (-shadow "watch"
           ["--config-merge"
            "{:autorun true}"]
           "watch"))
