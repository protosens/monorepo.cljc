(ns protosens.classpath

  "Simple classpath utilities."

  (:require [clojure.string    :as string]
            [protosens.process :as $.process]))


(declare split)


;;;;;;;;;;


(defn compute

  "Computes the classpath for the given aliases.

   By running `clojure -Spath ...` in the shell.
  
   Returns `nil` if something goes wrong."
  
  [alias+]

  (-> ($.process/run ["clojure"
                      "-Spath"
                      (when (seq alias+)
                        (str "-A"
                              (string/join alias+)))])
      ($.process/out)))



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
