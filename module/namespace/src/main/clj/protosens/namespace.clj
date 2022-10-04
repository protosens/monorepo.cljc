(ns protosens.namespace

  "Finding and requiring namespaces automatically."

  (:require [clojure.java.classpath       :as classpath]
            [clojure.java.io              :as java.io]
            [clojure.tools.namespace.find :as namespace.find]))


;;;;;;;;;; Searching for namespaces


(defn search

  "Searches for all namespaces available in the given paths.

   By default, search in the current classpath."


  ([]

   (search nil))


  ([path+]

   (namespace.find/find-namespaces (or (seq (map (fn [path]
                                                   (cond->
                                                     path
                                                     (string? path)
                                                     (java.io/file)))
                                                 path+))
                                        (classpath/classpath)))))


;;;;;;;;;; Requiring namespaces


(defn require-found

  "Requires all namespace filtered out by `f`.

   `f` takes a namespace as a simple and must:

   - Return `nil` if the namespace should not be required
   - Return an argument for `require` otherwise

   Namespaces are required one by one and prints what is happening."

  [f]

  (let [ns+ (sort-by (fn [x]
                       (cond->
                         x
                         (vector? x)
                         (first)))
                     (keep f
                           (search)))]
    (run! (fn [x]
            (prn (list 'require
                       x))
            (require x))
          ns+)
    ns+))
