(ns protosens.deps.edn

  "Handling `deps.edn` files.
  
   Most useful for tool authors."

  (:refer-clojure :exclude [read])
  (:require [protosens.edn.read  :as $.edn.read]
            [protosens.namespace :as $.namespace]))


(declare path+)


(set! *warn-on-reflection*
      true)


;;;;;;;;;; IO


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


;;;;;;;;;; Extracting information from `deps.edn` files


(defn namespace+

  "Returns namespaces provided by source files in that `deps.edn`.
  
   Options may be:

   | Key          | Value                  | Default                          |
   |--------------|------------------------|----------------------------------|
   | `:alias+`    | See [[path+]]          | `nil`                            |
   | `:extension+ | Source file extensions | `[\".clj\" \".cljc\" \".cljs\"]` |"


  ([deps-edn]

   (namespace+ deps-edn
               nil))


  ([deps-edn option+]

   (-> deps-edn
       (path+ (:alias+ option+))
       ($.namespace/in-path+))))



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
