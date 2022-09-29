(ns protosens.maestro.doc

  "Collection of miscellaneous helpers related to documentation."

  (:import (java.nio.file Path))
  (:refer-clojure :exclude [print])
  (:require [babashka.fs    :as bb.fs]
            [clojure.edn    :as edn]
            [clojure.set    :as set]
            [clojure.string :as string]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;; Helpers


(defn- -target+

  ;; Retrieves available targets in the given root directory.

  [root extension]

  (let [n-extension (count extension)]
    (keep (fn [^Path path]
            (let [filename (str (.getFileName path))]
              (when (string/ends-with? filename
                                       extension)
                (-> (.substring filename
                                0
                                (- (count filename)
                                   n-extension))
                    (symbol)))))
          (bb.fs/list-dir root))))



(defn- -task+

  ;; Retrieves all available Babashka tasks.

  [option+]

  (-> (or (:bb option+)
          "bb.edn")
      (slurp)
      (edn/read-string)
      (:tasks)))


;;;;;;;;;; Print documentation


(defn- -print

  ;; Core implementation.

  [root option+ print-body]

  (let [extension (or (:extension option+)
                      ".txt")]
    (if-some [target (or (:target option+)
                         (some-> (first *command-line-args*)
                                 (symbol)))]
      (print-body (let [path-body (str root
                                       "/"
                                       target
                                       extension)]
                    (when (bb.fs/exists? path-body)
                      (slurp path-body)))
                  (assoc option+
                         :extension extension
                         :target    target))
      (do
        (println "Documentation available for:")
        (println)
        (doseq [path (sort-by (fn [^String target]
                                (.toLowerCase target))
                              (-target+ root
                                        extension))]
          (println (str "  "
                        path)))))))


;;;


(defn print

  "Prints a documentation file from the `root` directory.

   Options may be:

   | Key          | Value                                          | Default      |
   |--------------|------------------------------------------------|--------------|
   | `:extension` | Extension of text files in the root directory  | `\".txt\"`   |
   | `:target`    | File to print (without extension)              | CLI arg      |
  
   Without any target, prints all possible targets from the root.

   Useful as a Babashka task, a quick way for providing help.
   See [[print-task]]."
  

  ([root]

   (print root
          nil))


  ([root option+]

   (-print root
           option+
           (fn print-body [body _option+]
             (println (or body
                          "No documentation found for this target."))))))







(defn print-task

  "Like [[print]] but targets are Babashk tasks.

   Does some additional nice printing.

   Options may additionally contain:

   | Key          | Value                                          | Default      |
   |--------------|------------------------------------------------|--------------|
   | `:bb`        | Path to the Babashka config file hosting tasks | `\"bb.edn\"` |"


  ([root]

   (print-task root
               nil))


  ([root option+]

   (-print root
           option+
           (fn print-body [body option+]
             (if-some [task-data (get (-task+ option+)
                                      (option+ :target))]
               ;;
               ;; User task found, print doc.
               (do
                 (println)
                 (when-some [docstring (:doc task-data)]
                   (println docstring)
                   (println)
                   (println "---")
                   (println))
                 (println (or body
                              "No documentation found for this task.")))
               ;;
               ;; Input task does not seem to exist.
               (println "Task not found."))))))



(defn report-task-documentation

  "Prints:

   - List of tasks that are undocumented
   - List of documentation not corresponding to an available task

   The latter happens when a task is renamed.

   Takes the same `option+` as [[print-task]]."


  ([root]

   (report-task-documentation root
                              nil))


  ([root option+]

   (let [available  (set (keys (-task+ option+)))
         documented (set (-target+ root
                                   (or (:extension option+)
                                       ".txt")))
         diff       (fn [header a b]
                      (when-some [task+ (-> (set/difference a
                                                            b)
                                            (not-empty)
                                            (some->
                                              (sort)
                                              (vec)))]
                        (println header)
                        (println)
                        (doseq [task task+]
                          (println (str "  "
                                        task)))
                        (println)
                        true))]
     (when-not (some identity
                     [(diff "Missing documentation for tasks:"
                            available
                            documented)
                      (diff "Documentation for inexistant tasks:"
                            documented
                            available)])
       (println "All good!")))))
