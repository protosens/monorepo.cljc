(ns protosens.maestro.plugin.build

  "Maestro plugin for `tools.build` focused on building jars and uberjars, key information being located
   right in aliases.

   Aims to provide enough flexibility so that it would cover a majority of use cases. Also extensible by
   implementing methods for [[by-type]].

   Main entry point is [[build]] and [[task]] offers a fast way of getting into it using Babashka.
  
   <!> `tools.build` is not imported and must be brought by the user."

  (:import (java.nio.file Files)
           (java.nio.file.attribute FileAttribute))
  (:require [clojure.edn               :as edn]
            [protosens.maestro         :as $.maestro]
            [protosens.maestro.alias   :as $.maestro.alias]
            [protosens.maestro.profile :as $.maestro.profile]
            [protosens.maestro.util    :as $.maestro.util]))


;;;;;;;;;; Failures


(defn- -fail

  ;; Notably used when a user argument is missing.

  [message]

  (throw (Exception. message)))


;;;;;;;;;; Accessing `tools.build` (brought by users)


(defn- -resolve

  ;; `tools.build` being brought by users, we use that little charade below with delays and
  ;; `requiring-resolve` so that the namespace can be analyzed by `cljdoc` even without access
  ;; to `tools.build` (which is, in addition, a Git dependency).

  [sym]

  (try
    (requiring-resolve sym)
    (catch Exception _ex
      (throw (Exception. "`tools.build` must be added to dependencies")))))


;;; Access to required `tools.build` functions


