# Modules

Publicly available as [Git dependencies](https://clojure.org/guides/deps_and_cli#_using_git_libraries) for [Clojure CLI](https://clojure.org/guides/deps_and_cli):

| Module | Description |
|---|---|
| [`bb.help`](./bb.help) | Printing extra documentation for Babashka tasks |
| [`bench`](./bench) | Higher-level helpers for [Criterium](https://github.com/hugoduncan/criterium) |
| [`classpath`](./classpath) | Classpath-related utilities |
| [`deps.edn`](./deps.edn) | Handling `deps.edn` files |
| [`edn`](./edn) | One-liners for reading EDN |
| [`git`](./git) | One-liners (or almost) for common Git operations |
| [`maestro`](./maestro) | Maestro, calmely orchestrating your Clojure (mono)repo |
| [`maestro.idiom`](./maestro.idiom) | Bundle of modules for idiomatic use of [Maestro](../maestro) and well as more opinionated tooling |
| [`maestro.plugin.build`](./maestro.plugin.build) | Maestro plugin for [`tools.build`](https://github.com/clojure/tools.build).   |
| [`maestro.plugin.clj-kondo`](./maestro.plugin.clj-kondo) | Maestro plugin for [Clj-kondo](https://github.com/clj-kondo/clj-kondo), the Clojure linter |
| [`maestro.plugin.kaocha`](./maestro.plugin.kaocha) | Maestro plugin for the [Kaocha](https://github.com/lambdaisland/kaocha) test runner |
| [`maestro.plugin.quickdoc`](./maestro.plugin.quickdoc) | Maestro plugin for [Quickdock](https://github.com/borkdude/quickdoc), the Markdown API generator.   |
| [`namespace`](./namespace) | Mainly about finding available namespaces |
| [`process`](./process) | Light wrapper over [`babashka/process`](https://github.com/babashka/process) |
| [`string`](./string) | String manipulation library complementing [`clojure.string`](https://clojuredocs.org/clojure.string) |
| [`symbol`](./symbol) | Handling symbols |

Private, not meant for public use:

| Module | Description |
|---|---|
| [`dev`](./dev) | Dev utilities used in this repository |
| [`task`](./task) | Merges dependencies and paths for Babashka tasks |
