(ns protosens.maestro.plugin.build

  "Building jars and uberjars."

  (:import (java.nio.file Files)
           (java.nio.file.attribute FileAttribute))
  (:require [protosens.maestro         :as $.maestro]
            [protosens.maestro.alias   :as $.maestro.alias]
            [protosens.maestro.profile :as $.maestro.profile]))


;;;;;;;;;; Accessing `tools.build` (brought by users)


(defn- -resolve

  ;;

  [sym]

  (try
    (requiring-resolve sym)
    (catch Exception _ex
      (throw (Exception. "`tools.build` must be added to dependencies")))))


;;;


(def ^:private -d*compile-clj
     (delay
       (-resolve 'clojure.tools.build.api/compile-clj)))



(def ^:private -d*copy-dir
     (delay
       (-resolve 'clojure.tools.build.api/copy-dir)))



(def ^:private -d*create-basis
     (delay
       (-resolve 'clojure.tools.build.api/create-basis)))



(def ^:private -d*delete
     (delay
       (-resolve 'clojure.tools.build.api/delete)))



(def ^:private -d*jar
     (delay
       (-resolve 'clojure.tools.build.api/jar)))



(def ^:private -d*uber
     (delay
       (-resolve 'clojure.tools.build.api/uber)))



(def ^:private -d*write-pom
     (delay
       (-resolve 'clojure.tools.build.api/write-pom)))


;;;;;;;;;; Tasks


(defn clean

  [ctx]

  (let [path (ctx :maestro.plugin.build.path/output)]
    (println "Removing any previous output:"
             path)
    (@-d*delete {:path path}))
  ctx)



(defn copy-src

  [ctx]

  (println "Copying source paths")
  (@-d*copy-dir {:src-dirs   (ctx :maestro.plugin.build.path/src+)
                 :target-dir (ctx :maestro.plugin.build.path/class)})
  ctx)


;;;;;;;;;;


(defn- -jar

  [arg+]

  (let [required-alias+ (arg+ :maestro/require)
        dir-tmp         (str (Files/createTempDirectory "maestro-build-"
                                                        (make-array FileAttribute
                                                                    0)))
        path-class      (str dir-tmp
                             "/classes")
        path-src+       ($.maestro.alias/extra-path+ arg+
                                                     required-alias+)]
    (println "Using temporary directory for building:"
             dir-tmp)
    (-> (merge {:maestro.plugin.build/basis       (@-d*create-basis {:aliases required-alias+
                                                                     :project "deps.edn"})
                :maestro.plugin.build.path/class  path-class
                :maestro.plugin.build.path/src+   path-src+
                :maestro.plugin.build.path/target dir-tmp}
               arg+)
        (clean)
        (copy-src))))



(defn jar

  [arg+]

  (let [{:as        ctx
         path-class :maestro.plugin.build.path/class
         path-jar   :maestro.plugin.build.path/output}
        (-jar arg+)
        ;;
        [artifact
         version-map]
        (-> ctx
            (get-in [:aliases
                     (ctx :maestro.plugin.build.alias/artifact)
                     :extra-deps])
            (first))]
    (println "Preparing POM file")
    (@-d*write-pom {:basis     (ctx :maestro.plugin.build/basis)
                    :class-dir path-class
                    :lib       artifact
                    :src-dirs  (ctx :maestro.plugin.build.path/src+)
                    :src-pom   (ctx :maestro.plugin.build.path/pom)
                    :version   (version-map :mvn/version)})
    (println "Assemling jar to:"
             path-jar)
    (@-d*jar {:class-dir path-class
              :jar-file  path-jar})
    ctx))



(defn uberjar

  [arg+]

  (let [{:as          ctx
         basis        :maestro.plugin.build/basis
         path-class   :maestro.plugin.build.path/class
         path-uberjar :maestro.plugin.build.path/output} (-jar arg+)]
    (println "Compiling" (ctx :maestro.plugin.build/alias))
    (@-d*compile-clj {:basis        basis
                      :class-dir    path-class
                      :compile-opts (ctx :maestro.uberjar/compiler)
                      :src-dirs     (ctx :maestro.plugin.build.path/src+)})
    (println "Assembling uberjar to: %s"
             path-uberjar)
    (@-d*uber {:basis     basis
               :class-dir path-class
               :exclude   (ctx :maestro.plugin.build.path/exclude)
               :main      (ctx :maestro.plugin.build.uberjar/main)
               :uber-file path-uberjar})
    ctx))


;;;;;;;;;; 


(defmulti by-type
  :maestro.plugin.build/type)



(defmethod by-type

  :jar

  [arg+]

  (jar arg+))



(defmethod by-type

  :uberjar

  [arg+]

  (uberjar arg+))


;;;


(defn build

  [arg+]

  (let [root-alias    (arg+ :maestro.plugin.build/alias)
        basis-maestro ($.maestro/search (-> {:maestro/alias+   [root-alias]
                                             :maestro/profile+ ['release]}
                                            ($.maestro.profile/prepend+ (arg+ :maestro/profile+))))]
    (-> (merge basis-maestro
               (get-in basis-maestro
                       [:aliases
                        root-alias])
               (dissoc arg+
                       :maestro/alias+
                       :maestro/profile+))
        (assoc :maestro/require
               (basis-maestro :maestro/require))
        (by-type))))
