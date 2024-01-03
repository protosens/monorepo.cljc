(ns protosens.bb.help.print

  "Default printers.
  
   Used by [[protosens.bb.help/print]] unless overwritten by the user."

  (:require [clojure.string :as string]))


;;;;;;;;;;


(defn no-task

  "When no task has been provided as input.
  
   Prints available tasks (documented ones)."

  [data]

  (println "These tasks have extra documentation:")
  (println)
  (doseq [task (sort-by string/lower-case
                        (keys (data :task+)))]
    (println (str "  "
                  task))))



(defn no-task+

  "When the `bb.edn` file does not have any task."

  [_data]

  (println "No tasks declared in that BB file."))



(defn not-found

  "When the given task does not exist.

   Also prints `:no-task` ([[no-task]] by default)."

  [data]

  (println "Task not found.")
  (println)
  ((get-in data
           [:printer+
            :no-task])
   data))



(defn task

  "When the given task has been found.
  
   Prints its docstring and `:protosens/doc` (if any)."

  [data]

  (when-some [docstring (data :doc)]
    (println docstring)
    (println)
    (println "---")
    (println))
  (println (or (data :body)
               "No extra documentation found for this task")))



(defn undocumented-task+

  "Prints undocumented tasks."

  [data]

  (if-some [task+ (not-empty (data :task+))]
    (do
       (println "These tasks do not have extra documentation:")
       (println)
       (doseq [task task+]
         (println (str "  "
                       task))))
    (println "All tasks have extra documentation.")))
