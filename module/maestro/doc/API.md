# Table of contents
-  [`protosens.maestro`](#protosens.maestro)  - See [README](./) about core principles.
    -  [`by-profile+`](#protosens.maestro/by-profile+) - Extracts a set of all required aliases selected by the given profiles.
    -  [`cli-arg+`](#protosens.maestro/cli-arg+) - Processes CLI arguments in a commonly needed way.
    -  [`create-basis`](#protosens.maestro/create-basis) - Reads and prepares a <code>deps.edn</code> file.
    -  [`ensure-basis`](#protosens.maestro/ensure-basis) - Reads the basis from disk if necessary and merge everything.
    -  [`fail`](#protosens.maestro/fail) - Fails with the given error <code>message</code>.
    -  [`fail-mode`](#protosens.maestro/fail-mode) - How failure is handled.
    -  [`not-by-profile+`](#protosens.maestro/not-by-profile+) - Extracts a set of all required aliases NOT selected by the given profiles.
    -  [`search`](#protosens.maestro/search) - Searches for all required aliases.
    -  [`stringify-required`](#protosens.maestro/stringify-required) - Stringifies concatenated aliases from <code>:maestro/require</code>.
    -  [`task`](#protosens.maestro/task) - Task searching and printing all required aliases.
-  [`protosens.maestro.aggr`](#protosens.maestro.aggr)  - Altering what is collected when searching for required aliases.
    -  [`alias`](#protosens.maestro.aggr/alias) - In <code>basis</code>, appends <code>alias</code> under <code>:maestro/require</code>.
    -  [`default`](#protosens.maestro.aggr/default) - Default alias aggregating function.
    -  [`env`](#protosens.maestro.aggr/env) - Merges <code>:maestro/env</code> from <code>alias-data</code> into <code>:maestro/env</code> in <code>basis</code>.
-  [`protosens.maestro.module.expose`](#protosens.maestro.module.expose)  - Exposing modules to be consumed publicly.
    -  [`deploy`](#protosens.maestro.module.expose/deploy) - Task exposing selected modules for consumption by Clojure CLI as Git dependencies.
    -  [`deploy-local`](#protosens.maestro.module.expose/deploy-local) - Local exposition for testing purporses.
    -  [`exposed?`](#protosens.maestro.module.expose/exposed?) - Returns true if an alias (given its data) is meant to be exposed as a Git library.
    -  [`requirer+`](#protosens.maestro.module.expose/requirer+) - Task generating requirer namespaces for all exposed modules.
    -  [`verify`](#protosens.maestro.module.expose/verify) - Task verifying exposed modules, checking if namespaces compile.
-  [`protosens.maestro.module.requirer`](#protosens.maestro.module.requirer)  - Generating "requirer" namespaces for modules.
    -  [`alias+`](#protosens.maestro.module.requirer/alias+) - Finds aliases to work with.
    -  [`generate`](#protosens.maestro.module.requirer/generate) - Task generating requirer namespaces for modules.
    -  [`verify`](#protosens.maestro.module.requirer/verify) - Task verifying modules by executing their requirer namespaces.
    -  [`verify-command`](#protosens.maestro.module.requirer/verify-command) - Creates a shell command for the verification process depending on the platform to test.
-  [`protosens.maestro.module.uber`](#protosens.maestro.module.uber)  - Special way of merging aliases in a generated <code>deps.edn</code> file.
    -  [`generate`](#protosens.maestro.module.uber/generate) - Generate a single <code>deps.edn</code> file by merging everything required by <code>alias</code>.
-  [`protosens.maestro.process`](#protosens.maestro.process)  - About running shell commands with computed required aliases.
    -  [`run`](#protosens.maestro.process/run) - Templates a shell command with required aliases and runs it.
    -  [`template-command`](#protosens.maestro.process/template-command) - Templates a command to run with required aliases.

-----
# <a name="protosens.maestro">protosens.maestro</a>


See [README](./) about core principles.
 
   [`search`](#protosens.maestro/search) is the star of this namespace and exemplifies the Maestro philosophy.
  
   To understand the notion of a "basis", see [`create-basis`](#protosens.maestro/create-basis) and [`ensure-basis`](#protosens.maestro/ensure-basis).




## <a name="protosens.maestro/by-profile+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L294-L305) `by-profile+`</a>
``` clojure

(by-profile+ basis profile+)
```


Extracts a set of all required aliases selected by the given profiles.

   Call only after [`search`](#protosens.maestro/search).

## <a name="protosens.maestro/cli-arg+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L345-L416) `cli-arg+`</a>
``` clojure

(cli-arg+ proto-basis)
(cli-arg+ proto-basis arg+)
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

## <a name="protosens.maestro/create-basis">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L94-L121) `create-basis`</a>
``` clojure

(create-basis)
(create-basis option+)
```


Reads and prepares a `deps.edn` file.
  
   The result is called a `basis` for consistency with other Clojure libraries.

   Options may be:

   | Key                | Value                                 | Default          |
   |--------------------|---------------------------------------|------------------|
   | `:maestro/project` | Alternative path to a `deps.edn` file | `"./deps.edn"` |

## <a name="protosens.maestro/ensure-basis">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L125-L141) `ensure-basis`</a>
``` clojure

(ensure-basis proto-basis)
```


Reads the basis from disk if necessary and merge everything.

   The term "proto-basis" denotes that it may already be a proper basis or that it may
   contain key-values to merge to basis to read from disk.

   It is commonly used by many Maestro-related utilities as a convenience for users.

   In practice, [`create-basis`](#protosens.maestro/create-basis) is called if `:aliases` are missing.

## <a name="protosens.maestro/fail">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L36-L56) `fail`</a>
``` clojure

(fail message)
```


Fails with the given error `message`.

   Plugin authors and such should use this function to guarantee consistent behavior.

   Re-aligns multiline strings.
  
   See [`fail-mode`](#protosens.maestro/fail-mode).

## <a name="protosens.maestro/fail-mode">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L60-L88) `fail-mode`</a>
``` clojure

(fail-mode)
(fail-mode mode)
```


How failure is handled.

   See [`fail`](#protosens.maestro/fail).
  
   There are 2 modes:

   - `:exit` is usually prefered on Babashka ; error message is printed and process exits with code 1
   - `:throw` might be preferred on the JVM ; an exception is thrown with the error message

   Sets behavior to the given `mode`.
   Without argument, returns the current one (default is `:exit`).

## <a name="protosens.maestro/not-by-profile+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L309-L325) `not-by-profile+`</a>
``` clojure

(not-by-profile+ basis profile+)
```


Extracts a set of all required aliases NOT selected by the given profiles.

   Opposite of [`by-profile+`](#protosens.maestro/by-profile+).

   Call only after [`search`](#protosens.maestro/search).

## <a name="protosens.maestro/search">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L223-L288) `search`</a>
``` clojure

(search proto-basis)
```


Searches for all required aliases.

   Namely, the full chain of aliases required by the aliases provided unde `:maestro/alias+` while activating
   profiles provided under `:maestro/profile+`.

   High-level steps are:

   - Pass `proto-basis` through [`ensure-basis`](#protosens.maestro/ensure-basis)
   - Apply `:maestro/mode` (if any)
   - Search for all required aliases, put vector result under `:maestro/require`
   - Under `:maestro/profile->alias+`, remember which profiles result in which aliases being selected
   - Execute hooks provided in `:maestro/on-require` of required aliase
   - Return the whole basis with everything

   Also see:

   - [`protosens.maestro.aggr`](#protosens.maestro.aggr) for expert users needing this function to do more
   - [`task`](#protosens.maestro/task) for doing a search conveniently as a task (perfect for Babashka)

## <a name="protosens.maestro/stringify-required">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L329-L339) `stringify-required`</a>
``` clojure

(stringify-required basis)
```


Stringifies concatenated aliases from `:maestro/require`.

   Just like Clojure CLI likes it.

   See [`search`](#protosens.maestro/search).

## <a name="protosens.maestro/task">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro.clj#L420-L446) `task`</a>
``` clojure

(task)
(task proto-basis)
```


Task searching and printing all required aliases.
  
   High level steps:
  
   - Handle CLI arguments with [`cli-arg+`](#protosens.maestro/cli-arg+)
   - Call [`search`](#protosens.maestro/search)
   - Print required aliases (or pass result to function under `:maestro.task/finalize` if present)

   Commonly used as a Babashka task. The output is especially useful in combination with Clojure CLI by
   leveraring shell substitution (e.g. `$()`) to insert aliases under `-M` and friends.

-----

-----
# <a name="protosens.maestro.aggr">protosens.maestro.aggr</a>


Altering what is collected when searching for required aliases.
  
   When running [`protosens.maestro/search`](#protosens.maestro/search), `basis` can contain an extra key `:maestro/aggr`
   pointing to a function such as `(fn [basis alias alias-data] basis-2)`.
  
   By default, this function is [`default`](#protosens.maestro.aggr/default). Technically, power users can provided an alternative implementation
   for additional features.




## <a name="protosens.maestro.aggr/alias">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/aggr.clj#L17-L33) `alias`</a>
``` clojure

(alias basis alias)
(alias basis alias _alias-data)
```


In `basis`, appends `alias` under `:maestro/require`.

## <a name="protosens.maestro.aggr/default">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/aggr.clj#L63-L88) `default`</a>
``` clojure

(default basis alias)
(default basis alias alias-data)
```


Default alias aggregating function.
  
   As used by [`protosens.maestro/search`](#protosens.maestro/search) unless overwritten by the user.

   Uses:

   - [`alias`](#protosens.maestro.aggr/alias)
   - [`env`](#protosens.maestro.aggr/env)

## <a name="protosens.maestro.aggr/env">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/aggr.clj#L37-L57) `env`</a>
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


Exposing modules to be consumed publicly.
 
   Modules containing a `:maestro.module.expose/name` in their alias data can be exposed publicly as
   [Git dependencies](https://clojure.org/guides/deps_and_cli#_using_git_libraries) and consumed from
   [Clojure CLI](https://clojure.org/guides/deps_and_cli). Naturally, users must rely on `:deps/root`
   to point to individual modules.

   Modules meant for exposition must have a `:maestro.module.expose/name`. A name is a symbol
   `<organization>/<artifact>` such as `com.acme/some-lib`.

   The [`deploy`](#protosens.maestro.module.expose/deploy) task does the necessary step for exposition.
   The [`verify`](#protosens.maestro.module.expose/verify) task may be used as a precaution.




## <a name="protosens.maestro.module.expose/deploy">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/module/expose.clj#L164-L240) `deploy`</a>
``` clojure

(deploy)
(deploy proto-basis)
```


Task exposing selected modules for consumption by Clojure CLI as Git dependencies.

   High-level steps are:

   - Ensure Git tree is absolutely clean
   - Select modules with a `:maestro.module.expose/name` in their alias data
   - In their `:maestro/root`, generate a `deps.edn` file
   - Dependencies on other modules are Git dependencies with the SHA of the previous commit
   - Commit
   - Repeat once

   This produces 2 commits and the SHA of the last commit is what users can rely on when pushed.
   
   Either `proto-basis` or the top `deps.edn` file must contain `:maestro.module.expose/url` pointing
   to the URL of the repo.

   For testing purposes, one can point to the absolute path of the repository. For production
   purposes, always use the public URL of the repository.
  
   **Note**: the `release` profile is activated automatically when resolving `:maestro/require` for each
   module.

## <a name="protosens.maestro.module.expose/deploy-local">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/module/expose.clj#L244-L264) `deploy-local`</a>
``` clojure

(deploy-local)
(deploy-local basis)
```


Local exposition for testing purporses.

   Exactly like [`deploy`](#protosens.maestro.module.expose/deploy) but sets the repository URL to the current directory.

   Which must be the root directory of the repository.

   For instance, it allows testing exposition and running the [`verify`](#protosens.maestro.module.expose/verify) task without having to push anything.

## <a name="protosens.maestro.module.expose/exposed?">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/module/expose.clj#L268-L282) `exposed?`</a>
``` clojure

(exposed? alias-data)
(exposed? basis alias)
```


Returns true if an alias (given its data) is meant to be exposed as a Git library.

## <a name="protosens.maestro.module.expose/requirer+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/module/expose.clj#L308-L327) `requirer+`</a>
``` clojure

(requirer+)
(requirer+ basis)
```


Task generating requirer namespaces for all exposed modules.
  
   See the [`protosens.maestro.module.requirer`](#protosens.maestro.module.requirer) about requirer namespaces and
   especially [`protosens.maestro.module.requirer/generate`](#protosens.maestro.module.requirer/generate) about the required
   setup.

   The main benefit about generating those is being able to call the [`verify`](#protosens.maestro.module.expose/verify)
   task.

## <a name="protosens.maestro.module.expose/verify">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/module/expose.clj#L333-L350) `verify`</a>
``` clojure

(verify)
(verify basis)
```


Task verifying exposed modules, checking if namespaces compile.
 
   This is done via [`protosens.maestro.module.requirer/verify`](#protosens.maestro.module.requirer/verify) and ensure that
   modules can be required in their production state.

   See [`requirer+`](#protosens.maestro.module.expose/requirer+).

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




## <a name="protosens.maestro.module.requirer/alias+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/module/requirer.clj#L40-L79) `alias+`</a>
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

## <a name="protosens.maestro.module.requirer/generate">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/module/requirer.clj#L85-L157) `generate`</a>
``` clojure

(generate)
(generate proto-basis)
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

## <a name="protosens.maestro.module.requirer/verify">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/module/requirer.clj#L163-L227) `verify`</a>
``` clojure

(verify)
(verify proto-basis)
```


Task verifying modules by executing their requirer namespaces.

   Assumes:
  
   - [`generate`](#protosens.maestro.module.requirer/generate) has been run first
   - Modules have the relevant data described in [`generate`](#protosens.maestro.module.requirer/generate)
   - Modules to verify has their own `deps.edn` under their `:maestro/root`

   Execution happens on all platforms indicated in alias data under `:maestro/platform+`.
   Defaults to `[:jvm]`. See [`verify-command`](#protosens.maestro.module.requirer/verify-command).

## <a name="protosens.maestro.module.requirer/verify-command">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/module/requirer.clj#L232-L247) `verify-command`</a>

Creates a shell command for the verification process depending on the platform to test.
  
   Used by [`verify`](#protosens.maestro.module.requirer/verify).

   The shell command is vector starting with the actuall shell command and the rest
   are individual arguments.

   Currently supported platforms:

   - `:bb`  (Babashka)
   - `:jvm` (Clojure JVM)

-----
# <a name="protosens.maestro.module.uber">protosens.maestro.module.uber</a>


Special way of merging aliases in a generated `deps.edn` file.




## <a name="protosens.maestro.module.uber/generate">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/module/uber.clj#L93-L165) `generate`</a>
``` clojure

(generate alias)
(generate alias proto-basis)
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




## <a name="protosens.maestro.process/run">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro/src/main/clj/protosens/maestro/process.clj#L49-L98) `run`</a>
``` clojure

(run)
(run proto-basis)
```


Templates a shell command with required aliases and runs it.

   Command-line arguments are split in two at `--`. Everything before is fed
   to [`protosens.maestro/task`](#protosens.maestro/task) to compute required aliases. Everything after
   is a command to template (see [`template-command`](#protosens.maestro.process/template-command)).

   `proto-basis` may contain a `:maestro.process/command` that will be prepended before
   templating.

   Extra environment variable maps provided in `:maestro/env` of required aliases,
   if any, are merged and set when executing the command.

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
