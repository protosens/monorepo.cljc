(ns protosens.namespace

  "Finding and requiring namespaces automatically."

  (:require [babashka.fs         :as bb.fs]
            [clojure.java.io     :as java.io]
            [clojure.string      :as string]
            [protosens.classpath :as $.classpath]))


(declare in-path+)


;;;;;;;;;; Miscellaneous


(defn from-filename


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



(defn main-ns

  [sym require+]

  (list 'ns
        sym
        (cons :require
              (filter (partial not=
                               sym)
                      require+))))


;;;;;;;;;; Searching for namespaces


(defn in-cp-dir+


  ([]

   (in-cp-dir+ nil))


  ([option+]

   (in-path+ (filter bb.fs/directory?
                     ($.classpath/split ($.classpath/current)))
             option+)))



(defn in-path


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

  "Requires all namespace filtered out by `f`.

   `f` takes a namespace as a simple and must:

   - Return `nil` if the namespace should not be required
   - Return an argument for `require` otherwise

   Namespaces are required one by one and prints what is happening."


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
