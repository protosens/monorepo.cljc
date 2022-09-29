(ns protosens.maestro.doc

  "Collection of miscellaneous helpers related to documentation."

  (:import (java.nio.file Path))
  (:require [babashka.fs    :as bb.fs]
            [clojure.edn    :as edn]
            [clojure.string :as string]
            [protosens.txt  :as $.txt]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;; Helpers


(defn- -target

  ;; Retrieves target to work with.

  [option+]

  (or (:target option+)
      (some-> (first *command-line-args*)
              (symbol))))



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


(defn print-help

  "Prints a documentation file from the `root` directory.

   Options may be:

   | Key          | Value                                          | Default       |
   |--------------|------------------------------------------------|---------------|
   | `:extension` | Extension of text files in the root directory  | `\".txt\"`    |
   | `:target`    | File to print (without extension)              | First CLI arg |
  
   Without any target, prints all possible targets from the root.

   Useful as a Babashka task, a quick way for providing help.
   Also see [[print-task]]."
  

  ([root]

   (print-help root
               nil))


  ([root option+]

   (let [extension (or (:extension option+)
                      ".txt")]
     (if-some [target (-target option+)]
       (let [path-body (str root
                            "/"
                            target
                            extension)
             body      (when (bb.fs/exists? path-body)
                         (slurp path-body))]
         (if body
           (println (or body
                        "No documentation found for this target."))
           (do
             (println "Documentation available for:")
             (println)
             (doseq [path (sort-by (fn [^String target]
                                     (.toLowerCase target))
                                   (-target+ root
                                             extension))]
               (println (str "  "
                             path))))))
       (println "No target specified.")))))



(defn print-task

  "Pretty-prints extra documentation for a task (if there is any).

   Options may contain:

   | Key          | Value                                          | Default       |
   |--------------|------------------------------------------------|---------------|
   | `:bb`        | Path to the Babashka config file hosting tasks | `\"bb.edn\"`  |
   | `:target`    | Task to print (without extension)              | First CLI arg |"


  ([]

   (print-task nil))


  ([option+]

   (let [task+ (-task+ option+)]
     (if-some [target (-target option+)]
       ;;
       ;; Target task provided.
       (if-some [task (get task+
                           target)]
         ;;
         ;; Okay, print task documentation.
         (do
           (when-some [docstring (task :doc)]
             (println docstring)
             (println)
             (println "---")
             (println))
           (println (or (some-> (task :maestro/doc)
                                ($.txt/realign))
                        "No extra documentation for this task.")))
         ;;
         ;; Task does not exist.
         (println "Task not found."))
       ;;
       ;; No target task.
       (if-some [documented (not-empty (keep (fn [[task data]]
                                               (when (:maestro/doc data)
                                                 task))
                                             task+))]
         ;;
         ;; Print documented tasks.
         (do
           (println "These tasks have extra documentation:")
           (println)
           (doseq [task (sort-by string/lower-case
                                 documented)]
             (println (str "  "
                           task))))
         ;;
         ;; There isn't any documentation.
         (println "No extra documentation found for any task."))))))



(defn undocumented-task+

  "Returns a sorted list of tasks which do not have a `:maestro/doc`.

   Options may be:

   | Key          | Value                                          | Default      |
   |--------------|------------------------------------------------|--------------|
   | `:bb`        | Path to the Babashka config file hosting tasks | `\"bb.edn\"` |"

  [option+]

  (sort-by string/lower-case
           (keep (fn [[task data]]
                   (when-not (:maestro/doc data)
                     task))
                 (-task+ option+))))



(defn report-undocumented-task+

  "Pretty-prints the result of [[undocumented-task+]]."


  ([]

   (report-undocumented-task+ nil))


  ([option+]

   (if-some [undocumented (not-empty (undocumented-task+ option+))]
     (do
       (println "These tasks do not have extra documentation:")
       (println)
       (doseq [task undocumented]
         (println (str "  "
                       task))))
     (println "All tasks have extra documentation."))))
