# `module/maestro.idiom`  - [CHANGES](doc/changelog.md)

Bundle of modules for idiomatic use of [Maestro](../maestro) and well as more opinionated tooling.

```clojure
;; Add to dependencies in `deps.edn`:
;;
protosens/maestro.idiom
{:deps/root "module/maestro.idiom"
 :git/sha   "98f817a"
 :git/tag   "stable/2022-10-10"
 :git/url   "https://github.com/protosens/monorepo.cljc"}
```

```clojure
;; Supported platforms:
;;
[:bb :jvm]
```


---

## Provides

External dependencies:

- [`babashka.fs`](https://github.com/babashka/fs)
- [`selmer`](https://github.com/yogthos/selmer)

Modules from this repository:

- [`bb.help`](../bb.help)
- [`classpath`](../classpath)
- [`edn`](../edn)
- [`git`](../git)
- [`maestro`](../maestro)
- [`maestro.plugin.build`](../maestro.plugin.build)
- [`maestro.plugin.clj-kondo`](../maestro.plugin.clj-kondo)
- [`maestro.plugin.kaocha`](../maestro.plugin.kaocha)
- [`maestro.plugin.quickdoc`](../maestro.plugin.quickdoc)
- [`namespace`](../namespace)
- [`process`](../process)
- [`string`](../string)
- [`symbol`](../symbol)

