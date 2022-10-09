This collection of principles started as a tool for expressing dependencies
between [`deps.edn`] aliases. This simple idea kept evolving and
eventually led to a somewhat new paradigm in repository organization providing
extreme flexbility.

Main benefits are:

- One single `deps.edn` where the whole repository is clearly described
- Structure around aliases combined at will, centered on the notion of "modules"
- Trivial to expose modules as Git dependencies
- Dependency pinning, no duplication
- Growing collection of plugins for integrating with other tools ([Clj-kondo], [Kaocha], ...)
- Easily automated with [Babashka]
- Scalable


---


## Overarching idea

Let us start by specifying dependencies between aliases:

```clojure
;; `deps.edn`
;;
{:aliases {:foo          {:extra-paths     ["..."]
                          :maestro/require [:some-library]}
           :some-library {:extra-deps      {...}}}}
```

Let us add a function for computing all required aliases given whatever is
needed:

```clojure
(require '[protosens.maestro :as maestro])

(-> (maestro/search {:maestro/alias+ [:foo]})
    :maestro/require)

;;  [:some-library :foo]
```

Let us add the idea of profiles:

```clojure
;; `deps.edn`
;;
{:aliases {:only-dev     {:extra-paths ["..."]}
           :only-prod    {:extra-paths ["..."]}
           :foo          {:extra-paths     ["..."]
                          :maestro/require [:some-library
                                            {dev  :only-dev
                                             prod :only-prod}]}
           :some-library {:extra-deps      {...}}}}
```

So that some aliases are required selectively based on activated profiles:

```clojure
(-> (maestro/search {:maestro/alias+   [:foo]
                     :maestro/profile+ ['dev]})
    :maestro/require)

;; [:only-dev :some-library :foo]
```

We have reached a way for writing aliases that are well-scoped, granular,
easily combined, for any type of environment (dev, testing, production, etc).
Computing required aliases is recursive.

The remainder is a little [Babashka] task
that can be added to [`bb.edn`]:

```clojure
{:tasks {aliases {:doc      "Print required aliases"
                  :requires ([protosens.maestro])
                  :task     (protosens.maestro/task)}}}
```

Everything is at our fingertips:

```
$ bb aliases :foo

:some-library:foo

$ bb aliases '[prod :foo]'

:only-prod:some-library:foo
```

And can be combined with Clojure CLI with a little shell substitution (aka
`$()`):

```
$ clj -M$( bb aliases '[dev :foo]' )
```


---


## Core concepts

The ideas exposed above can lead to an impressive number of ways of organizing
repositories efficiently. The concepts exposed below are strong recommendations
rather than a strict framework. They have been proven to work very well in a
several projects and current Maestro-related tools, like plugins, play
nicely along.


### Alias

Aliases are the main building blocks and should be written as reusable units.

As a starter, we suggest using one alias for each external dependency. By
convention, our [`deps.edn`] namespace those under `:ext/...`.
This allows dependency pinning. Any other alias that require such an external
dependency can now `:maestro/require` its alias. This removes duplication and
enforces the same dependency version everywhere.

We promote adding to alias data any information that might be relevant. For
instance, any newcomer can understand right away what any external dependency is
(`:maestro/doc`) and where to find more information about it (`:maestro/url`).

Following the idea, aliases can represent tools to invoke, dev utilities to
bring, test namespaces to provide, anything that feels like a well-scoped unit.


### Profiles

Often, the same alias is used under different circumstances, or environments. An
archetypical example is the distinction between development, testing, and
production:

```clojure
;; `deps.edn`
;;
{:aliases {:dev  {:extra-paths ["..."]}
           :foo  {:extra-paths     ["..."]
                  :maestro/require [:ext/bar
                                    {dev     :dev
                                     test    :test
                                     release :prod}]}
           :prod {:extra-paths ["..."]}
           :test {:extra-paths ["..."]}
           ...}}
```

