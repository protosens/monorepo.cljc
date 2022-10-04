# Table of contents
-  [`protosens.maestro`](#protosens.maestro)  - See README about core principles, [[search]] being the star of this namespace.
    -  [`by-profile+`](#protosens.maestro/by-profile+) - Extracts a set of all aliases required in the context of the given collection of profiles.
    -  [`cli-arg+`](#protosens.maestro/cli-arg+) - Processes CLI arguments in a commonly needed way.
    -  [`create-basis`](#protosens.maestro/create-basis) - Reads and prepares a <code>deps.edn</code> file.
    -  [`ensure-basis`](#protosens.maestro/ensure-basis) - Returns the given argument if it contains <code>:aliases</code>.
    -  [`extra-path+`](#protosens.maestro/extra-path+) - Extracts a list of all paths provided in <code>:extra-paths</code> for the given aliases.
    -  [`fail`](#protosens.maestro/fail) - Fails with the given error <code>message</code>.
    -  [`fail-mode`](#protosens.maestro/fail-mode) - How [[fail]] behaves.
    -  [`not-by-profile+`](#protosens.maestro/not-by-profile+) - Extracts a set of all aliases NOT required in the context of the given collection of profiles.
    -  [`search`](#protosens.maestro/search) - Given input aliases and profiles, under <code>:maestro/alias+</code> and <code>:maestro/profile+</code> respectively, searches for all necessary aliases and puts the results in a vector under <code>:maestro/require</code>.
    -  [`stringify-required`](#protosens.maestro/stringify-required) - Stringifies concatenated aliases from <code>:maestro/require</code>.
    -  [`task`](#protosens.maestro/task) - Like [[search]] but prepends aliases and profiles found using [[cli-arg]] and ends by printing all required aliases.
-  [`protosens.maestro.aggr`](#protosens.maestro.aggr)  - When running [[protosens.maestro/search]], <code>basis</code> can contain an extra key <code>:maestro/aggr</code> pointing to a function such as <code>(fn [basis alias alias-data] basis-2)</code>.
    -  [`alias`](#protosens.maestro.aggr/alias) - In <code>basis</code>, appends <code>alias</code> under <code>:maestro/require</code>.
    -  [`default`](#protosens.maestro.aggr/default) - Default alias aggregating function for [[protosens.maestro/search]].
    -  [`env`](#protosens.maestro.aggr/env) - Merges <code>:maestro/env</code> from <code>alias-data</code> into <code>:maestro/env</code> in <code>basis</code>.
-  [`protosens.maestro.git.lib`](#protosens.maestro.git.lib)  - Aliases that contains a name under <code>:maestro.git.lib/name</code> can be exposed publicly as git libraries and consumed from Clojure CLI via <code>:deps/root</code>.
    -  [`expose`](#protosens.maestro.git.lib/expose) - Generates custom <code>deps.edn</code> files for all aliases having in there data a name (see namespace description) as well as a <code>:maestro/root</code> (path to the root directory of that alias).
    -  [`gitlib?`](#protosens.maestro.git.lib/gitlib?) - Returns true if an alias (given its data) is meant to be exposed as a git library.
    -  [`prepare-deps-edn`](#protosens.maestro.git.lib/prepare-deps-edn) - Computes the content of the <code>deps.edn</code> file for the given <code>alias</code> meant to be exposed as a git library.
    -  [`task`](#protosens.maestro.git.lib/task) - Task reliably exposing modules as Git libraries to consume externally.
    -  [`write-deps-edn`](#protosens.maestro.git.lib/write-deps-edn) - Default way of writing a <code>deps-edn</code> file by pretty-printing it to the given <code>path</code>.
-  [`protosens.maestro.process`](#protosens.maestro.process)  - About running shell commands with computed required aliases.
    -  [`run`](#protosens.maestro.process/run) - Templates a shell command with required aliases and runs it.
    -  [`template-command`](#protosens.maestro.process/template-command) - Templates a command to run with required aliases.
-  [`protosens.maestro.uber`](#protosens.maestro.uber)  - Special way of merging aliases in a generated <code>deps.edn</code> file.
    -  [`task`](#protosens.maestro.uber/task) - Generate a single <code>deps.edn</code> file by merging everything required by <code>alias</code>.

-----
# <a name="protosens.maestro">protosens.maestro</a>


See README about core principles, [`search`](#protosens.maestro/search) being the star of this namespace.




## <a name="protosens.maestro/by-profile+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L276-L287) `by-profile+`</a>
``` clojure

(by-profile+ basis profile+)
```


Extracts a set of all aliases required in the context of the given collection of profiles.

   See [`search`](#protosens.maestro/search).

## <a name="protosens.maestro/cli-arg+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L350-L421) `cli-arg+`</a>
``` clojure

(cli-arg+ basis)
(cli-arg+ basis arg+)
```


Processes CLI arguments in a commonly needed way.

   [`task`](#protosens.maestro/task) is one example of a function that requires CLI arguments to be processed like so.

   Aliases and profiles are sorted and prepended to `:maestro/alias+` and `:maestro/profile+` respectively.
   
   The last argument can be an alias or a profile:

   ```
   :some-alias

   some-profile
   ```

   Or a vector combining any number of them:

   ```
   '[profile-foo alias-a alias-b profile-bar]'
   ```

   If there are 2 arguments, the first one is interpreted as a `:maestro/mode` (see [`search`](#protosens.maestro/search)):

   ```
   :some-mode '[profile-foo alias-a alias-b profile-bar]'
   ```
  
   `arg+` defaults to `*command-line-args*`.

## <a name="protosens.maestro/create-basis">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L88-L113) `create-basis`</a>
``` clojure

(create-basis)
(create-basis option+)
```


Reads and prepares a `deps.edn` file.

   Takes a nilable map of options such as:

   | Key                | Value                                 | Default          |
   |--------------------|---------------------------------------|------------------|
   | `:maestro/project` | Alternative path to a `deps.edn` file | `"./deps.edn"` |

## <a name="protosens.maestro/ensure-basis">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L117-L127) `ensure-basis`</a>
``` clojure

(ensure-basis maybe-basis)
```


Returns the given argument if it contains `:aliases`.
   Otherwise, forwards it to [`create-basis`](#protosens.maestro/create-basis).

## <a name="protosens.maestro/extra-path+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L291-L310) `extra-path+`</a>
``` clojure

(extra-path+ basis)
(extra-path+ basis alias+)
```


Extracts a list of all paths provided in `:extra-paths` for the given aliases.

## <a name="protosens.maestro/fail">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L32-L52) `fail`</a>
``` clojure

(fail message)
```


Fails with the given error `message`.

   Plugin authors and such should use this function to guarantee consistent behavior.

   Re-aligns multiline strings.
  
   See [`fail-mode`](#protosens.maestro/fail-mode).

## <a name="protosens.maestro/fail-mode">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L56-L82) `fail-mode`</a>
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

## <a name="protosens.maestro/not-by-profile+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L314-L330) `not-by-profile+`</a>
``` clojure

(not-by-profile+ basis profile+)
```


Extracts a set of all aliases NOT required in the context of the given collection of profiles.

   Opposite of [`by-profile+`](#protosens.maestro/by-profile+).

   See [`search`](#protosens.maestro/search).

## <a name="protosens.maestro/search">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L205-L270) `search`</a>
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
   - [[protosens.maestro.alias]]

## <a name="protosens.maestro/stringify-required">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L334-L344) `stringify-required`</a>
``` clojure

(stringify-required basis)
```


Stringifies concatenated aliases from `:maestro/require`.

   Just like Clojure CLI likes it.

   See [`search`](#protosens.maestro/search).

## <a name="protosens.maestro/task">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L425-L445) `task`</a>
``` clojure

(task)
(task basis)
```


Like [`search`](#protosens.maestro/search) but prepends aliases and profiles found using [[cli-arg]] and ends by printing all required aliases.

   Commonly used as a Babashka task. The output is especially useful in combination with Clojure CLI by leveraring shell
   substitution (e.g. `$()`) to insert aliases under `-M` and friends.

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
# <a name="protosens.maestro.git.lib">protosens.maestro.git.lib</a>


Aliases that contains a name under `:maestro.git.lib/name` can be exposed publicly as
   git libraries and consumed from Clojure CLI via `:deps/root`.

   A name is a symbol `<organization>/<artifact>` such as `com.acme/some-lib`.

   In order to do so, each such module must have its own `deps.edn` file.
   See [`expose`](#protosens.maestro.git.lib/expose) and [`task`](#protosens.maestro.git.lib/task).




## <a name="protosens.maestro.git.lib/expose">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/git/lib.clj#L142-L191) `expose`</a>
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

## <a name="protosens.maestro.git.lib/gitlib?">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/git/lib.clj#L26-L32) `gitlib?`</a>
``` clojure

(gitlib? alias-data)
```


Returns true if an alias (given its data) is meant to be exposed as a git library.

## <a name="protosens.maestro.git.lib/prepare-deps-edn">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/git/lib.clj#L36-L117) `prepare-deps-edn`</a>
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

## <a name="protosens.maestro.git.lib/task">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/git/lib.clj#L195-L265) `task`</a>
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

## <a name="protosens.maestro.git.lib/write-deps-edn">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/git/lib.clj#L121-L136) `write-deps-edn`</a>
``` clojure

(write-deps-edn path deps-edn)
```


Default way of writing a `deps-edn` file by pretty-printing it to the given `path`.

-----
# <a name="protosens.maestro.process">protosens.maestro.process</a>


About running shell commands with computed required aliases.
  
   [`protosens.maestro/task`](#protosens.maestro/task) prints required aliases and this is often
   useful in combination with Clojure CLI, by leveraging shell substitution
   like `$()`.

   However, some shells do not understand substitutions or are confused by it
   in some environments. The [`run`](#protosens.maestro.process/run) task from this namespace can be used to
   template shell commands with required aliases and running them.




## <a name="protosens.maestro.process/run">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/process.clj#L49-L91) `run`</a>
``` clojure

(run)
(run basis)
```


Templates a shell command with required aliases and runs it.

   Command-line arguments are split in two at `--`. Everything before is fed
   to [`protosens.maestro/task`](#protosens.maestro/task) to compute required aliases. Everything after
   is a command to template (see [`template-command`](#protosens.maestro.process/template-command)).

   `basis` may contain a `:maestro.process/command` that will be prepended before
   templating.

   Eventually, the command is run and this function returns `true` if the process
   exits with a non-zero status.

## <a name="protosens.maestro.process/template-command">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/process.clj#L21-L45) `template-command`</a>
``` clojure

(template-command basis)
```


Templates a command to run with required aliases.

   Assumes required aliases have been computed and are present under `:maestro/require`.

   The command to template is a vector of arguments starting by the shell program to run
   located under `:maestro.process/command`.

   The default pattern to replace is `__`. An alternative one may be provided under
   `:maestro.process/pattern` (but cannot be `--`).

-----
# <a name="protosens.maestro.uber">protosens.maestro.uber</a>


Special way of merging aliases in a generated `deps.edn` file.




## <a name="protosens.maestro.uber/task">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/uber.clj#L93-L165) `task`</a>
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
