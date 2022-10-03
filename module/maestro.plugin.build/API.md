# Table of contents
-  [`protosens.maestro.plugin.build`](#protosens.maestro.plugin.build)  - Maestro plugin for <code>tools.build</code> focused on building jars and uberjars, key information being located right in aliases.
    -  [`build`](#protosens.maestro.plugin.build/build) - Given a map with an alias to build under <code>:maestro.plugin.build/alias</code>, search for all required aliases after activating the <code>release</code> profile, using [[protosens.maestro/search]].
    -  [`by-type`](#protosens.maestro.plugin.build/by-type) - Called by [[build]] after some initial preparation.
    -  [`clean`](#protosens.maestro.plugin.build/clean) - Deletes the file under <code>:maestro.plugin.build.path/output</code>.
    -  [`copy-src`](#protosens.maestro.plugin.build/copy-src) - Copies source from <code>:maestro.plugin.build.path/src+</code> to <code>:maestro.plugin.build.path/class</code>.
    -  [`jar`](#protosens.maestro.plugin.build/jar) - Implementation for the <code>:jar</code> type in [[by-type]].
    -  [`task`](#protosens.maestro.plugin.build/task) - Convenient way of calling [[build]] using <code>clojure -X</code>.
    -  [`tmp-dir`](#protosens.maestro.plugin.build/tmp-dir) - Creates a temporary directory and returns its path as a string.
    -  [`uberjar`](#protosens.maestro.plugin.build/uberjar) - Implementation for the <code>:uberjar</code> type in [[by-type]].

-----
# <a name="protosens.maestro.plugin.build">protosens.maestro.plugin.build</a>


Maestro plugin for `tools.build` focused on building jars and uberjars, key information being located
   right in aliases.

   Aims to provide enough flexibility so that it would cover a majority of use cases. Also extensible by
   implementing methods for [`by-type`](#protosens.maestro.plugin.build/by-type).

   Main entry point is [`build`](#protosens.maestro.plugin.build/build) and [`task`](#protosens.maestro.plugin.build/task) offers a fast way of getting into it using Babashka.
  
   <!> `tools.build` is not imported and must be brought by the user.




## <a name="protosens.maestro.plugin.build/build">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.plugin.build/src/main/clj/protosens/maestro/plugin/build.cljc#L291-L319) `build`</a>
``` clojure

(build option+)
```


Given a map with an alias to build under `:maestro.plugin.build/alias`, search for all required aliases
   after activating the `release` profile, using [[protosens.maestro/search]].

   Merges the result with the alias data of the alias to build and the given option map, prior to being
   passed to [`by-type`](#protosens.maestro.plugin.build/by-type).

   In other words, options can be used to overwrite some information in the alias data of the target alias,
   like the output path of the artifact.

## <a name="protosens.maestro.plugin.build/by-type">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.plugin.build/src/main/clj/protosens/maestro/plugin/build.cljc#L239-L251) `by-type`</a>

Called by [`build`](#protosens.maestro.plugin.build/build) after some initial preparation.
   Dispatches on `:maestro.build.plugin/type` to carry out the actual build steps.

   Supported types are:

   | Type       | See         |
   |------------|-------------|
   | `:jar`     | [`jar`](#protosens.maestro.plugin.build/jar)     |
   | `:uberjar` | [`uberjar`](#protosens.maestro.plugin.build/uberjar) |

## <a name="protosens.maestro.plugin.build/clean">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.plugin.build/src/main/clj/protosens/maestro/plugin/build.cljc#L30-L40) `clean`</a>
``` clojure

(clean basis)
```


Deletes the file under `:maestro.plugin.build.path/output`.

## <a name="protosens.maestro.plugin.build/copy-src">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.plugin.build/src/main/clj/protosens/maestro/plugin/build.cljc#L45-L58) `copy-src`</a>
``` clojure

(copy-src basis)
```


Copies source from `:maestro.plugin.build.path/src+` to `:maestro.plugin.build.path/class`.

## <a name="protosens.maestro.plugin.build/jar">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.plugin.build/src/main/clj/protosens/maestro/plugin/build.cljc#L111-L168) `jar`</a>
``` clojure

(jar basis)
```


Implementation for the `:jar` type in [`by-type`](#protosens.maestro.plugin.build/by-type).

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

## <a name="protosens.maestro.plugin.build/task">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.plugin.build/src/main/clj/protosens/maestro/plugin/build.cljc#L323-L361) `task`</a>
``` clojure

(task alias-maestro)
(task alias-maestro option+)
```


Convenient way of calling [`build`](#protosens.maestro.plugin.build/build) using `clojure -X`.

   Requires the alias that brings or `:maestro/require` this plugin.

   Alias to build is read as first command line argument if not provided under `:maestro.plugin.build/alias`
   in `option+`.
 
   Useful as a Babashka task. For instance, in this repository, the jar for Maestro is built like this:

   ```
   bb build :module/maestro
   ```
  
   Options will be passed to [`build`](#protosens.maestro.plugin.build/build).

## <a name="protosens.maestro.plugin.build/tmp-dir">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.plugin.build/src/main/clj/protosens/maestro/plugin/build.cljc#L63-L79) `tmp-dir`</a>
``` clojure

(tmp-dir)
(tmp-dir prefix)
```


Creates a temporary directory and returns its path as a string.
   A prefix for the name may be provided.

## <a name="protosens.maestro.plugin.build/uberjar">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.plugin.build/src/main/clj/protosens/maestro/plugin/build.cljc#L173-L232) `uberjar`</a>
``` clojure

(uberjar basis)
```


Implementation for the `:uberjar` type in [`by-type`](#protosens.maestro.plugin.build/by-type).
  
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
