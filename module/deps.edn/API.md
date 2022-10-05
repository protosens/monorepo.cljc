# Table of contents
-  [`protosens.deps.edn`](#protosens.deps.edn)  - Handling <code>deps.edn</code> files.
    -  [`path+`](#protosens.deps.edn/path+) - Returns all <code>:paths</code>, prepending <code>:deps/root</code>.
    -  [`read`](#protosens.deps.edn/read) - Reads the <code>deps.edn</code> file located in <code>dir</code> (defaults to <code>./</code>).

-----
# <a name="protosens.deps.edn">protosens.deps.edn</a>


Handling `deps.edn` files.
  
   Most useful for tool authors.




## <a name="protosens.deps.edn/path+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/deps.edn/src/main/clj/protosens/deps/edn.clj#L45-L67) `path+`</a>
``` clojure

(path+ deps-edn)
(path+ deps-edn alias+)
```


Returns all `:paths`, prepending `:deps/root`.
  
   A collection of aliases may be provided for including `:extra-paths`.

## <a name="protosens.deps.edn/read">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/deps.edn/src/main/clj/protosens/deps/edn.clj#L18-L39) `read`</a>
``` clojure

(read)
(read dir)
```


Reads the `deps.edn` file located in `dir` (defaults to `./`).
  
   Remembers the `dir`ectory under `:deps/root`.
  
   Typically, an entry point for using other functions from this namespace.
