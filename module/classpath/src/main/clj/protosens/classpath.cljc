(ns protosens.classpath

  "Simple classpath utilities that happens to be handy once in a while."

  (:require #?(:bb [babashka.classpath :as bb.classpath])
            [clojure.string    :as string]
            [protosens.process :as $.process]))


(declare split)


;;;;;;;;;;


(defn current

  "Returns the current classpath."

  []

  #?(:bb  (bb.classpath/get-classpath)
     :clj (System/getProperty "java.class.path")))



(defn pprint

  "Pretty-prints the given classpath.
  
   Reads input from STDIN by default.
  
   Great match for [[compute]]. Classpath is [[split]] and sorted paths are printed
   line by line."


  #?(:clj ([]

   (pprint (slurp *in*))))


  ([classpath]

   (run! println
         (sort (split classpath)))))



(defn separator

  "Returns the platform-dependent separator used in the classpath."

  []

  #?(:clj (System/getProperty "path.separator")))



(defn split

  "Splits the given `classpath` into a vector of paths."

  [classpath]

  (map string/trim-newline
       (string/split classpath
                     (re-pattern (separator)))))