Hence, profiles (symbols) act a selection (map of `profile` -> `alias` in
`:maestro/require`) when activated. If no active profile is found, nothing is
required in such a map.

Now suppose the following search where all those profiles are activated:

```
$ bb aliases '[dev test release :foo]'

:dev:ext/bar:foo
```

The `dev` profile is activated first hence `:dev` is selected preferentially.
However, we could have written things like so:

```clojure
;; `deps.edn`, zoom on
:maestro/require [:ext/bar
                  {dev :dev}
                  {test :test}
                  {release :prod}]
```

Now, it is possible turning on and off each and every environment for `:foo`:

```
$ bb aliases '[dev test release :foo]`

:prod:test:dev:ext/bar:foo
```

Lastly, additional metadata on profiles can impact how they are activated. The
only supported key-value currently is `:direct?`. For instance, the following
activates the `test` profile only when considering `:maestro/require` for
`:foo`. It is then deactivated when recursively searching for other required
aliases. 

```
$ bb aliases '[^:direct? test :foo]'
```


### Module

Modules are a purely theoretical construct. The setup example exposed in the
previous section about profiles looks like a module. There is a core alias
(`:foo`), it has a supporting alias for development (`:dev`), another supporting
alias that probably provides paths to test namespaces (`:test`). Hence, `:foo`
feels very much like a unit, a module indeed.

The precise definition of a module remains fuzzy as to remain flexible but in
practice, current Maestro-related tools often expect a module to be represented
by an alias with a `:maestro/root`. A root is a directory where everything
needed for a module is kept (source, documentation, etc). The path to this very
directory is the `:maestro/root` of `:module/maestro`.

Qualified keywords for aliases should be used to better communicate intent. Once
again, different organizations are possible. However, we suggest using the
namespace part for communicating categories. For instance, our [`deps.edn`]
defines `:module/maestro` as the core alias for this very module while
`:test/maesto` is a supporting alias providing test namespaces when the `test`
profile is activated. There is even a `:test.release/maestro` allowing us to run
tests against Maestro exactly how it is exposed publicly.


### Mode

Modes are merely a convenience for users. Some combinations of aliases and
profiles are always needed for some workflows, depending on how a project
structures its repository. Such combinations, or "modes", can be provided in
[`deps.edn`].

For instance, this defines a `:dev` mode and a `:test` mode providing some
aliases and profiles that are always needed:

```clojure
;; `deps.edn`
;;
:maestro/mode+ {:dev  {:maestro/alias+   [:task/dev]
                       :maestro/profile+ [dev
                                          test]}

                :test {:maestro/alias+   [:task/test]
                       :maestro/profile+ [^:direct? test]}}
```

So that a user can easily launch, say, "dev mode" for `:foo` without having to
remember what else is needed:

```
$ bb aliases :dev :foo

