(ns protosens.namespace

  (:refer-clojure :exclude [find])
  (:require [clojure.java.classpath       :as classpath]
            [clojure.java.io              :as java.io]
            [clojure.tools.namespace.find :as namespace.find]))


;;;;;;;;;;


(defn find+


  ([]

   (namespace.find/find-namespaces (classpath/classpath)))


  ([path+]

   (namespace.find/find-namespaces (map (fn [path]
                                          (cond->
                                            path
                                            (string? path)
                                            (java.io/file)))
                                        path+))))



(defn require-found

  [f]

  (let [ns+ (sort-by (fn [x]
                       (cond->
                         x
                         (vector? x)
                         (first)))
                     (keep f
                           (find+)))]
    (run! (fn [x]
            (prn (list 'require
                       x))
            (require x))
          ns+)
    ns+))
