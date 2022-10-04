# Table of contents
-  [`protosens.deps.edn`](#protosens.deps.edn)  - Handling <code>deps.edn</code> files.
    -  [`namespace+`](#protosens.deps.edn/namespace+) - Returns namespaces found in the [[path+]] of that <code>deps-edn</code> file.
    -  [`path+`](#protosens.deps.edn/path+) - Returns all <code>:paths</code>, prepending <code>:deps/root</code>.
    -  [`read`](#protosens.deps.edn/read) - Reads the <code>deps.edn</code> file located in <code>dir</code> (defaults to <code>./</code>).
    -  [`require-project`](#protosens.deps.edn/require-project) - In a new process, requires all namespaces found with [[namespace+]].

-----
# <a name="protosens.deps.edn">protosens.deps.edn</a>


Handling `deps.edn` files.
  
   Most useful for tool authors.




## <a name="protosens.deps.edn/namespace+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/deps.edn/src/main/clj/protosens/deps/edn.clj#L24-L38) `namespace+`</a>
``` clojure

(namespace+ deps-edn)
(namespace+ deps-edn alias+)
```


Returns namespaces found in the [`path+`](#protosens.deps.edn/path+) of that `deps-edn` file.

## <a name="protosens.deps.edn/path+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/deps.edn/src/main/clj/protosens/deps/edn.clj#L42-L64) `path+`</a>
``` clojure

(path+ deps-edn)
(path+ deps-edn alias+)
```


Returns all `:paths`, prepending `:deps/root`.
  
   A collection of aliases may be provided for including `:extra-paths`.

## <a name="protosens.deps.edn/read">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/deps.edn/src/main/clj/protosens/deps/edn.clj#L70-L91) `read`</a>
``` clojure

(read)
(read dir)
```


Reads the `deps.edn` file located in `dir` (defaults to `./`).
  
   Remembers the `dir`ectory under `:deps/root`.
  
   Typically, an entry point for using other functions from this namespace.

## <a name="protosens.deps.edn/require-project">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/deps.edn/src/main/clj/protosens/deps/edn.clj#L95-L132) `require-project`</a>
``` clojure

(require-project deps-edn)
(require-project deps-edn option+)
```


In a new process, requires all namespaces found with [`namespace+`](#protosens.deps.edn/namespace+).
  
   This is useful for ensuring that a project fully compiles for production without any
   tests dependencies and such.

   Namespaces are required one by one using Clojure CLI.

   Returns `true` if the process completed with a zero status, meaning everything has been
   required without any problem.