(def ^:private -d*compile-clj
     (delay
       (-resolve 'clojure.tools.build.api/compile-clj)))



(def ^:private -d*copy-dir
     (delay
       (-resolve 'clojure.tools.build.api/copy-dir)))



(def ^:private -d*copy-file
     (delay
       (-resolve 'clojure.tools.build.api/copy-file)))


(def ^:private -d*create-basis
     (delay
       (-resolve 'clojure.tools.build.api/create-basis)))



(def ^:private -d*delete
     (delay
       (-resolve 'clojure.tools.build.api/delete)))



(def ^:private -d*jar
     (delay
       (-resolve 'clojure.tools.build.api/jar)))



(def ^:private -d*pom-path
     (delay
       (-resolve 'clojure.tools.build.api/pom-path)))


(def ^:private -d*uber
     (delay
       (-resolve 'clojure.tools.build.api/uber)))



(def ^:private -d*write-pom
     (delay
       (-resolve 'clojure.tools.build.api/write-pom)))


;;;;;;;;;; Tasks


(defn clean

  "Deletes the file under `:maestro.plugin.build.path/output`."

  [ctx]

  (let [path (ctx :maestro.plugin.build.path/output)]
    (println "Removing any previous output:"
             path)
    (@-d*delete {:path path}))
  ctx)



(defn copy-src

  "Copies source from `:maestro.plugin.build.path/src+` to `:maestro.plugin.build.path/class`."

  [ctx]

  (println "Copying source paths")
  (@-d*copy-dir {:src-dirs   (ctx :maestro.plugin.build.path/src+)
                 :target-dir (ctx :maestro.plugin.build.path/class)})
  ctx)


;;;;;;;;;;


(defn- -jar

  ;; Prelude common to jarring and uberjarring.
  ;;
  ;; Prepares paths and a temporary directory for work.

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
                                                                     :project (or (arg+ :maestro/project)
                                                                                  "deps.edn")})
                :maestro.plugin.build.path/class  path-class
                :maestro.plugin.build.path/src+   path-src+
                :maestro.plugin.build.path/target dir-tmp}
               arg+)
        (clean)
        (copy-src))))



(defn jar

  "Implementation for the `:jar` type in [[by-type]].

   Alias data for the build alias must or may contain:

   | Key                                    | Value                         | Mandatory? | Default       |
   |----------------------------------------|-------------------------------|------------|---------------|
   | `:maestro/root`                        | Root directory of the alias   | Yes        | /             |
   | `:maestro.plugin.build.alias/artifact` | Artifact alias (see below)    | Yes        | /             |
   | `:maestro.plugin.build.path/output`    | Output path for the jar       | Yes        | /             |
   | `:maestro.plugin.build.path/pom`       | Path to the template POM file | No         | `\"pom.xml\"` |

   A POM file will be created if necessary but it is often best starting from one that hosts key information
   that does not change from build to build like SCM, organization, etc. It will be copied to `./pom.xml` under
   `:maestro/root`.

   The artifact alias is an alias representing your release in its `:extra-deps` and nothing else. This is
   where the artifact name and version are extracted from. For instance, in this repository, `deps.edn` contains
   this artifact alias related to `:module/maestro`:

   ```clojure
   {:release/maestro
    {:extra-deps {com.protosens/maestro {:mvn/version \"x.x.x\"}}
     ...}}
   ```

   This is useful so that other modules can require this one in 2 ways using profiles: one for local development,
   one for their own releases. For instance:

   ```clojure
   {:module/another-module
    {:maestro/require [{default :module/maestro
                        release :release/maestro}]
     ...}}
   ``` 

   To go even further, it is possible to run tests against a release installed locally or downloaded remotely.
   This ensure that everything was built correctly beyond any doubt. For instance, in this repository, Maestro
   is tested like this:

   ```
   clojure -M$( bb aliases:test :module/maestro )
   ```

   But the following one will run the Maestro test suite against the Maestro version from the local Maven cache
   after downloading it from Clojars if necessary:

   ```
   clojure -M$( bb aliases:test '[release :release/maestro]' )
   ```

   Note: it is best activating the `release` alias when doing that sort of things."

  [arg+]

  (let [alias-artifact
       (arg+ :maestro.plugin.build.alias/artifact)
       _  (or alias-artifact
              (-fail "Missing artifact alias"))
       ;;
       dir-root
       (arg+ :maestro/root)
       _ (or dir-root
             (-fail "Missing root directory"))
       ;;
        {:as        ctx
         path-class :maestro.plugin.build.path/class
         path-jar   :maestro.plugin.build.path/output}
        (-jar arg+)
        ;;
        [artifact
         version-map]
        (-> ctx
            (get-in [:aliases
                     alias-artifact
                     :extra-deps])
            (first))
        ;;
        pom-config
        {:basis     (ctx :maestro.plugin.build/basis)
         :class-dir path-class
         :lib       artifact
         :src-dirs  (ctx :maestro.plugin.build.path/src+)
         :src-pom   (or (ctx :maestro.plugin.build.path/pom)
                        "pom.xml")
         :version   (version-map :mvn/version)}]
    (println "Preparing POM file")
    (@-d*write-pom pom-config)
    (let [path-pom-module (str dir-root
                               "/pom.xml")]
      (println "Copying POM file to:"
               path-pom-module)
      (@-d*copy-file {:src    (@-d*pom-path pom-config)
                      :target path-pom-module}))
    (println "Assemling jar to:"
             path-jar)
    (@-d*jar {:class-dir path-class
              :jar-file  path-jar})
    ctx))



(defn uberjar

  "Implementation for the `:uberjar` type in [[by-type]].
  
   Alias data for the build alias must or contain:

   | Key                                      | Value                         | Mandatory? |
   |------------------------------------------|-------------------------------|------------|
   | `:maestro.plugin.build.path/exclude`     | Paths to exclude              | No         |
   | `:maestro.plugin.build.path/output`      | Output path for the uberjar   | Yes        |
   | `:maestro.plugin.build.uberjar/compiler` | Clojure compiler options      | No         |
   | `:maestro.plugin.build.uberjar/main`     | Namespace containing `-main`  | No         |   

   Clojure compiler options like activating direct linking are [described here](https://clojure.org/reference/compilation#_compiler_options).
  
   It is often useful providing the exclusion paths globally as a top-level key-value in `deps.edn` rather than duplicating it in every alias.
   to build.

   JVM options passed to the Clojure compiler are deduced by concatenating `:jvm-opts` found in all aliases
   involved in the build."

  [arg+]

  (let [{:as          ctx
         basis        :maestro.plugin.build/basis
         path-class   :maestro.plugin.build.path/class
         path-uberjar :maestro.plugin.build.path/output} (-jar arg+)]
    (println "Compiling" (ctx :maestro.plugin.build/alias))
    (@-d*compile-clj {:basis        basis
                      :class-dir    path-class
                      :compile-opts (ctx :maestro.plugin.build.uberjar/compiler)
                      :java-opts    (into []
                                          (comp (map (ctx :aliases))
                                                (mapcat :jvm-opts))
                                          (ctx :maestro/require))
                      :src-dirs     (ctx :maestro.plugin.build.path/src+)})
    (println "Assembling uberjar to:"
             path-uberjar)
    (@-d*uber {:basis     basis
               :class-dir path-class
               :exclude   (ctx :maestro.plugin.build.path/exclude)
               :main      (ctx :maestro.plugin.build.uberjar/main)
               :uber-file path-uberjar})
    ctx))


;;;;;;;;;; 


(defmulti by-type

  "Called by [[build]] after some initial preparation.
   Dispatches on `:maestro.build.plugin/type` to carry out the actual build steps.

   Supported types are:

   | Type       | See         |
   |------------|-------------|
   | `:jar`     | [[jar]]     |
   | `:uberjar` | [[uberjar]] |"

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


;;; Entry points


(defn build

  "Given a map with an alias to build under `:maestro.plugin.build/alias`, computes all required aliases
   after activating the `release` profile.

   Merges the contents with `deps.edn` (or the path mentioned under `:maestro/project`), the alias data of
   the alias to build and the given `arg+`. Then, passes everything to [[by-type]]."

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

  "Convenient way of calling [[build]] by providing an alias to build.
   
   Requires at least the alias under which Maestro and `tools.build` are imported. Alias to build is read
   as first command line argument if not provided explicitly.
  
   Useful as a Babashka task. For instance, in this repository, the jar for Maestro is built like this:

   ```
   bb build :module/maestro
   ```
  
   Options might contain:

   | Key                           | Value                                          |
   |-------------------------------|------------------------------------------------|
   | `:maestro.plugin.build/alias` | Alias to build (first CLI argument by default) |"


  ([alias-maestro]

   (task alias-maestro
         nil))


  ([alias-maestro option+]

   (@$.maestro.util/d*clojure (str "-X"
                                   (-> (protosens.maestro/search {:maestro/alias+ [alias-maestro]})
                                       (:maestro/require)
                                       ($.maestro.alias/stringify+)))
                              'protosens.maestro.plugin.build/build
                              (update option+
                                      :maestro.plugin.build/alias
                                      #(or %
                                           (edn/read-string (first *command-line-args*)))))))
