(ns protosens.maestro.task

  "Tasks often useful in the context of a monorepo.
  
   Great match for the [Babashka task runner](https://book.babashka.org/#tasks)."
  
  (:require [clojure.string             :as string]
            [protosens.maestro.required :as $.maestro.required]))


;;;;;;;;;;


(defn alias+
  
  "Common way for retrieving and printing all aliases needed for given ones.

   If this function was called as a Babashka task, one could:

   ```shel
   bb alias+ :some-alias
   # Or
   bb alias+ '[:some-alias :another-alias some-profile]'
   ```

   Which is then easily combined with Clojure CLI:

   ```shell
   clj -M$( bb alias+ :some-alias )
   ```

   User can follow the implementation for custom tasks that could, for instance,
   inject some default aliases and profiles. The idea is always the same:
  
   - Read `deps.edn`
   - Process CLI args if relevant (contains given root aliases and/or profiles)
   - Inject aliases and profiles if relevant
   - Search for all required aliases
   - Print those required aliases"


  ([]

   (-> ($.maestro.required/create-basis)
       ($.maestro.required/cli-arg)
       (alias+)))


  ([basis]

   (-> basis
       $.maestro.required/search
       $.maestro.required/print)))


(defn pprint-cp

  "Pretty-prints a classpath in alphabetical order given as argument or retrieved from STDIN.

   Great match for the Clojure CLI when used as a Babasha task, such as:

   ```
   clj -A:some-aliases -Spath | bb pprint-cp
   ```"


  ([]

   (pprint-cp (slurp *in*)))


  ([raw-cp]

   (run! println
         (-> (map string/trim-newline
                  (string/split raw-cp
                                (re-pattern ":")))
             sort))))
