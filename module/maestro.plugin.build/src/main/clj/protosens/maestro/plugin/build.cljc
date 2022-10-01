(ns protosens.maestro.plugin.build

  "Maestro plugin for `tools.build` focused on building jars and uberjars, key information being located
   right in aliases.

   Aims to provide enough flexibility so that it would cover a majority of use cases. Also extensible by
   implementing methods for [[by-type]].

   Main entry point is [[build]] and [[task]] offers a fast way of getting into it using Babashka.
  
   <!> `tools.build` is not imported and must be brought by the user."

  #?(:clj (:import (java.nio.file Files)
                   (java.nio.file.attribute FileAttribute)))
          ;;
  #?(:bb  (:require [babashka.process        :as bb.process]
                    [clojure.edn             :as edn]
                    [protosens.maestro       :as $.maestro]
                    [protosens.maestro.alias :as $.maestro.alias]
                    [protosens.maestro.util  :as $.maestro.util])
          ;;
     :clj (:require [babashka.process          :as bb.process]
                    [clojure.edn               :as edn]
                    [clojure.tools.build.api   :as tools.build]
                    [protosens.maestro         :as $.maestro]
                    [protosens.maestro.alias   :as $.maestro.alias]
                    [protosens.maestro.profile :as $.maestro.profile])))


;;;;;;;;;; Tasks


#?(:bb  nil
   :clj (defn clean

  "Deletes the file under `:maestro.plugin.build.path/output`."

  [basis]

  (let [path (basis :maestro.plugin.build.path/output)]
    (println "Removing any previous output:"
             path)
    (tools.build/delete {:path path}))
  basis))



#?(:bb  nil
   :clj (defn copy-src

  "Copies source from `:maestro.plugin.build.path/src+` to `:maestro.plugin.build.path/class`."

  [basis]

  (println "Copying source paths")
  (tools.build/copy-dir {:src-dirs   (let [path-filter (basis :maestro.plugin.build.path/copy-filter)] 
                                       (cond->>
                                         (basis :maestro.plugin.build.path/src+)
                                         path-filter
                                         (filter path-filter)))
                         :target-dir (basis :maestro.plugin.build.path/class)})
  basis))



#?(:bb  nil
   :clj (defn tmp-dir

  "Creates a temporary directory and returns its path as a string.
   A prefix for the name may be provided."


  ([]

   (tmp-dir nil))


  ([prefix]

   (str (Files/createTempDirectory (or prefix
                                       "maestro-build-")
                                   (make-array FileAttribute
                                               0))))))


;;;;;;;;;;


#?(:bb  nil
   :clj (defn- -jar

  ;; Prelude common to jarring and uberjarring.
  ;;
  ;; Prepares paths and a temporary directory for work.

  [basis]

  (when-not (basis :maestro.plugin.build.path/output)
    ($.maestro/fail "Missing output path"))
  (let [dir-tmp (tmp-dir)]
    (println "Using temporary directory for building:"
             dir-tmp)
    (-> (merge basis
               {:maestro.plugin.build/basis       (tools.build/create-basis {:aliases (basis :maestro/require)
                                                                             :project (or (basis :maestro/project)
                                                                                          "deps.edn")})
                :maestro.plugin.build.path/class  (str dir-tmp
                                                       "/classes")
                :maestro.plugin.build.path/target dir-tmp})
        (clean)))))



#?(:bb  nil
   :clj (defn jar

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

  [basis]

  (let [alias-artifact
       (basis :maestro.plugin.build.alias/artifact)
       _  (or alias-artifact
              ($.maestro/fail "Missing artifact alias"))
       ;;
       dir-root
       (basis :maestro/root)
       _ (or dir-root
             ($.maestro/fail "Missing root directory"))
       ;;
        {:as        basis-2
         path-class :maestro.plugin.build.path/class
         path-jar   :maestro.plugin.build.path/output}
        (-jar basis)
        ;;
        [artifact
         version-map]
        (-> basis-2
            (get-in [:aliases
                     alias-artifact
                     :extra-deps])
            (first))
        ;;
        pom-config
        {:basis     (basis-2 :maestro.plugin.build/basis)
         :class-dir path-class
         :lib       artifact
         :src-pom   (or (basis-2 :maestro.plugin.build.path/pom)
                        "pom.xml")
         :version   (version-map :mvn/version)}
        ;;
        path+
        (basis :extra-paths)]
    (when (empty? path+)
      ($.maestro/fail "Missing paths"))
    (copy-src (assoc basis-2
                     :maestro.plugin.build.path/src+
                     path+))
    (println "Preparing POM file")
    (tools.build/write-pom pom-config)
    (let [path-pom-module (str dir-root
                               "/pom.xml")]
      (println "Copying POM file to:"
               path-pom-module)
      (tools.build/copy-file {:src    (tools.build/pom-path pom-config)
                              :target path-pom-module}))
    (println "Assemling jar to:"
             path-jar)
    (tools.build/jar {:class-dir path-class
                      :jar-file  path-jar})
    basis-2)))



