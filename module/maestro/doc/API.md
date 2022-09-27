# Table of contents
-  [`protosens.maestro`](#protosens.maestro)  - See README about core principles, [[search]] being the star of this namespace.
    -  [`by-profile+`](#protosens.maestro/by-profile+) - Extracts a set of all aliases required in the context of the given collection of profiles.
    -  [`cli-arg`](#protosens.maestro/cli-arg) - Reads a command-line argument and updates the basis accordingly.
    -  [`create-basis`](#protosens.maestro/create-basis) - Reads and prepares a <code>deps.edn</code> file.
    -  [`ensure-basis`](#protosens.maestro/ensure-basis) - Returns the given argument if it contains <code>:aliases</code>.
    -  [`not-by-profile+`](#protosens.maestro/not-by-profile+) - Extracts a set of all aliases NOT required in the context of the given collection of profiles.
    -  [`print`](#protosens.maestro/print) - Prints aliases from <code>:maestro/require</code> after concatenating them, the way Clojure CLI likes it.
    -  [`search`](#protosens.maestro/search) - Given input aliases and profiles, under <code>:maestro/alias+</code> and <code>:maestro/profile+</code> respectively, searches for all necessary aliases and puts the results in a vector under <code>:maestro/require</code>.
    -  [`task`](#protosens.maestro/task) - Like [[search]] but prepends aliases and profiles found using [[cli-arg]] and ends by [[print]]ing all required aliases.
-  [`protosens.maestro.aggr`](#protosens.maestro.aggr)  - When running [[protosens.maestro/search]], <code>basis</code> can contain an extra key <code>:maestro/aggr</code> pointing to a function such as <code>(fn [basis alias alias-data] basis-2)</code>.
    -  [`alias`](#protosens.maestro.aggr/alias) - In <code>basis</code>, appends <code>alias</code> under <code>:maestro/require</code>.
    -  [`default`](#protosens.maestro.aggr/default) - Default alias aggregating function for [[protosens.maestro/search]].
    -  [`env`](#protosens.maestro.aggr/env) - Merges <code>:maestro/env</code> from <code>alias-data</code> into <code>:maestro/env</code> in <code>basis</code>.
-  [`protosens.maestro.alias`](#protosens.maestro.alias)  - Miscellaneous helpers centered around aliases.
    -  [`append+`](#protosens.maestro.alias/append+) - In <code>basis</code>, add the given aliases as root aliases to resolve by appending them to any existing ones.
    -  [`extra-path+`](#protosens.maestro.alias/extra-path+) - Extracts a list of all paths provided in <code>:extra-paths</code> for the given list of aliases.
    -  [`prepend+`](#protosens.maestro.alias/prepend+) - In <code>basis</code>, add the given aliases as root aliases to resolve by prepending them to any existing ones.
    -  [`stringify+`](#protosens.maestro.alias/stringify+) - Stringifies the given collection of aliases by concatenating them, just like Clojure CLI likes it.
-  [`protosens.maestro.classpath`](#protosens.maestro.classpath)  - Simple classpath utilities.
    -  [`compute`](#protosens.maestro.classpath/compute) - Computes the classpath using the given aliases on <code>clojure</code>.
    -  [`pprint`](#protosens.maestro.classpath/pprint) - Pretty-prints the output from [[compute]] or <code>clojure -Spath ...</code> in alphabetical order given as argument or retrieved from STDIN.
-  [`protosens.maestro.doc`](#protosens.maestro.doc)  - Collection of miscellaneous helpers related to documentation.
    -  [`task`](#protosens.maestro.doc/task) - Prints documentation for a Babashka task.
-  [`protosens.maestro.git.lib`](#protosens.maestro.git.lib)  - Aliases that contains a name under <code>:maestro.git.lib/name</code> can be exposed publicly as git libraries and consumed from Clojure CLI via <code>:deps/root</code>.
    -  [`gen-deps`](#protosens.maestro.git.lib/gen-deps) - Generates custom <code>deps.edn</code> files for all aliases having in there data a name (see namespace description) as well as a <code>:maestro/root</code> (path to the root directory of that alias).
    -  [`gitlib?`](#protosens.maestro.git.lib/gitlib?) - Returns true if an alias (given its data) is meant to be exposed as a git library.
    -  [`prepare-deps-edn`](#protosens.maestro.git.lib/prepare-deps-edn) - Computes the content of the <code>deps.edn</code> file for the given <code>alias</code> meant to be exposed as a git library.
    -  [`task`](#protosens.maestro.git.lib/task) - Quick wrapper over [[gen-deps]], simply pretty-printing its result.
    -  [`write-deps-edn`](#protosens.maestro.git.lib/write-deps-edn) - Default way of writing a <code>deps-edn</code> file by pretty-printing it to the given <code>path</code>.
-  [`protosens.maestro.plugin.build`](#protosens.maestro.plugin.build)  - Maestro plugin for <code>tools.build</code> focused on building jars and uberjars, key information being located right in aliases.
    -  [`build`](#protosens.maestro.plugin.build/build) - Given a map with an alias to build under <code>:maestro.plugin.build/alias</code>, search for all required aliases after activating the <code>release</code> profile, using [[protosens.maestro/search]].
    -  [`by-type`](#protosens.maestro.plugin.build/by-type) - Called by [[build]] after some initial preparation.
    -  [`clean`](#protosens.maestro.plugin.build/clean) - Deletes the file under <code>:maestro.plugin.build.path/output</code>.
    -  [`copy-src`](#protosens.maestro.plugin.build/copy-src) - Copies source from <code>:maestro.plugin.build.path/src+</code> to <code>:maestro.plugin.build.path/class</code>.
    -  [`jar`](#protosens.maestro.plugin.build/jar) - Implementation for the <code>:jar</code> type in [[by-type]].
    -  [`task`](#protosens.maestro.plugin.build/task) - Convenient way of calling [[build]] using <code>clojure -X</code>.
    -  [`tmp-dir`](#protosens.maestro.plugin.build/tmp-dir) - Creates a temporary directory and returns its path as a string.
    -  [`uberjar`](#protosens.maestro.plugin.build/uberjar) - Implementation for the <code>:uberjar</code> type in [[by-type]].
-  [`protosens.maestro.plugin.clj-kondo`](#protosens.maestro.plugin.clj-kondo)  - Maestro plugin for linting Clojure code via Clj-kondo.
    -  [`lint`](#protosens.maestro.plugin.clj-kondo/lint) - Lints the whole repository by extracting <code>:extra-paths</code> from aliases.
    -  [`prepare`](#protosens.maestro.plugin.clj-kondo/prepare) - Prepares the Clj-kondo cache by linting all dependencies and copying configuration files.
-  [`protosens.maestro.plugin.deps-deploy`](#protosens.maestro.plugin.deps-deploy)  - Maestro plugin for installing and deploying artifacts via <code>slipset/deps-deploy</code>.
    -  [`clojars`](#protosens.maestro.plugin.deps-deploy/clojars) - Babashka task for deploying an artifact to Clojars.
    -  [`deploy`](#protosens.maestro.plugin.deps-deploy/deploy) - Core function for using <code>deps-deploy</code> via the <code>clojure</code> tool.
    -  [`local`](#protosens.maestro.plugin.deps-deploy/local) - Installs the given alias to the local Maven repository.
-  [`protosens.maestro.plugin.kaocha`](#protosens.maestro.plugin.kaocha)  - Maestro plugin for the Kaocha test runner reliably computing source and test paths for aliases you are working with.
    -  [`prepare`](#protosens.maestro.plugin.kaocha/prepare) - Given a <code>basis</code> that went through [[protosens.maestro/search]], produces an EDN file at containing <code>:kaocha/source-paths</code> and <code>:kaocha/test-paths</code>.
-  [`protosens.maestro.plugin.quickdoc`](#protosens.maestro.plugin.quickdoc)  - Maestro plugin generating markdown documentation for modules using [Quickdoc](https://github.com/borkdude/quickdoc) Works only with Babashka.
    -  [`task`](#protosens.maestro.plugin.quickdoc/task) - Generates documentation for all modules.
-  [`protosens.maestro.profile`](#protosens.maestro.profile)  - Miscellaneous helpers centered around profiles.
    -  [`append+`](#protosens.maestro.profile/append+) - In <code>basis</code>, activates the given profiles by appending them to any existing ones.
    -  [`prepend+`](#protosens.maestro.profile/prepend+) - In <code>basis</code>, activates the given profiles by prepending them to any existing ones.
-  [`protosens.maestro.user`](#protosens.maestro.user)  - Collection of helpers useful during development, often called in <code>user</code>.
    -  [`require-filtered`](#protosens.maestro.user/require-filtered) - Filters all namespaces found on the classpath and requires them.

-----
# <a name="protosens.maestro">protosens.maestro</a>


See README about core principles, [`search`](#protosens.maestro/search) being the star of this namespace.




## <a name="protosens.maestro/by-profile+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L215-L227) `by-profile+`</a>
``` clojure

(by-profile+ basis profile+)
```


Extracts a set of all aliases required in the context of the given collection of profiles.

   See [`search`](#protosens.maestro/search).

## <a name="protosens.maestro/cli-arg">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L43-L82) `cli-arg`</a>
``` clojure

(cli-arg basis)
(cli-arg basis arg)
```


Reads a command-line argument and updates the basis accordingly.
  
   Either:

   - Single alias
   - Vector of aliases and/or profiles

   Uses the first item of `*command-line-args*` by default.

   Given aliases and profiles are respectively appended to `:maestro/alias+` and `:maestro/profile+`.
   See [`search`](#protosens.maestro/search) for more information about the net effect.
  
   Often used right after [`create-basis`](#protosens.maestro/create-basis).

## <a name="protosens.maestro/create-basis">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L18-L40) `create-basis`</a>
``` clojure

(create-basis)
(create-basis option+)
```


Reads and prepares a `deps.edn` file.

   Takes a nilable map of options such as:

   | Key                | Value                                 | Default          |
   |--------------------|---------------------------------------|------------------|
   | `:maestro/project` | Alternative path to a `deps.edn` file | `"./deps.edn"` |

## <a name="protosens.maestro/ensure-basis">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L86-L96) `ensure-basis`</a>
``` clojure

(ensure-basis maybe-basis)
```


Returns the given argument if it contains `:aliases`.
   Otherwise, forwards it to [`create-basis`](#protosens.maestro/create-basis).

## <a name="protosens.maestro/not-by-profile+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L230-L246) `not-by-profile+`</a>
``` clojure

(not-by-profile+ basis profile+)
```


Extracts a set of all aliases NOT required in the context of the given collection of profiles.

   Opposite of [`by-profile+`](#protosens.maestro/by-profile+).

   See [`search`](#protosens.maestro/search).

## <a name="protosens.maestro/print">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L252-L263) `print`</a>
``` clojure

(print basis)
```


Prints aliases from `:maestro/require` after concatenating them, the way Clojure CLI likes it.
  
   See [`search`](#protosens.maestro/search).

## <a name="protosens.maestro/search">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L169-L209) `search`</a>
``` clojure

(search basis)
```


Given input aliases and profiles, under `:maestro/alias+` and `:maestro/profile+` respectively, searches
   for all necessary aliases and puts the results in a vector under `:maestro/require`.

   Input will go through [`ensure-basis`](#protosens.maestro/ensure-basis) first.

   Also remembers which profiles resulted in which aliases being selected under `:maestro/profile->alias+`.

   Alias data in `deps.edn` can also contain a vector of qualified symbols under `:maestro/on-require`. Those
   are resolved to functions and executed with the results at the very end if required.

   See the following namespaces for additional helpers:

   - [`protosens.maestro.aggr`](#protosens.maestro.aggr) for expert users needing this function to do more
   - [`protosens.maestro.alias`](#protosens.maestro.alias)
   - [`protosens.maestro.profile`](#protosens.maestro.profile)

## <a name="protosens.maestro/task">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L268-L288) `task`</a>
``` clojure

(task)
(task basis)
```


Like [`search`](#protosens.maestro/search) but prepends aliases and profiles found using [`cli-arg`](#protosens.maestro/cli-arg) and ends by [`print`](#protosens.maestro/print)ing all required aliases.

   Commonly used as a Babashka task.

-----
# <a name="protosens.maestro.aggr">protosens.maestro.aggr</a>


When running [`protosens.maestro/search`](#protosens.maestro/search), `basis` can contain an extra key `:maestro/aggr`
   pointing to a function such as `(fn [basis alias alias-data] basis-2)`.
  
   By default, this function is [`default`](#protosens.maestro.aggr/default). Technically, power users can provided an alternative implementation
   for additional features.




## <a name="protosens.maestro.aggr/alias">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/aggr.clj#L15-L31) `alias`</a>
``` clojure

(alias basis alias)
(alias basis alias _alias-data)
```


In `basis`, appends `alias` under `:maestro/require`.

## <a name="protosens.maestro.aggr/default">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/aggr.clj#L61-L84) `default`</a>
``` clojure

(default basis alias)
(default basis alias alias-data)
```


Default alias aggregating function for [`protosens.maestro/search`](#protosens.maestro/search).

   Uses:

   - [`alias`](#protosens.maestro.aggr/alias)
   - [`env`](#protosens.maestro.aggr/env)

## <a name="protosens.maestro.aggr/env">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/aggr.clj#L35-L55) `env`</a>
``` clojure

(env basis alias-data)
(env basis _alias alias-data)
```


Merges `:maestro/env` from `alias-data` into `:maestro/env` in `basis`.

   Those are typically used to represent environment variables and become useful
   when executing a process. For instance, sell utilities in Babashka accepts such
   a map of environment variables.

-----
# <a name="protosens.maestro.alias">protosens.maestro.alias</a>


Miscellaneous helpers centered around aliases.
  
   See the [`protosens.maestro`](#protosens.maestro) namespace.




## <a name="protosens.maestro.alias/append+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/alias.clj#L14-L23) `append+`</a>
``` clojure

(append+ basis alias+)
```


In `basis`, add the given aliases as root aliases to resolve by appending them to
   any existing ones.

## <a name="protosens.maestro.alias/extra-path+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/alias.clj#L41-L54) `extra-path+`</a>
``` clojure

(extra-path+ basis alias+)
```


Extracts a list of all paths provided in `:extra-paths` for the given list of aliases.

   Notable use-cases are:

   - Working with [tools.build](https://clojure.org/guides/tools_build)
   - Fetching test paths for tests runners like [Kaocha](https://github.com/lambdaisland/kaocha)

## <a name="protosens.maestro.alias/prepend+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/alias.clj#L26-L35) `prepend+`</a>
``` clojure

(prepend+ basis alias+)
```


In `basis`, add the given aliases as root aliases to resolve by prepending them to
   any existing ones.

## <a name="protosens.maestro.alias/stringify+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/alias.clj#L57-L63) `stringify+`</a>
``` clojure

(stringify+ alias+)
```


Stringifies the given collection of aliases by concatenating them, just like Clojure CLI likes it.

-----
# <a name="protosens.maestro.classpath">protosens.maestro.classpath</a>


Simple classpath utilities.




## <a name="protosens.maestro.classpath/compute">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/classpath.clj#L13-L25) `compute`</a>
``` clojure

(compute alias+)
```


Computes the classpath using the given aliases on `clojure`.
  
   Only works in Babashka.

## <a name="protosens.maestro.classpath/pprint">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/classpath.clj#L29-L46) `pprint`</a>
``` clojure

(pprint)
(pprint raw-cp)
```


Pretty-prints the output from [`compute`](#protosens.maestro.classpath/compute) or `clojure -Spath ...` in alphabetical order given as argument
   or retrieved from STDIN.

-----
# <a name="protosens.maestro.doc">protosens.maestro.doc</a>


Collection of miscellaneous helpers related to documentation.




## <a name="protosens.maestro.doc/task">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/doc.clj#L12-L74) `task`</a>
``` clojure

(task root)
(task root option+)
```


Prints documentation for a Babashka task.
  
   `root` is a path to a directory hosting documentation text files, one per task.
   Those must be named after the task they document.

   For instance, at the root of the public Protosens monorepo, try:

   ```
   bb doc deploy:clojars
   ```

   Options may be:

   | Key          | Value                                          | Default      |
   |--------------|------------------------------------------------|--------------|
   | `:bb`        | Path to the Babashka config file hosting tasks | `"bb.edn"` |
   | `:extension` | Extension of text files in the root directory  | `".txt"`   |

-----
# <a name="protosens.maestro.git.lib">protosens.maestro.git.lib</a>


Aliases that contains a name under `:maestro.git.lib/name` can be exposed publicly as
   git libraries and consumed from Clojure CLI via `:deps/root`.

   A name is a symbol `<organization>/<artifact>` such as `com.acme/some-lib`.

   In order to do so, each such module must have its own `deps.edn` file.
   See [`gen-deps`](#protosens.maestro.git.lib/gen-deps).




## <a name="protosens.maestro.git.lib/gen-deps">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/git/lib.clj#L125-L172) `gen-deps`</a>
``` clojure

(gen-deps)
(gen-deps basis)
```


Generates custom `deps.edn` files for all aliases having in there data a name (see namespace
   description) as well as a `:maestro/root` (path to the root directory of that alias).

   The algorithm is descrived in [`prepare-deps-edn`](#protosens.maestro.git.lib/prepare-deps-edn).

   When a `deps.edn` file has been computed, it is written to disk by [`write-deps-edn`](#protosens.maestro.git.lib/write-deps-edn). This
   can be overwritten by providing an alternative function under `:maestro.git.lib/write`.
   

   Returns a map where keys are aliased for which a `deps.edn` file has been generated and values
   are the data returned from [`prepare-deps-edn`](#protosens.maestro.git.lib/prepare-deps-edn).

## <a name="protosens.maestro.git.lib/gitlib?">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/git/lib.clj#L22-L28) `gitlib?`</a>
``` clojure

(gitlib? alias-data)
```


Returns true if an alias (given its data) is meant to be exposed as a git library.

## <a name="protosens.maestro.git.lib/prepare-deps-edn">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/git/lib.clj#L32-L103) `prepare-deps-edn`</a>
``` clojure

(prepare-deps-edn basis alias)
```


Computes the content of the `deps.edn` file for the given `alias` meant to be exposed
   as a git library.

   The algorithm uses [`protosens.maestro/search`](#protosens.maestro/search) starting with `basis`.
   The `release` profile is activated by default.
  
   For each required alias:

     - Merge `:extra-deps`
     - If the required alias is itself exposed as a git library, require it as a `:local/root` dependency
     - If not, merge `:extra-paths`

   Fails if a path to merge is not a child of the `:maestro/root` of the alias.

   Returns a map with:

   | Key                              | Value                                           |
   |----------------------------------|-------------------------------------------------|
   | `:maestro/require`               | Vector of required aliases                      |
   | `:maestro.git.lib/deps.edn`      | `deps.edn` map                                  |
   | `:maestro.git.lib.path/deps.edn` | Path where the `deps.edn` map should be written |

## <a name="protosens.maestro.git.lib/task">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/git/lib.clj#L176-L192) `task`</a>
``` clojure

(task)
(task basis)
```


Quick wrapper over [`gen-deps`](#protosens.maestro.git.lib/gen-deps), simply pretty-printing its result.
  
   Meant to be used as a Babashka task.

## <a name="protosens.maestro.git.lib/write-deps-edn">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/git/lib.clj#L107-L119) `write-deps-edn`</a>
``` clojure

(write-deps-edn path deps-edn)
```


Default way of writing a `deps-edn` file by pretty-printing it to the given `path`.

-----
# <a name="protosens.maestro.plugin.build">protosens.maestro.plugin.build</a>


Maestro plugin for `tools.build` focused on building jars and uberjars, key information being located
   right in aliases.

   Aims to provide enough flexibility so that it would cover a majority of use cases. Also extensible by
   implementing methods for [`by-type`](#protosens.maestro.plugin.build/by-type).

   Main entry point is [`build`](#protosens.maestro.plugin.build/build) and [`task`](#protosens.maestro.plugin.build/task) offers a fast way of getting into it using Babashka.
  
   <!> `tools.build` is not imported and must be brought by the user.




## <a name="protosens.maestro.plugin.build/build">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/plugin/build.clj#L399-L427) `build`</a>
``` clojure

(build option+)
```


Given a map with an alias to build under `:maestro.plugin.build/alias`, search for all required aliases
   after activating the `release` profile, using [`protosens.maestro/search`](#protosens.maestro/search).

   Merges the result with the alias data of the alias to build and the given option map, prior to being
   passed to [`by-type`](#protosens.maestro.plugin.build/by-type).

   In other words, options can be used to overwrite some information in the alias data of the target alias,
   like the output path of the artifact.

## <a name="protosens.maestro.plugin.build/by-type">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/plugin/build.clj#L351-L363) `by-type`</a>

Called by [`build`](#protosens.maestro.plugin.build/build) after some initial preparation.
   Dispatches on `:maestro.build.plugin/type` to carry out the actual build steps.

   Supported types are:

   | Type       | See         |
   |------------|-------------|
   | `:jar`     | [`jar`](#protosens.maestro.plugin.build/jar)     |
   | `:uberjar` | [`uberjar`](#protosens.maestro.plugin.build/uberjar) |

## <a name="protosens.maestro.plugin.build/clean">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/plugin/build.clj#L108-L118) `clean`</a>
``` clojure

(clean basis)
```


Deletes the file under `:maestro.plugin.build.path/output`.

## <a name="protosens.maestro.plugin.build/copy-src">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/plugin/build.clj#L122-L135) `copy-src`</a>
``` clojure

(copy-src basis)
```


Copies source from `:maestro.plugin.build.path/src+` to `:maestro.plugin.build.path/class`.

## <a name="protosens.maestro.plugin.build/jar">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/plugin/build.clj#L191-L290) `jar`</a>
``` clojure

(jar basis)
```


Implementation for the `:jar` type in [`by-type`](#protosens.maestro.plugin.build/by-type).

   Alias data for the build alias must or may contain:

   | Key                                    | Value                         | Mandatory? | Default       |
   |----------------------------------------|-------------------------------|------------|---------------|
   | `:maestro/root`                        | Root directory of the alias   | Yes        | /             |
   | `:maestro.plugin.build.alias/artifact` | Artifact alias (see below)    | Yes        | /             |
   | `:maestro.plugin.build.path/output`    | Output path for the jar       | Yes        | /             |
   | `:maestro.plugin.build.path/pom`       | Path to the template POM file | No         | `"pom.xml"` |

   A POM file will be created if necessary but it is often best starting from one that hosts key information
   that does not change from build to build like SCM, organization, etc. It will be copied to `./pom.xml` under
   `:maestro/root`.

   The artifact alias is an alias representing your release in its `:extra-deps` and nothing else. This is
   where the artifact name and version are extracted from. For instance, in this repository, `deps.edn` contains
   this artifact alias related to `:module/maestro`:

   ```clojure
   {:release/maestro
    {:extra-deps {com.protosens/maestro {:mvn/version "x.x.x"}}
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

   Note: it is best activating the `release` alias when doing that sort of things.

## <a name="protosens.maestro.plugin.build/task">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/plugin/build.clj#L431-L463) `task`</a>
``` clojure

(task alias-maestro)
(task alias-maestro option+)
```


Convenient way of calling [`build`](#protosens.maestro.plugin.build/build) using `clojure -X`.
 
   Requires at least the alias under which Maestro and `tools.build` are imported. Alias to build is read
   as first command line argument if not provided explicitly.
  
   Useful as a Babashka task. For instance, in this repository, the jar for Maestro is built like this:

   ```
   bb build :module/maestro
   ```
  
   Options will be passed to [`build`](#protosens.maestro.plugin.build/build).

## <a name="protosens.maestro.plugin.build/tmp-dir">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/plugin/build.clj#L139-L155) `tmp-dir`</a>
``` clojure

(tmp-dir)
(tmp-dir prefix)
```


Creates a temporary directory and returns its path as a string.
   A prefix for the name may be provided.

## <a name="protosens.maestro.plugin.build/uberjar">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/plugin/build.clj#L294-L345) `uberjar`</a>
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

-----
# <a name="protosens.maestro.plugin.clj-kondo">protosens.maestro.plugin.clj-kondo</a>


Maestro plugin for linting Clojure code via Clj-kondo.
  
   Assumes it is already and `clj-kondo` is available in the shell.




## <a name="protosens.maestro.plugin.clj-kondo/lint">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/plugin/clj_kondo.clj#L32-L67) `lint`</a>
``` clojure

(lint)
(lint option+)
```


Lints the whole repository by extracting `:extra-paths` from aliases.

   Options may be:

   | Key            | Value                                                       |
   |----------------|-------------------------------------------------------------|
   | `:path-filter` | Predicate function deciding whether a path should be linted |

## <a name="protosens.maestro.plugin.clj-kondo/prepare">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/plugin/clj_kondo.clj#L16-L28) `prepare`</a>
``` clojure

(prepare)
```


Prepares the Clj-kondo cache by linting all dependencies and copying configuration files.
  
   Should be called prior to [`lint`](#protosens.maestro.plugin.clj-kondo/lint)ing for the first time and on dependency updates.

-----
# <a name="protosens.maestro.plugin.deps-deploy">protosens.maestro.plugin.deps-deploy</a>


Maestro plugin for installing and deploying artifacts via `slipset/deps-deploy`.

   Works even better in combination with [`protosens.maestro.plugin.build`](#protosens.maestro.plugin.build).

   Babashka tasks:

   - [`clojars`](#protosens.maestro.plugin.deps-deploy/clojars)
   - [`local`](#protosens.maestro.plugin.deps-deploy/local)




## <a name="protosens.maestro.plugin.deps-deploy/clojars">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/plugin/deps_deploy.clj#L78-L111) `clojars`</a>
``` clojure

(clojars alias-deps-deploy)
(clojars alias-deps-deploy username path-token alias-deploy)
```


Babashka task for deploying an artifact to Clojars.

   See [`deploy`](#protosens.maestro.plugin.deps-deploy/deploy) about `:maestro.plugin.deps-deploy/exec-args`.

   `username`, `path-token`, and `alias` are taken from command line arguments
   if not provided explicitly.

   `path-token` is the path to the file containing the Clojars deploy token to use.

## <a name="protosens.maestro.plugin.deps-deploy/deploy">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/plugin/deps_deploy.clj#L32-L72) `deploy`</a>
``` clojure

(deploy alias-deps-deploy installer alias-deploy env)
```


Core function for using `deps-deploy` via the `clojure` tool.
   Works only using Babashka.
  
   | Argument           | Value                               |
   |--------------------|-------------------------------------|
   |`alias-deps-deploy` | Alias providing `deps-deploy`       |
   |`installer`         | See `deps-deploy` documentation     |
   |`alias-deploy`      | Alias to deploy                     |
   |`env`               | Map of environment variables to set | 

   The alias data of `alias-deploy` may contain arguments for `deps-deploy` under
   `:maestro.plugin.deps-deploy/exec-args`. Those ones are filled-in based on alias data
   when not provided:

   | Key         | Value                                             |
   |-------------|---------------------------------------------------|
   | `:artifact` | Value of `:maestro.plugin.build.path/output`      |
   | `:pom-file` | "pom.xml" file assumed to be in `:maestro/root` |

## <a name="protosens.maestro.plugin.deps-deploy/local">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/plugin/deps_deploy.clj#L115-L139) `local`</a>
``` clojure

(local alias-deps-deploy)
(local alias-deps-deploy alias-deploy)
```


Installs the given alias to the local Maven repository.

   Alias to install will be taken from the first command line argument if not provided
   explicitly.

   See [`deploy`](#protosens.maestro.plugin.deps-deploy/deploy) about `:maestro.plugin.deps-deploy/exec-args`.

-----
# <a name="protosens.maestro.plugin.kaocha">protosens.maestro.plugin.kaocha</a>


Maestro plugin for the Kaocha test runner reliably computing source and test paths for aliases you are
   working with. No need to maintain several test suites manually.




## <a name="protosens.maestro.plugin.kaocha/prepare">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/plugin/kaocha.clj#L14-L47) `prepare`</a>
``` clojure

(prepare basis)
```


Given a `basis` that went through [`protosens.maestro/search`](#protosens.maestro/search), produces an EDN file
   at containing `:kaocha/source-paths` and `:kaocha/test-paths`.

   The path for that EDN file must be provided under `:maestro.plugin.kaocha/path`. It should be
   ignored in `.gitignore` as it is not meant to be checked out.

   Your Kaocha configuration file can then refer to it:

   ```clojure
   #kaocha/v1
   {:tests [#meta-merge [{:id                 :unit
                          :kaocha/ns-patterns [".+"]}
                         #include "<PATH>"]]}
   ```

   Test paths are deduced from aliases that were required by activing the `test` profile`. Source
   paths are all the remaining paths.

-----
# <a name="protosens.maestro.plugin.quickdoc">protosens.maestro.plugin.quickdoc</a>


Maestro plugin generating markdown documentation for modules using [Quickdoc](https://github.com/borkdude/quickdoc)

   Works only with Babashka.




## <a name="protosens.maestro.plugin.quickdoc/task">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/plugin/quickdoc.clj#L15-L45) `task`</a>
``` clojure

(task)
(task option+)
```


Generates documentation for all modules.

   Alias data for the module must contain `:extra-paths`, those will be the source
   provided for analysis. To activate Quickdoc, it must also contain `:maestro.plugin.quickdoc.path/output`
   specifying the output path for the generated markdown.

-----
# <a name="protosens.maestro.profile">protosens.maestro.profile</a>


Miscellaneous helpers centered around profiles.
  
   See the [`protosens.maestro`](#protosens.maestro) namespace.




## <a name="protosens.maestro.profile/append+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/profile.clj#L13-L21) `append+`</a>
``` clojure

(append+ basis profile+)
```


In `basis`, activates the given profiles by appending them to any existing ones.

## <a name="protosens.maestro.profile/prepend+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/profile.clj#L24-L32) `prepend+`</a>
``` clojure

(prepend+ basis profile+)
```


In `basis`, activates the given profiles by prepending them to any existing ones.

-----
# <a name="protosens.maestro.user">protosens.maestro.user</a>


Collection of helpers useful during development, often called in `user`.




## <a name="protosens.maestro.user/require-filtered">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/user.clj#L12-L75) `require-filtered`</a>
``` clojure

(require-filtered option+)
```


Filters all namespaces found on the classpath and requires them.

   Useful to invoke in `user` for ensuring that all expected namespaces compiles correctly.

   Actually, it is useful defining in `user` a short function that calls this one.
   Since `user` is accessible from everywhere, it is an easy solution for quickly requiring needed
   namespaces from anywhere at the REPL.

   Options are:

   | Key               | Value                                                                | Default |
   |-------------------|----------------------------------------------------------------------|---------|
   | `:fail-fast?`     | Stop when requiring one namespace fails?                             | `true`  |
   | `:map-namespace`  | Function used for mapping found namespaces                           | /       |
   | `:require.after`  | Function called with a namespace after requiring it                  | /       |
   | `:require.before` | Function called with a namespace before requiring it                 | /       |
   | `:require.fail`   | Function called with a namespace and an exception in case of failure | /       |

   The value returned by `:map-namespace`, if any, will be passed to `require` as well as to any
   of `:require...` functions above.

-----
