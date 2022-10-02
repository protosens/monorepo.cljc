# Table of contents
-  [`protosens.maestro`](#protosens.maestro)  - See README about core principles, [[search]] being the star of this namespace.
    -  [`-*fail-mode`](#protosens.maestro/-*fail-mode)
    -  [`by-profile+`](#protosens.maestro/by-profile+) - Extracts a set of all aliases required in the context of the given collection of profiles.
    -  [`clojure`](#protosens.maestro/clojure) - Executes the <code>clojure</code> command with <code>-?</code> (-M, -X, ...) Behaves like [[task]] but instead of printing aliases, there are appended to <code>-?</code>.
    -  [`create-basis`](#protosens.maestro/create-basis) - Reads and prepares a <code>deps.edn</code> file.
    -  [`ensure-basis`](#protosens.maestro/ensure-basis) - Returns the given argument if it contains <code>:aliases</code>.
    -  [`fail`](#protosens.maestro/fail) - Fails with the given error <code>message</code>.
    -  [`fail-mode`](#protosens.maestro/fail-mode) - How [[fail]] behaves.
    -  [`not-by-profile+`](#protosens.maestro/not-by-profile+) - Extracts a set of all aliases NOT required in the context of the given collection of profiles.
    -  [`print`](#protosens.maestro/print) - Prints aliases from <code>:maestro/require</code> after concatenating them, the way Clojure CLI likes it.
    -  [`search`](#protosens.maestro/search) - Given input aliases and profiles, under <code>:maestro/alias+</code> and <code>:maestro/profile+</code> respectively, searches for all necessary aliases and puts the results in a vector under <code>:maestro/require</code>.
    -  [`sort-arg`](#protosens.maestro/sort-arg) - Sorts aliases and vectors into a map of <code>:maestro/alias+</code> and <code>:maestro/profile+</code>.
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
    -  [`print-help`](#protosens.maestro.doc/print-help) - Prints a documentation file from the <code>root</code> directory.
    -  [`print-task`](#protosens.maestro.doc/print-task) - Pretty-prints extra documentation for a task (if there is any).
    -  [`report-undocumented-task+`](#protosens.maestro.doc/report-undocumented-task+) - Pretty-prints the result of [[undocumented-task+]].
    -  [`undocumented-task+`](#protosens.maestro.doc/undocumented-task+) - Returns a sorted list of tasks which do not have a <code>:maestro/doc</code>.
-  [`protosens.maestro.git.lib`](#protosens.maestro.git.lib)  - Aliases that contains a name under <code>:maestro.git.lib/name</code> can be exposed publicly as git libraries and consumed from Clojure CLI via <code>:deps/root</code>.
    -  [`expose`](#protosens.maestro.git.lib/expose) - Generates custom <code>deps.edn</code> files for all aliases having in there data a name (see namespace description) as well as a <code>:maestro/root</code> (path to the root directory of that alias).
    -  [`gitlib?`](#protosens.maestro.git.lib/gitlib?) - Returns true if an alias (given its data) is meant to be exposed as a git library.
    -  [`prepare-deps-edn`](#protosens.maestro.git.lib/prepare-deps-edn) - Computes the content of the <code>deps.edn</code> file for the given <code>alias</code> meant to be exposed as a git library.
    -  [`task`](#protosens.maestro.git.lib/task) - Task reliably exposing modules as Git libraries to consume externally.
    -  [`write-deps-edn`](#protosens.maestro.git.lib/write-deps-edn) - Default way of writing a <code>deps-edn</code> file by pretty-printing it to the given <code>path</code>.
-  [`protosens.maestro.profile`](#protosens.maestro.profile)  - Miscellaneous helpers centered around profiles.
    -  [`append+`](#protosens.maestro.profile/append+) - In <code>basis</code>, activates the given profiles by appending them to any existing ones.
    -  [`prepend+`](#protosens.maestro.profile/prepend+) - In <code>basis</code>, activates the given profiles by prepending them to any existing ones.
-  [`protosens.maestro.uber`](#protosens.maestro.uber)  - Special way of merging aliases in a generated <code>deps.edn</code> file.
    -  [`task`](#protosens.maestro.uber/task) - Generate a single <code>deps.edn</code> file by merging everything required by <code>alias</code>.

-----
# <a name="protosens.maestro">protosens.maestro</a>


See README about core principles, [`search`](#protosens.maestro/search) being the star of this namespace.




## <a name="protosens.maestro/-*fail-mode">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L23-L27) `-*fail-mode`</a>

## <a name="protosens.maestro/by-profile+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L304-L316) `by-profile+`</a>
``` clojure

(by-profile+ basis profile+)
```


Extracts a set of all aliases required in the context of the given collection of profiles.

   See [`search`](#protosens.maestro/search).

## <a name="protosens.maestro/clojure">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L392-L436) `clojure`</a>
``` clojure

(clojure -?)
(clojure -? basis)
```


Executes the `clojure` command with `-?` (-M, -X, ...)

   Behaves like [`task`](#protosens.maestro/task) but instead of printing aliases, there are appended
   to `-?`.

   CLI arguments are split in 2 if there is a `--` argument. What is before
   it will be applied as CLI arguments for [`task`](#protosens.maestro/task). Anything after it will
   be feed as additional CLI arguments for the `clojure` command.

   ```clojure
   ;; E.g. CLI args like:  :some/module -- -m some.namespace 1 2 3 
   (clojure "-M")
   ```

   The `basis` argument is forwarded to [`task`](#protosens.maestro/task).

   Works only with Babashka.

## <a name="protosens.maestro/create-basis">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L87-L109) `create-basis`</a>
``` clojure

(create-basis)
(create-basis option+)
```


Reads and prepares a `deps.edn` file.

   Takes a nilable map of options such as:

   | Key                | Value                                 | Default          |
   |--------------------|---------------------------------------|------------------|
   | `:maestro/project` | Alternative path to a `deps.edn` file | `"./deps.edn"` |

## <a name="protosens.maestro/ensure-basis">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L113-L123) `ensure-basis`</a>
``` clojure

(ensure-basis maybe-basis)
```


Returns the given argument if it contains `:aliases`.
   Otherwise, forwards it to [`create-basis`](#protosens.maestro/create-basis).

## <a name="protosens.maestro/fail">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L31-L51) `fail`</a>
``` clojure

(fail message)
```


Fails with the given error `message`.

   Plugin authors and such should use this function to guarantee consistent behavior.

   Re-aligns multiline strings.
  
   See [`fail-mode`](#protosens.maestro/fail-mode).

## <a name="protosens.maestro/fail-mode">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L55-L81) `fail-mode`</a>
``` clojure

(fail-mode)
(fail-mode mode)
```


How [`fail`](#protosens.maestro/fail) behaves.
  
   There are 2 modes:

   - `:exit` is usually prefered on Babashka ; error message is printed and process exits with 1
   - `:throw` might be preferred on the JVM ; an exception is thrown with the error message

   Sets behavior to the given `mode`.
   Without argument, returns the current one (default is `:exit`).

## <a name="protosens.maestro/not-by-profile+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L319-L335) `not-by-profile+`</a>
``` clojure

(not-by-profile+ basis profile+)
```


Extracts a set of all aliases NOT required in the context of the given collection of profiles.

   Opposite of [`by-profile+`](#protosens.maestro/by-profile+).

   See [`search`](#protosens.maestro/search).

## <a name="protosens.maestro/print">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L338-L349) `print`</a>
``` clojure

(print basis)
```


Prints aliases from `:maestro/require` after concatenating them, the way Clojure CLI likes it.
  
   See [`search`](#protosens.maestro/search).

## <a name="protosens.maestro/search">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L236-L298) `search`</a>
``` clojure

(search basis)
```


Given input aliases and profiles, under `:maestro/alias+` and `:maestro/profile+` respectively, searches
   for all necessary aliases and puts the results in a vector under `:maestro/require`.

   Input will go through [`ensure-basis`](#protosens.maestro/ensure-basis) first.

   Then, will apply the mode found under `:maestro/mode` if any. Modes are described in the basis under
   `:maestro/mode+`, a map of where keys are modes (typically keywords) and values can contain optional
   `:maestro/alias+` and `:maestro/profile+` to append before starting the search.

   Also remembers which profiles resulted in which aliases being selected under `:maestro/profile->alias+`.

   Alias data in `deps.edn` can also contain a vector of qualified symbols under `:maestro/on-require`. Those
   are resolved to functions and executed with the results at the very end if required.

   See the following namespaces for additional helpers:

   - [`protosens.maestro.aggr`](#protosens.maestro.aggr) for expert users needing this function to do more
   - [`protosens.maestro.alias`](#protosens.maestro.alias)
   - [`protosens.maestro.profile`](#protosens.maestro.profile)

## <a name="protosens.maestro/sort-arg">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L127-L158) `sort-arg`</a>
``` clojure

(sort-arg arg)
(sort-arg hmap arg)
```


Sorts aliases and vectors into a map of `:maestro/alias+` and `:maestro/profile+`.

   The map in question is often a basis (see [`create-basis`](#protosens.maestro/create-basis)).

   `arg` can be a vector to sort out or a single item. Useful for parsing aliases and
   profiles provided as a CLI argument.

## <a name="protosens.maestro/task">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L355-L386) `task`</a>
``` clojure

(task)
(task basis)
```


Like [`search`](#protosens.maestro/search) but prepends aliases and profiles found using [[cli-arg]] and ends by [`print`](#protosens.maestro/print)ing all required aliases.

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




## <a name="protosens.maestro.classpath/compute">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/classpath.clj#L13-L22) `compute`</a>
``` clojure

(compute alias+)
```


Computes the classpath using the given aliases on `clojure`.

## <a name="protosens.maestro.classpath/pprint">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/classpath.clj#L26-L43) `pprint`</a>
``` clojure

(pprint)
(pprint raw-cp)
```


Pretty-prints the output from [`compute`](#protosens.maestro.classpath/compute) or `clojure -Spath ...` in alphabetical order given as argument
   or retrieved from STDIN.

-----
# <a name="protosens.maestro.doc">protosens.maestro.doc</a>


Collection of miscellaneous helpers related to documentation.




## <a name="protosens.maestro.doc/print-help">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/doc.clj#L67-L113) `print-help`</a>
``` clojure

(print-help root)
(print-help root option+)
```


Prints a documentation file from the `root` directory.

   Options may be:

   | Key          | Value                                          | Default       |
   |--------------|------------------------------------------------|---------------|
   | `:extension` | Extension of text files in the root directory  | `".txt"`    |
   | `:target`    | File to print (without extension)              | First CLI arg |
  
   Without any target, prints all possible targets from the root.

   Useful as a Babashka task, a quick way for providing help.
   Also see [`print-task`](#protosens.maestro.doc/print-task).

## <a name="protosens.maestro.doc/print-task">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/doc.clj#L117-L173) `print-task`</a>
``` clojure

(print-task)
(print-task option+)
```


Pretty-prints extra documentation for a task (if there is any).

   Options may contain:

   | Key          | Value                                          | Default       |
   |--------------|------------------------------------------------|---------------|
   | `:bb`        | Path to the Babashka config file hosting tasks | `"bb.edn"`  |
   | `:target`    | Task to print (without extension)              | First CLI arg |

## <a name="protosens.maestro.doc/report-undocumented-task+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/doc.clj#L197-L216) `report-undocumented-task+`</a>
``` clojure

(report-undocumented-task+)
(report-undocumented-task+ option+)
```


Pretty-prints the result of [`undocumented-task+`](#protosens.maestro.doc/undocumented-task+).

## <a name="protosens.maestro.doc/undocumented-task+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/doc.clj#L177-L193) `undocumented-task+`</a>
``` clojure

(undocumented-task+ option+)
```


Returns a sorted list of tasks which do not have a `:maestro/doc`.

   Options may be:

   | Key          | Value                                          | Default      |
   |--------------|------------------------------------------------|--------------|
   | `:bb`        | Path to the Babashka config file hosting tasks | `"bb.edn"` |

-----
# <a name="protosens.maestro.git.lib">protosens.maestro.git.lib</a>


Aliases that contains a name under `:maestro.git.lib/name` can be exposed publicly as
   git libraries and consumed from Clojure CLI via `:deps/root`.

   A name is a symbol `<organization>/<artifact>` such as `com.acme/some-lib`.

   In order to do so, each such module must have its own `deps.edn` file.
   See [`expose`](#protosens.maestro.git.lib/expose) and [`task`](#protosens.maestro.git.lib/task).




## <a name="protosens.maestro.git.lib/expose">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/git/lib.clj#L143-L190) `expose`</a>
``` clojure

(expose git-sha)
(expose git-sha basis)
```


Generates custom `deps.edn` files for all aliases having in there data a name (see namespace
   description) as well as a `:maestro/root` (path to the root directory of that alias).

   The algorithm is described in [`prepare-deps-edn`](#protosens.maestro.git.lib/prepare-deps-edn).

   When a `deps.edn` file has been computed, it is written to disk by [`write-deps-edn`](#protosens.maestro.git.lib/write-deps-edn). This
   can be overwritten by providing an alternative function under `:maestro.git.lib/write`.
   
   Returns a map where keys are aliased for which a `deps.edn` file has been generated and values
   are the data returned from [`prepare-deps-edn`](#protosens.maestro.git.lib/prepare-deps-edn) without the `deps.edn` content.

## <a name="protosens.maestro.git.lib/gitlib?">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/git/lib.clj#L27-L33) `gitlib?`</a>
``` clojure

(gitlib? alias-data)
```


Returns true if an alias (given its data) is meant to be exposed as a git library.

## <a name="protosens.maestro.git.lib/prepare-deps-edn">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/git/lib.clj#L37-L118) `prepare-deps-edn`</a>
``` clojure

(prepare-deps-edn basis git-sha alias)
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

## <a name="protosens.maestro.git.lib/task">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/git/lib.clj#L194-L264) `task`</a>
``` clojure

(task)
(task basis)
```


Task reliably exposing modules as Git libraries to consume externally.

   These automated steps guarantees a safe deployment:

   - Ensure Git tree is absolutely clean
   - Run [`expose`](#protosens.maestro.git.lib/expose), commit (preparation)
   - Run [`expose`](#protosens.maestro.git.lib/expose) again, commit (actual exposition)
   - Print SHA of last commit, what user can consume

   This double commit ensures Clojure CLI will have no trouble finding everything without any
   clash.
  
   The commit messages can be customized by providing functions `git-sha of last commit` -> `message`
   under `:maestro.git.lib.message/prepare` and/or `:maestro.git.lib.message/expose`.

## <a name="protosens.maestro.git.lib/write-deps-edn">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/git/lib.clj#L122-L137) `write-deps-edn`</a>
``` clojure

(write-deps-edn path deps-edn)
```


Default way of writing a `deps-edn` file by pretty-printing it to the given `path`.

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
# <a name="protosens.maestro.uber">protosens.maestro.uber</a>


Special way of merging aliases in a generated `deps.edn` file.




## <a name="protosens.maestro.uber/task">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/uber.clj#L95-L161) `task`</a>
``` clojure

(task alias)
(task alias basis)
```


Generate a single `deps.edn` file by merging everything required by `alias`.

   This is probably only useful in a limited set of dev use cases. One notable
   example is syncing dependencies and paths with [Babashka](https://github.com/babashka/babashka)'s
   `bb.edn` files. One can create:

   - Create an alias in `deps.edn` with a `:maestro/root`, requiring other aliases
   - Run this task on this alias
   - Generated `deps.edn` file in `:maestro/root` will contain all necessary `:deps` and `:paths`
   - Hard links were created for all files found in `:paths`
   - `bb.edn` can use `:local/root` on this

   Hard links are created to allow consuming paths from anywhere in the repository.
   This is because Clojure CLI dislikes outsider paths (e.g. `../foo`). They are generated in
   `./maestro/uber` relative to the `:maestro/root`.

-----