#?(:bb  nil
   :clj (defn uberjar

  "Implementation for the `:uberjar` type in [[by-type]].
  
   Alias data for the build alias must or contain:

   | Key                                      | Value                            | Mandatory? |
   |------------------------------------------|----------------------------------|------------|
   | `:maestro.plugin.build.path/exclude`     | Paths to exclude (regex strings) | No         |
   | `:maestro.plugin.build.path/output`      | Output path for the uberjar      | Yes        |
   | `:maestro.plugin.build.uberjar/bind`     | Map of bindings for compilation  | No         |
   | `:maestro.plugin.build.uberjar/compiler` | Clojure compiler options         | No         |
   | `:maestro.plugin.build.uberjar/main`     | Namespace containing `-main`     | No         |   

   Clojure compiler options like activating direct linking are [described here](https://clojure.org/reference/compilation#_compiler_options).
   Bindings will be applied with `binding` when starting compilation. Useful for things like setting `*warn-on-reflection*`.
  
   It is often useful providing the exclusion paths globally as a top-level key-value in `deps.edn` rather than duplicating it in every alias.
   to build.

   JVM options passed to the Clojure compiler are deduced by concatenating `:jvm-opts` found in all aliases
   involved in the build."

  [basis]

  (let [{:as          basis-2
         basis-tb     :maestro.plugin.build/basis
         path-class   :maestro.plugin.build.path/class
         path-uberjar :maestro.plugin.build.path/output}
        (-jar basis)
        ;;
        path+
        ($.maestro.alias/extra-path+ basis-2
                                     (basis-2 :maestro/require))]
    (copy-src (assoc basis-2
                     :maestro.plugin.build.path/src+
                     path+))
    (println "Compiling" (basis-2 :maestro.plugin.build/alias))
    (tools.build/compile-clj {:basis        basis-tb
                              :bindings     (update-keys (basis-2 :maestro.plugin.build.uberjar/bind)
                                                         (fn [k]
                                                           (cond->
                                                             k
                                                             (symbol? k)
                                                             (resolve))))
                              :class-dir    path-class
                              :compile-opts (basis-2 :maestro.plugin.build.uberjar/compiler)
                              :java-opts    (into []
                                                  (comp (map (basis-2 :aliases))
                                                        (mapcat :jvm-opts))
                                                  (basis-2 :maestro/require))
                              :src-dirs     path+})
    (println "Assembling uberjar to:"
             path-uberjar)
    (tools.build/uber {:basis     basis-tb
                       :class-dir path-class
                       :exclude   (basis-2 :maestro.plugin.build.path/exclude)
                       :main      (basis-2 :maestro.plugin.build.uberjar/main)
                       :uber-file path-uberjar})
    basis-2)))


;;;;;;;;;; 


#?(:bb  nil
   :clj (defmulti by-type

  "Called by [[build]] after some initial preparation.
   Dispatches on `:maestro.build.plugin/type` to carry out the actual build steps.

   Supported types are:

   | Type       | See         |
   |------------|-------------|
   | `:jar`     | [[jar]]     |
   | `:uberjar` | [[uberjar]] |"

  :maestro.plugin.build/type))



#?(:bb  nil
   :clj (defmethod by-type

  :default

  [_basis]

  ($.maestro/fail "Missing build type")))



#?(:bb  nil
   :clj (defmethod by-type

  :jar

  [basis]

  (jar basis)))



#?(:bb  nil
   :clj (defmethod by-type

  :uberjar

  [basis]

  (uberjar basis)))


;;; Entry points


#?(:bb  nil
   :clj (defn build

  "Given a map with an alias to build under `:maestro.plugin.build/alias`, search for all required aliases
   after activating the `release` profile, using [[protosens.maestro/search]].

   Merges the result with the alias data of the alias to build and the given option map, prior to being
   passed to [[by-type]].

   In other words, options can be used to overwrite some information in the alias data of the target alias,
   like the output path of the artifact."

  [option+]

  (let [alias-build (option+ :maestro.plugin.build/alias)
        _           (when-not alias-build
                      ($.maestro/fail "Missing alias to build"))
        basis       ($.maestro/search (-> {:maestro/alias+   [alias-build]
                                           :maestro/profile+ ['release]}
                                          ($.maestro.profile/prepend+ (option+ :maestro/profile+))))]
    (-> (merge basis
               (get-in basis
                       [:aliases
                        alias-build])
               (dissoc option+
                       :maestro/alias+
                       :maestro/profile+))
        (assoc :maestro/require
               (basis :maestro/require))
        (by-type)))))



#?(:clj (defn task

  "Convenient way of calling [[build]] using `clojure -X`.

   Requires the alias that brings or `:maestro/require` this plugin.

   Alias to build is read as first command line argument if not provided under `:maestro.plugin.build/alias`
   in `option+`.
 
   Useful as a Babashka task. For instance, in this repository, the jar for Maestro is built like this:

   ```
   bb build :module/maestro
   ```
  
   Options will be passed to [[build]]."


  ([alias-maestro]

   (task alias-maestro
         nil))


  ([alias-maestro option+]

   (apply bb.process/shell
          "clojure"
          (str "-X"
               (-> ($.maestro/search {:maestro/alias+ [alias-maestro]})
                                     (:maestro/require)
                                     ($.maestro.alias/stringify+)))
          "protosens.maestro.plugin.build/build"
          (mapcat (fn [[k v]]
                    [(pr-str k)
                     (pr-str v)])
                  (update option+
                          :maestro.plugin.build/alias
                          #(or %
                               (some-> (first *command-line-args*)
                                       (edn/read-string))
                               ($.maestro/fail "Missing alias"))))))))
