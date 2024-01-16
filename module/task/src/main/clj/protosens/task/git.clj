(ns protosens.task.git

  (:require [protosens.maestro.plugin           :as $.maestro.plugin]
            [protosens.maestro.plugin.bb        :as $.maestro.plugin.bb]
            [protosens.maestro.plugin.clj-kondo :as $.maestro.plugin.clj-kondo]
            [protosens.process                  :as $.process]))


;;;;;;;;;; Read backwards starting from [[commit]]


(defn- -terminate

  []

  ($.maestro.plugin/intro "protosens.task.git/commit (continue)")
  ($.maestro.plugin/step "Performing actual commit")
  (println)
  (if (-> ($.process/shell (concat ["git"
                                    "commit"]
                                   *command-line-args*))
          ($.process/success?))
    ($.maestro.plugin/done "New commit performed successfully")
    ($.maestro.plugin/fail "Error while running `git commit`")))



(defn- -plugin+

  []

  ($.maestro.plugin.bb/check :module/task)
  (binding [*command-line-args* [":GOD"]]
    ($.maestro.plugin.clj-kondo/lint)))



(defn- -intro

  []

  ($.maestro.plugin/step "Before committing, this script will:")
  (doseq [line ["Ensure that `bb.edn` is in sync with `bb.maestro.edn` and `deps.edn`"
                "Lint the whole repository"]]
    ($.maestro.plugin/step 1
                           line))
  (println))


;;;


(defn commit

  []

  ($.maestro.plugin/intro "protosens.task.git/commit")
  ($.maestro.plugin/safe
    (delay
      (-intro)
      (-plugin+)
      (-terminate))))
