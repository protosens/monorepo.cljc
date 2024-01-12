(ns protosens.path

  (:import (java.nio.file Path
                          Paths)))


(set! *warn-on-reflection*
      true)


(declare normalized)


;;;;;;;;;;


(defn absolute

  ^Path

  [^Path path]

  (.toAbsolutePath path))



(defn canonical

  ^Path

  [path]

  (-> path
      (absolute)
      (normalized)))



(defn canonical+

  [path+]

  (into #{}
        (map canonical)
        path+))



(defn from-string

  ^Path

  [^String string]

  (Paths/get string
             (make-array String
                         0)))



(defn from-string+

  ^Path

  [string+]

  (Paths/get (first string+)
             (into-array String
                         (rest string+))))



(defn is?

  [x]

  (instance? Path
             x))



(defn normalized

  ^Path

  [^Path path]

  (.normalize path))



(defn normalized+

  [path+]

  (into #{}
        (map normalized)
        path+))



(defn starts-with?

  [^Path path-a ^Path path-b]

  (.startsWith path-a
               path-b))


;;;;;;;;;;


(defprotocol ICoercible

  (coerce ^Path [this]))



(extend-protocol ICoercible


  Path

    (coerce [path]
      path)


  String
  
    (coerce [string]
      (from-string string)))
