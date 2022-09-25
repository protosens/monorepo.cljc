(ns protosens.maestro.plugin.build

  "Building jars and uberjars."

  (:import (java.nio.file Files)
           (java.nio.file.attribute FileAttribute))
  (:require [clojure.edn               :as edn]
            [protosens.maestro         :as $.maestro]
            [protosens.maestro.alias   :as $.maestro.alias]
            [protosens.maestro.profile :as $.maestro.profile]
            [protosens.maestro.util    :as $.maestro.util]))


;;;;;;;;;; Failures


(defn- -fail

  ;;

  [message]

  (throw (Exception. message)))


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

  (when-not (arg+ :maestro.plugin.build.path/output)
    (-fail "Missing output path"))
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

  (when-not (arg+ :maestro.plugin.build.alias/artifact)
    (-fail "Missing artifact alias"))
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

  (let [main          (arg+ :maestro.plugin.build.uberjar/main)
        _             (when-not main
                        (-fail "Missing main method for uberjar"))
        {:as          ctx
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

  :default

  [_arg+]

  (-fail "Missing build type"))



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

  (let [alias-build   (arg+ :maestro.plugin.build/alias)
        _             (when-not alias-build
                        (-fail "Missing alias to build"))
        basis-maestro ($.maestro/search (-> {:maestro/alias+   [alias-build]
                                             :maestro/profile+ ['release]}
                                            ($.maestro.profile/prepend+ (arg+ :maestro/profile+))))]
    (-> (merge basis-maestro
               (get-in basis-maestro
                       [:aliases
                        alias-build])
               (dissoc arg+
                       :maestro/alias+
                       :maestro/profile+))
        (assoc :maestro/require
               (basis-maestro :maestro/require))
        (by-type))))



(defn task


  ([alias-maestro]

   (task alias-maestro
         nil))


  ([alias-maestro alias-build]

   (@$.maestro.util/d*clojure (str "-X"
                                   (with-out-str
                                     (protosens.maestro/task {:maestro/alias+ [alias-maestro]})))
                              'protosens.maestro.plugin.build/build
                              {:maestro.plugin.build/alias (or alias-build
                                                               (edn/read-string (first *command-line-args*)))})))
