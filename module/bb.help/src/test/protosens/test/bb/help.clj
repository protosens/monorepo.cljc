(ns protosens.test.bb.help

  "Tests `$.bb.help`."

  (:require [clojure.test      :as T]
            [protosens.bb.help :as $.bb.help]))


;;;;;;;;;; Values


(def -root

  ;; Path to `bb.edn` files to test.

  "module/bb.help/resrc/test/bb/")


;;;


(def -path-no-task+

  ;; Path to file without any task.

  (str -root
       "no-tasks.edn"))



(def -path-task+

  ;; Path to file with tasks.

  (str -root
       "tasks.edn"))


;;;


(def -base

  ;; Used in [[task]].

  {:task+ ($.bb.help/-task+ {:bb -path-task+})})


;;;;;;;;;;


(T/deftest task

  (T/is (= {:type :no-task+}
           ($.bb.help/task {:bb -path-no-task+}))
        "No tasks in the file")

  (T/is (= (merge -base
                  {:type :no-task})
           (binding [*command-line-args* '()]
             ($.bb.help/task {:bb -path-task+})))
        "No task provided")

  (T/is (= (merge -base
                  {:task 'inexistent
                   :type :not-found})
           ($.bb.help/task {:bb   -path-task+
                            :task 'inexistent}))
        "Task not found")

  (T/is (= (merge -base
                  {:body      "1-extra" 
                   :docstring "1-doc"
                   :task      'task-1
                   :type      :task})
           ($.bb.help/task {:bb   -path-task+
                            :task 'task-1}))
        "Task found"))



(T/deftest undocumented-task+

  (T/is (= {:task+ '()
            :type  :undocumented-task+}
           ($.bb.help/undocumented-task+ {:bb -path-no-task+}))
        "File does not contain any task")


  (T/is (= {:task+ '(task-3
                     task-4)
            :type  :undocumented-task+}
           ($.bb.help/undocumented-task+ {:bb -path-task+}))
        "Listing all tasks without extra documentation"))
