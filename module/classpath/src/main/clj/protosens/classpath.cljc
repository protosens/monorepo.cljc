(ns protosens.classpath

  "Simple classpath utilities that happens to be handy once in a while."

  (:require #?(:bb  [babashka.classpath :as bb.classpath])
            #?(:bb  [babashka.deps      :as bb.deps])
                    [clojure.string     :as string]
            #?(:clj [protosens.process  :as $.process])))



(declare split)


;;;;;;;;;;


(defn compute

  "Computes the classpath of `deps.edn`."

  []

  #?(:bb
     (with-out-str
       (bb.deps/clojure ["-Spath"]))
     ,
     :clj
     (-> ($.process/run ["clojure"
                         "-Spath"])
         (:out)
         (slurp))))



(defn current

  "Returns the current classpath."

  []

  #?(:bb  (bb.classpath/get-classpath)
     :clj (System/getProperty "java.class.path")))



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