:test:dev:foo:task/dev
```

This remain flexible. For instance, a user can work on `:foo` and `:bar` at the
same time with a personal NREPL alias from its own `~/.clojure/deps.edn`:

```
$ clj -M$( bb aliases :dev '[:foo :bar :me/nrepl] )'
```


---


## Expose modules as Git dependencies

Especially in monorepos, deploying libraries is often a recursive nightmare.
Maestro offers a simple solution called "exposition". In short, a custom
`deps.edn` file is generated for each publicly exposed module. Dependencies to
other modules are expressed as [Git dependencies], all sharing the same SHA.

In your [`deps.edn`], indicate the URL of your repository:

```clojure
:maestro.module.expose/url "https://github.com/protosens/monorepo.cljc"
```

In each exposed module (or more precisely, its alias), add an artifact name:

```clojure
{:aliases {:module/maestro {:maestro/root               "module/root"
                            :maestro.module.expose/name protosens/maestro
                            ...}}}
```

In your [`bb.edn`], use the `deploy` task:

```clojure
{:tasks {expose {:requires ([protosens.maestro.module.expose])
                 :task     (protosens.maestro.module.expose/deploy)}}}^
```

When the repository is clean and stable:

```
$ bb expose
```

This will generate those custom `deps.edn` files in the `:maestro/root` of
exposed modules and create 2 commits. The SHA of the last commit is what users
can rely on. They can import any number of modules as [Git dependencies] using
`:deps/root` as long as they always use the same SHA. Everything will be
beautifully in sync.

This promotes the idea of atomic repositories. All modules always strive to work
together and avoid breaking changes.

As such, we recommend using [calver] at the level of the repository, tagging
`stable/...` releases. A main changelog can merely point to which modules where
impacted between stable expositions while each impacted module can keep its own
changelog in its `:maestro/root` containing details.


---


## Sync `deps.edn` with Babashka

The beauty of a monorepo, even for small projects, is that it provides a strong
incentive for automation and good practices. No need to replicate and maintain
setups across a gazillion repositories. Invest everything in the perfect setup
once and maintain only that.

[Babashka] is the perfect scripting tool for a Clojure repository. However,
tasks may need their own paths and dependencies. Striving for perfection, in the
best of worlds, everything would be kept in [`deps.edn`]. Especially if those
tasks need local modules.

For this purpose, we have developped the notion of an **uber module**. Meant for
local use, an uber module has its own autogenerated `deps.edn` where paths and
dependencies are deduced from its `:maestro/require`.

For instance, our `:module/task` does exactly that. Every time this command is
run:

```
$ bb genesis
```

A `deps.edn` file is generated [in its `:maestro/root`](../task/deps.edn) with
all necessary `:deps`, `:paths` pointing to hard links of all local paths so
that this scheme works from everywhere in the repository. Indeed, [Clojure-CLI]
dislikes "outsider" paths like `../some/path`. Your `.gitignore` should ignore
everything under `**/maestro/uber`.

Now, all tasks in [`bb.edn`] are usable since it specifies a `:local/root`
dependency to `"module/task"`.

```clojure
;; `bb.edn`
;;
:tasks {genesis {:requires ([protosens.maestro.module.uber])
                 :task     (protosens.maestro.module.uber/task :module/task)}}
```


---


## Plugins

In the [list of publicly available modules](../), `maestro.plugin...` modules
provide easy integration with other common tools. For instance,
[`maestro.plugin.kaocha`](../maestro.plugin.kaocha) is able to output source
paths and tests paths for any combination of aliases on the spot, meaning no one
will ever need to maintain complex `tests.edn` files with a gazillion test
suites.


---


## Roadmap

Maestro and related tools are already quite feature-complete and are expected to
work out of the box for a variety of setups. Since the concept exposed in this
README are flexible, we encourage contributions for extending capabilities even
more and providing solid integration to other tools via plugins.

The last remaining step to solve for offering a complete solution is continuous
integration. For instance, providing a way for running tests only for modules
that effectively changed. In theory, everything is there already since all
dependencies are clearly stated. In practice, there are a few gotchas to solve
for making it foolproof.


--- 


## Final notes

To really get a hang of it, one could study our [`deps.edn`], have a look at our
[`bb.edn`], fork this repository, and simply try out all those ideas.

Initialize the repository:

    $ bb genesis

List available tasks:

    $ bb tasks

And start with trying out `bb aliases`.




<!--- Links -->


[`bb.edn`]:         ../../bb.edn
[Babashka]:         https://github.com/babashka/babashka
[calver]:           https://calver.org
[Clojure-CLI]:      https://clojure.org/guides/deps_and_cli
[`deps.edn`]:       ../../deps.edn
[Clj-kondo]:        https://github.com/clj-kondo/clj-kondo
[Git dependencies]: https://clojure.org/guides/deps_and_cli#_using_git_libraries
[Kaocha]:           https://github.com/lambdaisland/kaocha
