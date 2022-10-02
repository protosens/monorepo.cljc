(ns protosens.classpath

  "Simple classpath utilities."

  (:require [babashka.process :as bb.process]
            [clojure.string   :as string]))


(declare split)


;;;;;;;;;;


(defn compute

  "Computes the classpath for the given aliases.

   By running `clojure -Spath ...` in the shell."
  
  [alias+]

  (-> (bb.process/process ["clojure"
                           "-Spath"
                           (str "-A"
                                (string/join alias+))])
      (:out)
      (slurp)))



(defn pprint

  "Pretty-prints the given classpath.
  
   Reads input from STDIN by default.
  
   Great match for [[compute]]. Classpath is [[split]] and sorted paths are printed."


  ([]

   (pprint (slurp *in*)))


  ([classpath]

   (run! println
         (sort (split classpath)))))



(defn separator

  "Returns the platform-dependent separator used in the classpath."

  []

  (System/getProperty "path.separator"))


(defn split

  "Splits the given `classpath` into a vector of paths."

  [classpath]

  (map string/trim-newline
       (string/split classpath
                     (re-pattern (separator)))))
