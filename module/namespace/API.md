# Table of contents
-  [`protosens.namespace`](#protosens.namespace)  - Finding and requiring namespaces automatically.
    -  [`from-filename`](#protosens.namespace/from-filename) - Converts a <code>filename</code> to a namespace symbol.
    -  [`in-cp-dir+`](#protosens.namespace/in-cp-dir+) - Uses [[in-path+]] on directories from the current classpath.
    -  [`in-path`](#protosens.namespace/in-path) - Finds all namespaces available in the given directory <code>path</code>.
    -  [`in-path+`](#protosens.namespace/in-path+) - Exactly like [[in-path]] but works with a collection of directories.
    -  [`main-ns`](#protosens.namespace/main-ns) - Produces form for declaring a namespace <code>sym</code>.
    -  [`require-cp-dir+`](#protosens.namespace/require-cp-dir+) - Requires all namespaces found with [[in-cp-dir+]].

-----
# <a name="protosens.namespace">protosens.namespace</a>


Finding and requiring namespaces automatically.




## <a name="protosens.namespace/from-filename">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/namespace/src/main/clj/protosens/namespace.clj#L17-L49) `from-filename`</a>
``` clojure

(from-filename filename)
(from-filename root filename)
```


Converts a `filename` to a namespace symbol.

   If a `root` directory is provided, `filename` is relativized first before
   being converted.

   The extension of `filename` is remembered in the `meta`data of the produced
   symbol under `:protosens.namespace/extension`.

## <a name="protosens.namespace/in-cp-dir+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/namespace/src/main/clj/protosens/namespace.clj#L72-L89) `in-cp-dir+`</a>
``` clojure

(in-cp-dir+)
(in-cp-dir+ option+)
```


Uses [`in-path+`](#protosens.namespace/in-path+) on directories from the current classpath.
  
   Useful for detecting available namespaces.
   Does not crawl JAR files.

## <a name="protosens.namespace/in-path">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/namespace/src/main/clj/protosens/namespace.clj#L93-L124) `in-path`</a>
``` clojure

(in-path path)
(in-path path option+)
```


Finds all namespaces available in the given directory `path`.

   Options may be:

   | Key           | Value                          | Default                          |
   |---------------|--------------------------------|----------------------------------|
   | `:extension+` | Extensions for files to handle | `[".clj" ".cljc" ".cljs"]` |

## <a name="protosens.namespace/in-path+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/namespace/src/main/clj/protosens/namespace.clj#L129-L145) `in-path+`</a>
``` clojure

(in-path+ path+)
(in-path+ path+ option+)
```


Exactly like [`in-path`](#protosens.namespace/in-path) but works with a collection of directories.

## <a name="protosens.namespace/main-ns">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/namespace/src/main/clj/protosens/namespace.clj#L53-L66) `main-ns`</a>
``` clojure

(main-ns sym require+)
```


Produces form for declaring a namespace `sym`.
  
   It will require all namespaces provided in `require+`.

## <a name="protosens.namespace/require-cp-dir+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/namespace/src/main/clj/protosens/namespace.clj#L151-L186) `require-cp-dir+`</a>
``` clojure

(require-cp-dir+ f)
(require-cp-dir+ f option+)
```


Requires all namespaces found with [`in-cp-dir+`](#protosens.namespace/in-cp-dir+).

   They are filtered by `f`, a function which takes a namespace and must:
  
   - Return `nil` if the namespace should not be required
   - Return an argument for `require` otherwise

   Namespaces are required one by one and prints what is happening.
  
   Useful to put in the `user` namespace for automatically requiring a set of
   namespaces.
