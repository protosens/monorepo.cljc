(ns protosens.deps.edn

  "Handling `deps.edn` files.
  
   Most useful for tool authors."

  (:refer-clojure :exclude [read])
  (:require [clojure.string      :as string]
            [protosens.edn.read  :as $.edn.read]
            [protosens.namespace :as $.namespace]
            [protosens.process   :as $.process]))


(declare path+)


(set! *warn-on-reflection*
      true)


;;;;;;;;;; 


(defn namespace+

  "Returns namespaces found in the [[path+]] of that `deps-edn` file."


  ([deps-edn]

   (namespace+ deps-edn
               nil))


  ([deps-edn alias+]

   ($.namespace/search (path+ deps-edn
                              alias+))))



(defn path+

  "Returns all `:paths`, prepending `:deps/root`.
  
   A collection of aliases may be provided for including `:extra-paths`."

  ([deps-edn]

   (path+ deps-edn
          nil))


  ([deps-edn alias+]

   (map (let [root (deps-edn :deps/root)]
          (fn [path]
            (str root
                 "/"
                 path)))
        (concat (deps-edn :paths)
                (mapcat (comp :extra-paths
                              (deps-edn :aliases))
                        alias+)))))


;;;;;;;;;; IO and processes


(defn read 

  "Reads the `deps.edn` file located in `dir` (defaults to `./`).
  
   Remembers the `dir`ectory under `:deps/root`.
  
   Typically, an entry point for using other functions from this namespace."


  ([]

   (read nil))


  ([dir]

   (-> (str (or dir
                ".")
            "/deps.edn")
       ($.edn.read/file)
       (assoc :deps/root
              dir))))



(defn require-project

  "In a new process, requires all namespaces found with [[namespace+]].
  
   This is useful for ensuring that a project fully compiles for production without any
   tests dependencies and such.

   Namespaces are required one by one using Clojure CLI.

   Returns `true` if the process completed with a zero status, meaning everything has been
   required without any problem."


  ([deps-edn]

   (require-project deps-edn
                    nil))


  ([deps-edn option+]
  
   (let [alias+ (:alias+ option+)]
     (-> ($.process/shell (concat ["clojure"
                                   (cond->
                                     "-M"
                                     (seq alias+)
                                     (str (string/join alias+)))]
                                  (mapcat (fn [nmspace]
                                            ["-e" (format "(println \"(require '%s)\")"
                                                         nmspace) 
                                             "-e" (format "(require '%s)"
                                                          nmspace)])
                                          (sort (namespace+ deps-edn
                                                            alias+))))
                          (-> (:protosens.process/option+ option+)
                              (assoc :dir
                                     (deps-edn :deps/root))))
         ($.process/success?)))))



(defn require-project-bb

  "Exactly like [[require-project]] but uses Babashka instead of Clojure CLI."


  ([deps-edn]

   (require-project-bb deps-edn
                       nil))


  ([deps-edn option+]

   (let [alias+ (:alias+ option+)]
     (-> ($.process/shell (concat ["bb"
                                   "-e"
                                   ;; Note: `./` local root does not work.
                                   (format "(babashka.deps/add-deps '%s
                                                                    {:aliases %s})"
                                           deps-edn
                                           (if (seq alias+)
                                             (vec alias+)
                                             "nil"))]
                                  (mapcat (fn [nmspace]
                                            ["-e" (format "(println \"(require '%s)\")"
                                                         nmspace) 
                                             "-e" (format "(require '%s)"
                                                          nmspace)])
                                          (sort (namespace+ deps-edn
                                                            alias+))))
                          (-> (:protosens.process/option+ option+)
                              (assoc :dir
                                     (deps-edn :deps/root))))
         ($.process/success?)))))
