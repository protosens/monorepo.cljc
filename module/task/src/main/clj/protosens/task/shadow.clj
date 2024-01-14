(ns protosens.task.shadow

  (:require [protosens.maestro        :as $.maestro]
            [protosens.maestro.plugin :as $.maestro.plugin]
            [protosens.task           :as $.task]))


;;;;;;;;;;


(defn run

  []

  (let [exit-code ($.task/with-appended-alias+
                    [:ext/shadow-cljs]
                    (delay
                      ($.maestro/clj (cons (str "-M"
                                                (first *command-line-args*))
                                           (concat ["-m" "shadow.cljs.devtools.cli"]
                                                   (rest *command-line-args*))))))]
    (when (not (zero? exit-code))
      ($.maestro.plugin/fail (format "Shadow-CLJS terminated with code %d"
                                     exit-code)))
    exit-code))


;;;


(defn compile-test+

  [compilation-mode build-target]

  ($.maestro.plugin/step "Compiling CLJS tests with Shadow-CLJS")
  ($.maestro.plugin/step (format "Compilation mode is `%s`"
                                 compilation-mode))
  (binding [*command-line-args* (concat *command-line-args*
                                        [compilation-mode
                                         build-target])]
    (run)))



(defn task

  []

  ($.maestro.plugin/intro "protosens.task.shadow/task")
  ($.maestro.plugin/safe
    (delay
      ($.maestro.plugin/step "Running Shadow-CLJS with command-line arguments")
      (run)
      ($.maestro.plugin/done "Shadow-CLJS terminated normally"))))
