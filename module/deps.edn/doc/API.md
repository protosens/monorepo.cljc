# Table of contents
-  [`protosens.deps.edn`](#protosens.deps.edn)  - Specialized functions for handling <code>deps.edn</code> files.
    -  [`extra-path+`](#protosens.deps.edn/extra-path+) - Returns all <code>:extra-paths</code> from the given aliases.
    -  [`main-ns`](#protosens.deps.edn/main-ns) - Pretty-prints to <code>*out*</code> a CLJC namespace requiring all namespaces provided by <code>deps.edn</code>.
    -  [`namespace+`](#protosens.deps.edn/namespace+) - Returns namespaces provided by source files in that <code>deps.edn</code>.
    -  [`path+`](#protosens.deps.edn/path+) - Returns all <code>:paths</code> and <code>:extra-paths</code> for the given aliases.
    -  [`read`](#protosens.deps.edn/read) - Reads the <code>deps.edn</code> file located in <code>dir</code>.

-----
# <a name="protosens.deps.edn">protosens.deps.edn</a>


Specialized functions for handling `deps.edn` files.
  
   Most useful for tool authors. [`read`](#protosens.deps.edn/read) fetches a `deps.edn` file and other functions
   are used for extracting informations.




## <a name="protosens.deps.edn/extra-path+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/deps.edn/src/main/clj/protosens/deps/edn.clj#L71-L82) `extra-path+`</a>
``` clojure

(extra-path+ deps-edn alias+)
```


Returns all `:extra-paths` from the given aliases.

   Prepends them prepended with `:deps/root`.

## <a name="protosens.deps.edn/main-ns">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/deps.edn/src/main/clj/protosens/deps/edn.clj#L136-L183) `main-ns`</a>
``` clojure

(main-ns deps-edn ns-sym)
(main-ns deps-edn ns-sym option+)
```


Pretty-prints to `*out*` a CLJC namespace requiring all namespaces provided by `deps.edn`.

   Namespace is named after `ns-sym`.
   Pure CLJ or pure CLJS required namespaces are guarded by reader conditionals.

   Aliases to activate may be provided in `option+` under `:alias+`.

   Also see [`namespace+`](#protosens.deps.edn/namespace+).

## <a name="protosens.deps.edn/namespace+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/deps.edn/src/main/clj/protosens/deps/edn.clj#L86-L108) `namespace+`</a>
``` clojure

(namespace+ deps-edn)
(namespace+ deps-edn option+)
```


Returns namespaces provided by source files in that `deps.edn`.
  
   Options may be:

   | Key           | Value                  | Default                          |
   |---------------|------------------------|----------------------------------|
   | `:alias+`     | See [`path+`](#protosens.deps.edn/path+)          | `nil`                            |
   | `:extension+` | Source file extensions | `[".clj" ".cljc" ".cljs"]` |

## <a name="protosens.deps.edn/path+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/deps.edn/src/main/clj/protosens/deps/edn.clj#L112-L130) `path+`</a>
``` clojure

(path+ deps-edn)
(path+ deps-edn alias+)
```


Returns all `:paths` and `:extra-paths` for the given aliases.

   Prepends them prepended with `:deps/root`.

## <a name="protosens.deps.edn/read">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/deps.edn/src/main/clj/protosens/deps/edn.clj#L23-L46) `read`</a>
``` clojure

(read)
(read dir)
```


Reads the `deps.edn` file located in `dir`.
  
   Defaults to `./`.
  
   Remembers the `dir`ectory under `:deps/root`.
  
   Typically, an entry point for using other functions from this namespace.

-----
