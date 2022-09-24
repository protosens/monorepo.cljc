(ns protosens.maestro.classpath

  "Simple classpath utilities."

  (:require [clojure.string          :as string]
            [protosens.maestro.alias :as $.maestro.alias]))


;;;;;;;;;;


(defn compute

  "Computes the classpath using the given aliases on `clojure`.
  
   Only works in Babashka."
  
  [aliases]

  (let [shell (requiring-resolve 'babashka.tasks/shell)]
    (-> (shell {:out :string}
               (format "clojure -A%s -Spath"
                       ($.maestro.alias/stringify+ aliases)))
        (deref)
        (:out))))



(defn pprint

  "Pretty-prints the output from [[compute]] or `clojure -Spath ...` in alphabetical order given as argument
   or retrieved from STDIN."


  ([]

   (pprint (slurp *in*)))


  ([raw-cp]

   (run! println
         (-> (map string/trim-newline
                  (string/split raw-cp
                                (re-pattern ":")))
             sort))))
