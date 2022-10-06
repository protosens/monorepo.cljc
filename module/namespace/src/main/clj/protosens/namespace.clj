(ns protosens.namespace

  "Finding and requiring namespaces automatically."

  (:require [babashka.fs         :as bb.fs]
            [clojure.java.io     :as java.io]
            [clojure.string      :as string]
            [protosens.classpath :as $.classpath]))


(declare in-path+)


;;;;;;;;;; Miscellaneous


(defn from-filename

  "Converts a `filename` to a namespace symbol.

   Opposite of [[to-filename]].

   If a `root` directory is provided, `filename` is relativized first before
   being converted.

   The extension of `filename` is remembered in the `meta`data of the produced
   symbol under `:protosens.namespace/extension`."


  ([filename]

   (let [[filename-2
          extension] (string/split filename
                                   #"\."
                                   2)]
   (-> filename-2
       (string/replace "/"
                       ".")
       (string/replace "_"
                       "-")
       (symbol)
       (with-meta {:protosens.namespace/extension (str \.
                                                       extension)}))))


  ([root filename]

   (-> (bb.fs/relativize root
                         filename)
       (str)
       (from-filename))))



(defn to-filename

  "Converts a (namespace) symbol to a filename

   Opposite of [[from-filename]].

   A root directory may be provided."


  ([ns-sym extension]

   (str (-> ns-sym
            (str)
            (string/replace "."
                            "/")
            (string/replace "-"
                            "_"))
        extension))


  ([root ns-sym extension]

   (str root
        "/"
        (to-filename ns-sym
                     extension))))


;;;;;;;;;; Searching for namespaces


(defn in-cp-dir+

  "Uses [[in-path+]] on directories from the current classpath.
  
   Useful for detecting available namespaces.
   Does not crawl JAR files."


  ([]

   (in-cp-dir+ nil))


  ([option+]

   (in-path+ (filter bb.fs/directory?
                     ($.classpath/split ($.classpath/current)))
             option+)))



(defn in-path

  "Finds all namespaces available in the given directory `path`.

   Options may be:

   | Key           | Value                          | Default                          |
   |---------------|--------------------------------|----------------------------------|
   | `:extension+` | Extensions for files to handle | `[\".clj\" \".cljc\" \".cljs\"]` |"


  ([path]

   (in-path path
            nil))


  ([path option+]

   (let [extension+ (or (:extension+ option+)
                        [".clj"
                         ".cljc"
                         ".cljs"])]
     (into []
           (comp (filter (fn [child]
                           (and (bb.fs/regular-file? child)
                                (some (partial string/ends-with?
                                               (str child))
                                      extension+))))
                 (map (partial from-filename
                               path)))
           (file-seq (java.io/file path))))))




(defn in-path+

  "Exactly like [[in-path]] but works with a collection of directories."

  ([path+]

   (in-path+ path+
             nil))


  ([path+ option+]

   (apply concat
          (map (fn [path]
                 (in-path path
                          option+))
               path+))))


;;;;;;;;;; Requiring namespaces


(defn require-cp-dir+

  "Requires all namespaces found with [[in-cp-dir+]].

   They are filtered by `f`, a function which takes a namespace and must:
  
   - Return `nil` if the namespace should not be required
   - Return an argument for `require` otherwise

   Namespaces are required one by one and prints what is happening.
  
   Useful to put in the `user` namespace for automatically requiring a set of
   namespaces."


  ([f]

   (require-cp-dir+ f
                    nil))


  ([f option+]

   (let [ns+ (sort-by (fn [x]
                        (cond->
                          x
                          (vector? x)
                          (first)))
                      (keep f
                            (in-cp-dir+ option+)))]
     (run! (fn [x]
             (prn (list 'require
                        x))
             (require x))
           ns+)
     ns+)))



(defn main-ns

  "Pretty-prints to `*out*` a CLJC namespace requiring all namespaces provided by `deps.edn`.

   Namespace is named after `ns-sym`.
   Pure CLJ or pure CLJS required namespaces are guarded by reader conditionals.

   Aliases to activate may be provided in `option+` under `:alias+`.

   Also see [[namespace+]]."

  [ns-sym ns-require+]

  (println ";; This file has been autogenerated.")
  (println ";; Its only purpose is to require a collection of namespaces.")
  (println ";;")
  (println ";;")
  (print "(ns ")
  (binding [*print-meta* true]
    (prn (with-meta ns-sym
                    {:no-doc true})))
  (println "  (:require")
  (doseq [sym (sort ns-require+)]
    (print "    ")
    (println (case (:protosens.namespace/extension (meta sym))
               ".clj"  (format "#?(:clj %s)"
                               sym)
               ".cljc" sym
               ".cljs" (format "#?(:cljs %s)"
                               sym))))
  (println "))")
  (println)
  (println)
  (println ";;;;;;;;;;")
  (println)
  (println)
  (println "(defn -main [& _arg+])"))
