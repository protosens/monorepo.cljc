# Table of contents
-  [`protosens.maestro.plugin.clj-kondo`](#protosens.maestro.plugin.clj-kondo)  - Maestro plugin for linting Clojure code via [Clj-kondo](https://github.com/clj-kondo/clj-kondo).
    -  [`lint`](#protosens.maestro.plugin.clj-kondo/lint) - Lints the whole repository by extracting <code>:extra-paths</code> from aliases.
    -  [`prepare`](#protosens.maestro.plugin.clj-kondo/prepare) - Prepares the Clj-kondo cache by linting all dependencies and copying configuration files.

-----
# <a name="protosens.maestro.plugin.clj-kondo">protosens.maestro.plugin.clj-kondo</a>


Maestro plugin for linting Clojure code via [Clj-kondo](https://github.com/clj-kondo/clj-kondo).
  
   Assumes `clj-kondo` is installed and available in the shell.

   Those tasks only work when executed with [Babashka](https://github.com/babashka/babashka).




## <a name="protosens.maestro.plugin.clj-kondo/lint">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.plugin.clj-kondo/src/main/clj/protosens/maestro/plugin/clj_kondo.clj#L34-L69) `lint`</a>
``` clojure

(lint)
(lint option+)
```


Lints the whole repository by extracting `:extra-paths` from aliases.

   Options may be:

   | Key            | Value                                                       |
   |----------------|-------------------------------------------------------------|
   | `:path-filter` | Predicate function deciding whether a path should be linted |

## <a name="protosens.maestro.plugin.clj-kondo/prepare">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.plugin.clj-kondo/src/main/clj/protosens/maestro/plugin/clj_kondo.clj#L18-L30) `prepare`</a>
``` clojure

(prepare)
```


Prepares the Clj-kondo cache by linting all dependencies and copying configuration files.
  
   Should be called prior to [`lint`](#protosens.maestro.plugin.clj-kondo/lint)ing for the first time and on dependency updates.
