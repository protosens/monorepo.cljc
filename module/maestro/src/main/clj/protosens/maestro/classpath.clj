(ns protosens.maestro.classpath

  "Simple classpath utilities."

  (:require [clojure.string          :as string]
            [protosens.maestro.alias :as $.maestro.alias]
            [protosens.maestro.util  :as $.maestro.util]))


;;;;;;;;;;


(defn compute

  "Computes the classpath using the given aliases on `clojure`.
  
   Only works in Babashka."
  
  [alias+]

  (-> (@$.maestro.util/d*shell {:out :string}
                               (format "clojure -Spath -A%s"
                                       ($.maestro.alias/stringify+ alias+)))
      (deref)
      (:out)))



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
