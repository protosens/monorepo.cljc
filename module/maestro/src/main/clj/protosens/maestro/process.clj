(ns protosens.maestro.process

  "About running shell commands with computed required aliases.
  
   [[protosens.maestro/task]] prints required aliases and this is often
   useful in combination with Clojure CLI, by leveraging shell substitution
   like `$()`.

   However, some shells do not understand substitutions or are confused by it
   in some environments. The [[run]] task from this namespace can be used to
   template shell commands with required aliases and running them."

  (:require [clojure.string          :as string]
            [protosens.maestro       :as $.maestro]
            [protosens.maestro.alias :as $.maestro.alias]
            [protosens.process       :as $.process]))


;;;;;;;;;;


(defn template-command

  "Templates a command to run with required aliases.

   Assumes required aliases have been computed and are present under `:maestro/require`.

   The command to template is a vector of arguments starting by the shell program to run
   located under `:maestro.process/command`.

   The default pattern to replace is `__`. An alternative one may be provided under
   `:maestro.process/pattern` (but cannot be `--`)."

  [basis]

  (let [str-alias+ (-> basis
                       (:maestro/require)
                       ($.maestro.alias/stringify+))
        pattern    (or (basis :maestro.process/pattern)
                       "__")]
    (update basis
            :maestro.process/command
            (fn [command]
              (map (fn [x]
                     (string/replace x
                                     pattern
                                     str-alias+))
                   command)))))



(defn run

  "Templates a shell command with required aliases and runs it.

   Command-line arguments are split in two at `--`. Everything before is fed
   to [[protosens.maestro/task]] to compute required aliases. Everything after
   is a command to template (see [[template-command]]).

   `basis` may contain a `:maestro.process/command` that will be prepended before
   templating.

   Eventually, the command is run and this function returns `true` if the process
   exits with a non-zero status."

  ;; Nothing is actually fed to [[protosens.maestro/task]] but it is the right
  ;; mental model.


  ([]

   (run nil))


  ([basis]

   (let [[for-maestro
          [_--
           & command]] (split-with (fn [arg]
                                     (not= arg
                                           "--"))
                                   *command-line-args*)]
     (-> ($.process/shell (-> basis
                             (cond->
                               (seq for-maestro)
                               ($.maestro/cli-arg+ for-maestro))
                             ($.maestro/search)
                             (update :maestro.process/command
                                     #(concat %
                                              command))
                             (template-command)
                             (:maestro.process/command))
                          (:maestro.process/option+ basis))
         ($.process/success?)))))
