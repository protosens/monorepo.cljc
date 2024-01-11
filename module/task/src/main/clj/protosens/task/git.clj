(ns protosens.task.git

  (:require [protosens.maestro                  :as $.maestro]
            [protosens.maestro.plugin           :as $.maestro.plugin]
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

  (binding [*command-line-args* [":GOD"]]
      ($.maestro/sync))
  ($.maestro.plugin.bb/check :module/task)
  ($.maestro.plugin.clj-kondo/lint))



(defn- -intro

  []

  ($.maestro.plugin/step "Before committing, this script will:")
  (doseq [line ["Prepare `deps.edn` in `:GOD` mode"
                "Ensure that `bb.edn` is in sync with `bb.maestro.edn` and `deps.maestro.edn`"
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
