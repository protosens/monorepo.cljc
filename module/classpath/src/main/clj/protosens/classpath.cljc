(ns protosens.classpath

  "Simple classpath utilities that happens to be handy once in a while."

  (:require         [clojure.string     :as C.string]
            #?(:bb  [babashka.classpath :as bb.classpath])
            #?(:bb  [babashka.deps      :as bb.deps])
            #?(:clj [protosens.process  :as $.process])))



(declare split)


;;;;;;;;;;


(defn compute

  "Computes the classpath of `deps.edn`."


  ([]

   (compute nil))


  ([alias+]

   (let [arg+ (cons "-Spath"
                    (when (seq alias+)
                      (str "-A"
                           (C.string/join ""
                                          alias+))))]
     #?(:bb
        (with-out-str
          (bb.deps/clojure arg+))
        ,
        :clj
        (-> ($.process/run (cons "clojure"
                                 arg+))
            (:out)
            (slurp))))))



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

  (map C.string/trim-newline
       (C.string/split classpath
                       (re-pattern (separator)))))
