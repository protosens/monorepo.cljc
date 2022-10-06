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
-  [`protosens.maestro.module.expose`](#protosens.maestro.module.expose)  - Modules containing a <code>:maestro.module.expose/name</code> in their alias data can be exposed publicly as git libraries and consumed from Clojure CLI (using <code>:deps/root</code> to point to the <code>:maestro/root</code> of the consumed module).
    -  [`-prepare-deps-edn`](#protosens.maestro.module.expose/-prepare-deps-edn)
    -  [`-write-deps-edn`](#protosens.maestro.module.expose/-write-deps-edn)
    -  [`deploy`](#protosens.maestro.module.expose/deploy) - Exposes selected modules allowing them to be consumed by Clojure CLI as Git dependencies.
    -  [`exposed?`](#protosens.maestro.module.expose/exposed?) - Returns true if an alias (given its data) is meant to be exposed as a Git library.
    -  [`verify`](#protosens.maestro.module.expose/verify) - Verifies exposed modules with [[protosens.maestro.module.requirer/verify]].
-  [`protosens.maestro.module.requirer`](#protosens.maestro.module.requirer)  - Generating "requirer" namespaces for modules.
    -  [`alias+`](#protosens.maestro.module.requirer/alias+) - Finds aliases to work with.
    -  [`generate`](#protosens.maestro.module.requirer/generate) - Task generating requirer namespaces for modules.
    -  [`verify`](#protosens.maestro.module.requirer/verify) - Task verifying modules by executing their requirer namespaces.
    -  [`verify-command`](#protosens.maestro.module.requirer/verify-command) - Used by [[verify]] to create a shell command depending on the platform to verify.
-  [`protosens.maestro.module.uber`](#protosens.maestro.module.uber)  - Special way of merging aliases in a generated <code>deps.edn</code> file.
    -  [`task`](#protosens.maestro.module.uber/task) - Generate a single <code>deps.edn</code> file by merging everything required by <code>alias</code>.
-  [`protosens.maestro.process`](#protosens.maestro.process)  - About running shell commands with computed required aliases.
    -  [`run`](#protosens.maestro.process/run) - Templates a shell command with required aliases and runs it.
    -  [`template-command`](#protosens.maestro.process/template-command) - Templates a command to run with required aliases.

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
# <a name="protosens.maestro.module.expose">protosens.maestro.module.expose</a>


Modules containing a `:maestro.module.expose/name` in their alias data can be exposed publicly as
   git libraries and consumed from Clojure CLI (using `:deps/root` to point to the `:maestro/root`
   of the consumed module).

   A name is a symbol `<organization>/<artifact>` such as `com.acme/some-lib`.

   The [[expose]] task does the necessary step for exposition.
   The [`verify`](#protosens.maestro.module.expose/verify) task may be used as a precaution.




## <a name="protosens.maestro.module.expose/-prepare-deps-edn">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/module/expose.clj#L31-L94) `-prepare-deps-edn`</a>
``` clojure

(-prepare-deps-edn basis git-sha alias)
```


## <a name="protosens.maestro.module.expose/-write-deps-edn">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/module/expose.clj#L98-L115) `-write-deps-edn`</a>
``` clojure

(-write-deps-edn path deps-edn)
```


## <a name="protosens.maestro.module.expose/deploy">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/module/expose.clj#L160-L236) `deploy`</a>
``` clojure

(deploy)
(deploy basis)
```


Exposes selected modules allowing them to be consumed by Clojure CLI as Git dependencies.

   High-level steps are:

   - Ensure Git tree is absolutely clean
   - Select modules with a `:maestro.module.expose/name` in their alias data
   - In their `:maestro/root`, generate a `deps.edn` file
   - Dependencies on other modules are Git dependencies with the SHA of the previous commit
   - Commit
   - Repeat once

   This produces 2 commits and the SHA of the last commit is what users can rely on when pushed.
   
   Either `basis` or the top `deps.edn` file must contain `:maestro.module.expose/url` pointing
   to the URL of the repo.

   For testing purposes, one can point to the absolute path of the repository. For production
   purposes, always use the public URL of the repository.
  
   **Note**: the `release` profile is activated automatically when resolving `:maestro/require` for each
   module.

## <a name="protosens.maestro.module.expose/exposed?">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/module/expose.clj#L240-L254) `exposed?`</a>
``` clojure

(exposed? alias-data)
(exposed? basis alias)
```


Returns true if an alias (given its data) is meant to be exposed as a Git library.

## <a name="protosens.maestro.module.expose/verify">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/module/expose.clj#L260-L285) `verify`</a>
``` clojure

(verify)
(verify basis)
```


Verifies exposed modules with [`protosens.maestro.module.requirer/verify`](#protosens.maestro.module.requirer/verify).

   This ensures that exposed modules can be required in their production state.
   See [`protosens.maestro.module.requirer/generate`](#protosens.maestro.module.requirer/generate) about setup.

-----
# <a name="protosens.maestro.module.requirer">protosens.maestro.module.requirer</a>


Generating "requirer" namespaces for modules.
  
   A module requirer namespace is a namespace that requires all other namespaces provided
   by a module.

   Concretely, those namespaces are found by collecting all `:extra-paths` for an alias and
   the aliases it requires, filtering out those that do not start with `:maestro/root`.

   Several use cases exists for this. For instance, an application might systematically require
   such a requirer namespace to ensure that everything is executed (e.g. namespaces with
   `defmethods`).

   See [`generate`](#protosens.maestro.module.requirer/generate) about generating requirers.

   In some situtation, a module has its own `deps.edn` (see [[protosens.maestro.git.lib]]).
   It can then be verified by executing its requirer namespace. It ensures that the whole
   module can be required in its production state, without any test dependencies and such.
   Also, verification can happen on several platforms (e.g. Clojure CLI + Babashka).

   See [`verify`](#protosens.maestro.module.requirer/verify).




## <a name="protosens.maestro.module.requirer/alias+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/module/requirer.clj#L39-L78) `alias+`</a>
``` clojure

(alias+ basis)
```


Finds aliases to work with.
  
   These steps are tried successively until one succeeds:

   - Fetch collection under `:maestro.module.requirer/alias+`
   - Reads first CLI arg (a single alias or a vector of aliases)
   - Get all existing aliases in `basis`
  
   `basis` may contain `:maestro.module.requirer/alias-filter`, a `(fn [alias data])`
   deciding whether the alias is selected.

## <a name="protosens.maestro.module.requirer/generate">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/module/requirer.clj#L84-L156) `generate`</a>
``` clojure

(generate)
(generate basis)
```


Task generating requirer namespaces for modules.

   For this to work, a module must have the following in its alias data:

   | Key                                  | Value                                 | Mandatory? |
   |--------------------------------------|---------------------------------------|------------|
   | `:maestro.module.requirer/namespace` | Symbol for the requirer namespace     | Yes        |
   | `:maestro.module.requirer/path`      | Directory where the file is generated | Yes        |
   | `:maestro.module.requirer/exclude+   | Namespaces that must not be required  | No         |

   The CLJC file will be generated in the given respecting the directory structure of
   Clojure namespaces.

   Prints feedback about what is being generated and which namespaces are being required.
 
   See [`alias+`](#protosens.maestro.module.requirer/alias+) about selecting aliases.

## <a name="protosens.maestro.module.requirer/verify">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/module/requirer.clj#L162-L226) `verify`</a>
``` clojure

(verify)
(verify basis)
```


Task verifying modules by executing their requirer namespaces.

   Assumes:
  
   - [`generate`](#protosens.maestro.module.requirer/generate) has been run first
   - Modules have the relevant data described in [`generate`](#protosens.maestro.module.requirer/generate)
   - Modules to verify has their own `deps.edn` under their `:maestro/root`

   Execution happens on all platforms indicated in alias data under `:maestro/platform+`.
   Defaults to `[:jvm]`. See [`verify-command`](#protosens.maestro.module.requirer/verify-command).

## <a name="protosens.maestro.module.requirer/verify-command">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/module/requirer.clj#L231-L244) `verify-command`</a>

Used by [`verify`](#protosens.maestro.module.requirer/verify) to create a shell command depending on the platform to verify.

   The shell command is vector starting with the actuall shell command and the rest
   are individual arguments.

   Currently supported platforms:

   - `:bb`  (Babashka)
   - `:jvm` (Clojure JVM)

-----
# <a name="protosens.maestro.module.uber">protosens.maestro.module.uber</a>


Special way of merging aliases in a generated `deps.edn` file.




## <a name="protosens.maestro.module.uber/task">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/module/uber.clj#L93-L165) `task`</a>
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
