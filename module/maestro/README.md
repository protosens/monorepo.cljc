# `:module/maestro`

[![Clojars](https://img.shields.io/clojars/v/com.protosens/maestro.svg)](https://clojars.org/com.protosens/maestro)
[![Cljdoc](https://cljdoc.org/badge/com.protosens/maestro)](https://cljdoc.org/d/com.protosens/maestro/CURRENT)

Maestro, calmely orchestrating your Clojure repositories.

This collection of principles and tools started as a simple way of managing
monorepos of Clojure modules. It kept evolving and became convenient even for
single repositories.

Main benefits are:

- One single `deps.edn` where the whole repository is clearly described
- Organized around aliases combined at will
- Easy dependency pinning
- Freedom, no strict framework
- Plugins for common tooling ([clj-kondo](https://github.com/clj-kondo/clj-kondo), [kaocha](https://github.com/lambdaisland/kaocha), ...)
- [Babashka](https://github.com/babashka/babashka)-friendly for efficient scripting


## Core concepts

Maestro was first designed as a tool for expressing dependencies between aliases using
`:maestro/require` in alias data of a [deps.edn](../../deps.edn) file:

```clojure
{:aliases {:alias-1 {:extra-paths     ["..."]
                     :maestro/require [:alias-2
                                       {dev  :alias-3
                                        prod :alias-4}]}
           :alias-2 {:extra-paths     ["..."]
                     :maestro/require [:alias-5]}
           :alias-3 {:extra-paths ["..."]}
           :alias-4 {:extra-paths ["..."]}
           :alias-5 {:extra-paths ["..."]}}}
```

With a function for computing all required aliases for any given one (and a bit
more under the hood):

```clojure
(require '[protosens.maestro :as maestro])

(-> (maestro/search {:maestro/alias+   [:alias-1]
                     :maestro/profile+ ['dev]})
    :maestro/require)

;; [:alias-3 :alias-5 :alias-2 :alias-1]
```

Profiles are abitrary symbols used for selecting aliases, typically used for
bringing dev dependencies, test namespaces, and such. required only in some
contexts. When a map is encountered in `:maestro/require`. the algorithm uses
activated profiles to find a required alias (if any).

A task for printing required aliases makes this scheme easy to use as a
[Babashka](https://github.com/babashka/babashka) task in you `bb.edn` file,
after adding Maestro to dependencies. For instance, our own `aliases:dev` task
prints aliases required for the alias given as command-line argument while also
injecting the `:task/dev` and a couple profiles:

```clojure
{:tasks
 {aliases:dev
  {:requires ([protosens.maestro])
   :task     (protosens.maestro/task {:maestro/alias+   [:task/dev]
                                      :maestro/profile+ ['dev
                                                         'test]})}}}
```

Such a task is then easily combined with [Clojure
CLI](https://clojure.org/guides/deps_and_cli), leveraging a little shell
substitution with `$()`:

```
clj -M$( bb aliases:dev )
```


## Repository organization

The scheme exposed above is simple yet powerful. There are certainly a few ways
to organize a repository around it. In practice, the way this very repository is
organized has been proven effective and certainly a good way to start before
jazzing it up if necessary.

Indeed, key points illustrated in our own [deps.edn](../../deps.edn) are:

**Namespaced aliases**. Where the namespace is a broad category: `ext` for
external dependencies like libraries, `task` for tasks, `module` for local
modules, etc.

**Dependency pinning**. Each and every external dependency has its own alias
with its own `:extra-deps`. This ensures all aliases that need them always use
the exact same version which is a recommended goal.

**Everything has an alias**. There isn't even a top-level `:deps` or `:paths`.

**Alias data contain extra information**. Nothing is mandatory but many
recognizable key-values make things easier to follow: `:maestro/doc` provides
a docstring for the alias, `:maestro/url` links to external documentation,
`:maestro/root` shows where modules are located in the repository, etc.

**Other information for plugins and tools**. Since `deps.edn` files are data,
nothing dictates they cannot contain arbitrary key-values that tools other than
Clojure CLI can read and act upon. A common occurence among the growing
collection of Maestro plugins.

Here is [another
example](https://github.com/Convex-Dev/convex.cljc/blob/main/deps.edn) of a
similar `deps.edn` files in another growing monorepo.


## Plugins and miscellaneous tooling

Plugins typically offer fast ways for using other tools, leveraging how a Maestro
repository is organized. For instance, given required aliases, the plugin for
[Kaocha](https://github.com/lambdaisland/kaocha) is able to deduce source paths
and tests paths without having to maintain that sort of things manually.

Lastly, Maestro also bundles little function helpers and Babashka tasks of
various nature for convenience and good practices. Explore the [full
API](https://cljdoc.org/d/com.protosens/maestro/0.0.0-alpha0).


## Final notes

In order to flirt with the Maestro philosophy, one could simply clone this
repository, explore `bb tasks` alongside `deps.edn` and `bb.edn`, and see what
happens. Fortune favors the brave.
