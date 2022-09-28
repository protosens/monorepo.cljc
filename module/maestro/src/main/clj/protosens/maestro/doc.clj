(ns protosens.maestro.doc

  "Collection of miscellaneous helpers related to documentation."

  (:refer-clojure :exclude [print])
  (:require [babashka.fs :as bb.fs]
            [clojure.edn :as edn]))


;;;;;;;;;;


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
                         :target
                         target))
      (let [n-extension (count extension)]
        (println "Documentation available for:")
        (println)
        (doseq [path (sort-by str
                              (bb.fs/list-dir root))]
          (println (str "  "
                        (let [file (str (.getFileName path))]
                          (.substring file
                                      0
                                      (- (count file)
                                         n-extension))))))))))


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
             (if-some [task-data (-> (slurp (or (option+ :bb)
                                                "bb.edn"))
                                     (edn/read-string)
                                     (get-in [:tasks
                                              (option+ :target)]))]
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
