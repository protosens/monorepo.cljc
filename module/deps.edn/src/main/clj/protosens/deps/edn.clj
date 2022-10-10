(ns protosens.deps.edn

  "Specialized functions for handling `deps.edn` files.
  
   Most useful for tool authors. [[read]] fetches a `deps.edn` file and other functions
   are used for extracting informations."

  (:refer-clojure :exclude [read])
  (:require [protosens.edn.read  :as $.edn.read]
            [protosens.namespace :as $.namespace]))


(declare path+)


(set! *warn-on-reflection*
      true)


;;;;;;;;;; IO


(defn read 

  "Reads the `deps.edn` file located in `dir`.
  
   Defaults to `./`.
  
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


;;;;;;;;;; Private


(defn- -prepend-root

  ;; Prepends `:deps/root` to paths.

  [deps-edn path+]

  (let [root (deps-edn :deps/root)]
    (cond->>
      path+
      root
      (map (fn [path]
             (str root
                  "/"
                  path))))))


;;;;;;;;;; Extracting information from `deps.edn` files


(defn extra-path+

  "Returns all `:extra-paths` from the given aliases.

   Prepends them prepended with `:deps/root`."

  [deps-edn alias+]

  (-prepend-root deps-edn
                 (mapcat (comp :extra-paths
                               (deps-edn :aliases))
                         alias+)))



(defn namespace+

  "Returns namespaces provided by source files in that `deps.edn`.
  
   Options may be:

   | Key           | Value                  | Default                          |
   |---------------|------------------------|----------------------------------|
   | `:alias+`     | See [[path+]]          | `nil`                            |
   | `:extension+` | Source file extensions | `[\".clj\" \".cljc\" \".cljs\"]` |"


  ([deps-edn]

   (namespace+ deps-edn
               nil))


  ([deps-edn option+]

   (-> deps-edn
       (path+ (:alias+ option+))
       ($.namespace/in-path+ option+))))



(defn path+

  "Returns all `:paths` and `:extra-paths` for the given aliases.

   Prepends them prepended with `:deps/root`."


  ([deps-edn]

   (path+ deps-edn
          nil))


  ([deps-edn alias+]

   (concat (-prepend-root deps-edn
                          (deps-edn :paths))
           (extra-path+ deps-edn
                        alias+))))
