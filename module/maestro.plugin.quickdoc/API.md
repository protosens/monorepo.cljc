# Table of contents
-  [`protosens.maestro.plugin.quickdoc`](#protosens.maestro.plugin.quickdoc)  - Maestro plugin generating markdown documentation for modules using [Quickdoc](https://github.com/borkdude/quickdoc) Works with Babashka out of the box.
    -  [`bundle`](#protosens.maestro.plugin.quickdoc/bundle) - Generates a single documentation file for the given aliases.
    -  [`module+`](#protosens.maestro.plugin.quickdoc/module+) - Generates documentation for modules automatically.

-----
# <a name="protosens.maestro.plugin.quickdoc">protosens.maestro.plugin.quickdoc</a>


Maestro plugin generating markdown documentation for modules using [Quickdoc](https://github.com/borkdude/quickdoc)

   Works with Babashka out of the box. For Clojure JVM, add the JVM flavor of Quickdoc to your dependencies.

   Attention, it is necessary adding the `clj-kondo` to your `bb.edn` file as a [Babashka pod](https://github.com/babashka/pods):

   ```clojure
   {:pods
    {clj-kondo/clj-kondo {:version "2022.09.08"}}}
   ```




## <a name="protosens.maestro.plugin.quickdoc/bundle">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.plugin.quickdoc/src/main/clj/protosens/maestro/plugin/quickdoc.clj#L37-L70) `bundle`</a>
``` clojure

(bundle)
(bundle option+)
(bundle option+ alias+)
```


Generates a single documentation file for the given aliases.

   All `:extra-paths` of those aliases will be merged and used as source paths.

   For options, see the Quickdoc documentation.
  
   Prints paths that have been bundled together.

## <a name="protosens.maestro.plugin.quickdoc/module+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.plugin.quickdoc/src/main/clj/protosens/maestro/plugin/quickdoc.clj#L74-L115) `module+`</a>
``` clojure

(module+)
(module+ option+)
```


Generates documentation for modules automatically.

   Selects modules that have an `:maestro.plugin.quickdoc.path/output` in their alias data specifying
   where the markdown file should be written to. Source paths are based on `:extra-paths`.

   For options, see the Quickdoc documentation.
   
   Prints which modules have produced documentation where.

-----
