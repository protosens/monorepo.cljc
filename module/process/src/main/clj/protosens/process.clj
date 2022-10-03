(ns protosens.process

  "Spawning processes from Babahska or Clojure JVM.

   This is a light wrapper over [`babashka.process`](https://github.com/babashka/process).
   The main purpose is to maintain a collection utilities commonly needed by the rest
   of the repository.
  
   See [[run]]."

  (:refer-clojure :exclude [await])
  (:require [babashka.process :as bb.process]
            [clojure.string   :as string]))


;;;;;;;;;; Launching processes


(defn shell

  "Exactly like [[run]] but STDIO is set to `:inherit`."


  ([command]

   (shell command
          nil))


  ([command option+]

   (bb.process/process command
                       (merge {:err      :inherit
                               :in       :inherit
                               :out      :inherit
                               :shutdown bb.process/destroy-tree}
                              option+))))



(defn run

  "Runs the given `command` and returns a process.

   Supported options are:

   | Key         | Value                       | Default                |
   |-------------|-----------------------------|------------------------|
   | `:dir`      | Working directory           | Current directory      |
   | `:env`      | Environment variables map   | `nil`                  |
   | `:err`      | STDERR                      | A Java `OutputStream`  |
   | `:in`       | STDIN                       | A Java `InputStream`   |
   | `:out`      | STDOUT                      | A Java `OutputStream`  |
   | `:shutdown` | Shutdown hook               | [[destroy]]            |

   STDIO arguments must be compatible with `clojure.java.io/copy` or be set to `:inherit`
   (meaning they will be inherited from the current process).

   The shutdown function is executed on clean-up.
  
   **Note:** this uses `babasha.process/process` but there is no guarantee other options will
   be supported in the future."


  ([command]
   
   (run command
        nil))


  ([command option+]

   (bb.process/process command
                       (merge {:shutdown bb.process/destroy-tree}
                              option+))))


;;;;;;;;;; Feedback on processes


(defn- -slurp

  ;; Reads an output completely, trimming pending whitespace (e.g. newline).

  [process k]

  (-> (get process
           k)
      (slurp)
      (string/trimr)
      (not-empty)))


;;;


(defn await

  "Awaits the termination of the given `process`.
  
   Returns the process with an `:exit` code."

  [process]

  (deref process))



(defn destroy

  "Detroys the given `process` and all its descendant."

  [process]

  (bb.process/destroy-tree process))



(defn err

  "Like [[out]] but for STDERR."

  [process]

  (-slurp process
          :err))



(defn exit-code

  "Returns the exit code of the given `process`.

   Blocks until termination if needed."

  [process]

  (:exit (await process)))



(defn out

  "Captures and returns STDOUT as a string.
  
   Trims whitespace at the end (typically, a new line)."

  [process]

  (-slurp process
          :out))



(defn success?

  "Returns `true` is the `process` terminated with a zero status code.
  
   Blocks until temrination if needed."

  [process]

  (zero? (exit-code process)))
