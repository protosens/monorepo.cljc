# Table of contents
-  [`protosens.maestro.plugin.build`](#protosens.maestro.plugin.build)  - Maestro plugin for <code>tools.build</code> focused on building jars and uberjars.
    -  [`build`](#protosens.maestro.plugin.build/build) - Builds the module requested under <code>:maestro.plugin.build/alias</code>.
    -  [`by-type`](#protosens.maestro.plugin.build/by-type) - Carries out specific build steps depending on the target type.
    -  [`clean`](#protosens.maestro.plugin.build/clean) - Deletes the file under <code>:maestro.plugin.build.path/output</code>.
    -  [`copy-src`](#protosens.maestro.plugin.build/copy-src) - Copies source files.
    -  [`jar`](#protosens.maestro.plugin.build/jar) - Implementation for the <code>:jar</code> build type.
    -  [`main`](#protosens.maestro.plugin.build/main) - Higher-level task for building a module.
    -  [`tmp-dir`](#protosens.maestro.plugin.build/tmp-dir) - Creates a temporary directory and returns its path as a string.
    -  [`uberjar`](#protosens.maestro.plugin.build/uberjar) - Implementation for the <code>:uberjar</code> build type.

-----
# <a name="protosens.maestro.plugin.build">protosens.maestro.plugin.build</a>


Maestro plugin for `tools.build` focused on building jars and uberjars.
  
   Meant to declarative by reading key information from alias data. The premise of `tools.build` is that
   build are programs. Hence, this approach strives to offer a solution fit for common Clojure projects,
   a convenience often sufficient but not always.

   However, this approach is somewhat extensible via the [`by-type`](#protosens.maestro.plugin.build/by-type) multimethod.

   Main entry point is [`build`](#protosens.maestro.plugin.build/build) and [[task]] is a quick wrapper over it suited for Babashka.




## <a name="protosens.maestro.plugin.build/build">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/main/module/maestro.plugin.build/src/main/clj/protosens/maestro/plugin/build.cljc#L302-L332) `build`</a>
``` clojure

(build option+)
```


Builds the module requested under `:maestro.plugin.build/alias`.

   Uses [[protosens.maestro/search]] to query all required aliases.
   Activates the `release` profile by default.
  
   Merges the result with the alias data of the target alias and the given option map, prior to being
   passed to [`by-type`](#protosens.maestro.plugin.build/by-type).

   In other words, options can be used to overwrite some information in the alias data of the target alias,
   like the output path of the artifact.

## <a name="protosens.maestro.plugin.build/by-type">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/main/module/maestro.plugin.build/src/main/clj/protosens/maestro/plugin/build.cljc#L247-L262) `by-type`</a>

Carries out specific build steps depending on the target type.
  
   Called by [`build`](#protosens.maestro.plugin.build/build) after some initial preparation.

   Dispatches on `:maestro.build.plugin/type` to carry out the actual build steps.

   Supported types are:

   | Type       | See         |
   |------------|-------------|
   | `:jar`     | [`jar`](#protosens.maestro.plugin.build/jar)     |
   | `:uberjar` | [`uberjar`](#protosens.maestro.plugin.build/uberjar) |

## <a name="protosens.maestro.plugin.build/clean">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/main/module/maestro.plugin.build/src/main/clj/protosens/maestro/plugin/build.cljc#L32-L42) `clean`</a>
``` clojure

(clean basis)
```


Deletes the file under `:maestro.plugin.build.path/output`.

## <a name="protosens.maestro.plugin.build/copy-src">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/main/module/maestro.plugin.build/src/main/clj/protosens/maestro/plugin/build.cljc#L47-L62) `copy-src`</a>
``` clojure

(copy-src basis)
```


Copies source files.

   From `:maestro.plugin.build.path/src+` to `:maestro.plugin.build.path/class`.

## <a name="protosens.maestro.plugin.build/jar">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/main/module/maestro.plugin.build/src/main/clj/protosens/maestro/plugin/build.cljc#L115-L174) `jar`</a>
``` clojure

(jar basis)
```


Implementation for the `:jar` build type.
  
   See [`by-type`](#protosens.maestro.plugin.build/by-type).

   Alias data for the build alias must or may contain:

   | Key                                 | Value                         | Mandatory? | Default       |
   |-------------------------------------|-------------------------------|------------|---------------|
   | `:maestro/root`                     | Root directory of the alias   | Yes        | /             |
   | `:maestro.plugin.build.jar/name`    | Name of the artifact          | Yes        | /             |
   | `:maestro.plugin.build.jar/version` | Version of the artifact       | Yes        | /             |
   | `:maestro.plugin.build.path/output` | Output path for the jar       | Yes        | /             |
   | `:maestro.plugin.build.path/pom`    | Path to the template POM file | No         | `"pom.xml"` |

   A POM file will be created if necessary but it is often best starting from one that hosts key information
   that does not change from build to build like SCM, organization, etc. It will be copied to `./pom.xml` under
   `:maestro/root`.

## <a name="protosens.maestro.plugin.build/main">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/main/module/maestro.plugin.build/src/main/clj/protosens/maestro/plugin/build.cljc#L336-L374) `main`</a>
``` clojure

(main alias-plugin)
(main alias-plugin option+)
```


Higher-level task for building a module.
  
   Convenient way of calling [`build`](#protosens.maestro.plugin.build/build) using `clojure -X`.

   Alias to build is read as first command line argument if not provided under `:maestro.plugin.build/alias`
   in `option+`.
 
   Useful as a Babashka task. For instance, in this repository, the jar for Maestro is built like this:

   ```
   bb build :module/maestro
   ```
  
   Options will be passed to [`build`](#protosens.maestro.plugin.build/build).

## <a name="protosens.maestro.plugin.build/tmp-dir">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/main/module/maestro.plugin.build/src/main/clj/protosens/maestro/plugin/build.cljc#L67-L83) `tmp-dir`</a>
``` clojure

(tmp-dir)
(tmp-dir prefix)
```


Creates a temporary directory and returns its path as a string.
   A prefix for the name may be provided.

## <a name="protosens.maestro.plugin.build/uberjar">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/main/module/maestro.plugin.build/src/main/clj/protosens/maestro/plugin/build.cljc#L179-L240) `uberjar`</a>
``` clojure

(uberjar basis)
```


Implementation for the `:uberjar` build type.

   See [`by-type`](#protosens.maestro.plugin.build/by-type).
  
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
   involved in the build.

-----
