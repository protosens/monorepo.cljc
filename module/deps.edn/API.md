# Table of contents
-  [`protosens.deps.edn`](#protosens.deps.edn)  - Handling <code>deps.edn</code> files.
    -  [`main-ns`](#protosens.deps.edn/main-ns) - Pretty-prints to <code>*out*</code> a CLJC namespace requiring all namespaces provided by <code>deps.edn</code>.
    -  [`namespace+`](#protosens.deps.edn/namespace+) - Returns namespaces provided by source files in that <code>deps.edn</code>.
    -  [`path+`](#protosens.deps.edn/path+) - Returns all <code>:paths</code>, prepending <code>:deps/root</code>.
    -  [`read`](#protosens.deps.edn/read) - Reads the <code>deps.edn</code> file located in <code>dir</code> (defaults to <code>./</code>).

-----
# <a name="protosens.deps.edn">protosens.deps.edn</a>


Handling `deps.edn` files.
  
   Most useful for tool authors. [`read`](#protosens.deps.edn/read) fetches a `deps.edn` file and other functions
   are used to handle it.




## <a name="protosens.deps.edn/main-ns">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/deps.edn/src/main/clj/protosens/deps/edn.clj#L104-L151) `main-ns`</a>
``` clojure

(main-ns deps-edn ns-sym)
(main-ns deps-edn ns-sym option+)
```


Pretty-prints to `*out*` a CLJC namespace requiring all namespaces provided by `deps.edn`.

   Namespace is named after `ns-sym`.
   Pure CLJ or pure CLJS required namespaces are guarded by reader conditionals.

   Aliases to activate may be provided in `option+` under `:alias+`.

   Also see [`namespace+`](#protosens.deps.edn/namespace+).

## <a name="protosens.deps.edn/namespace+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/deps.edn/src/main/clj/protosens/deps/edn.clj#L50-L72) `namespace+`</a>
``` clojure

(namespace+ deps-edn)
(namespace+ deps-edn option+)
```


Returns namespaces provided by source files in that `deps.edn`.
  
   Options may be:

   | Key          | Value                  | Default                          |
   |--------------|------------------------|----------------------------------|
   | `:alias+`    | See [`path+`](#protosens.deps.edn/path+)          | `nil`                            |
   | `:extension+ | Source file extensions | `[".clj" ".cljc" ".cljs"]` |

## <a name="protosens.deps.edn/path+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/deps.edn/src/main/clj/protosens/deps/edn.clj#L76-L98) `path+`</a>
``` clojure

(path+ deps-edn)
(path+ deps-edn alias+)
```


Returns all `:paths`, prepending `:deps/root`.
  
   A collection of aliases may be provided for including `:extra-paths`.

## <a name="protosens.deps.edn/read">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/deps.edn/src/main/clj/protosens/deps/edn.clj#L23-L44) `read`</a>
``` clojure

(read)
(read dir)
```


Reads the `deps.edn` file located in `dir` (defaults to `./`).
  
   Remembers the `dir`ectory under `:deps/root`.
  
   Typically, an entry point for using other functions from this namespace.

-----
