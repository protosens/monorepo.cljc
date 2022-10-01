(ns protosens.maestro.classpath

  "Simple classpath utilities."

  (:require [babashka.process        :as bb.process]
            [clojure.string          :as string]
            [protosens.maestro.alias :as $.maestro.alias]))


;;;;;;;;;;


(defn compute

  "Computes the classpath using the given aliases on `clojure`."
  
  [alias+]

  (-> (bb.process/process ["clojure" "-Spath" (str "-A"
                                                   ($.maestro.alias/stringify+ alias+))])
      (:out)
      (slurp)))



(defn pprint

  "Pretty-prints the output from [[compute]] or `clojure -Spath ...` in alphabetical order given as argument
   or retrieved from STDIN."


  ([]

   (pprint (slurp *in*)))


  ([raw-cp]

   (run! println
         (-> (map string/trim-newline
                  (string/split raw-cp
                                (re-pattern (System/getProperty "path.separator"))))
             sort))))
