# Table of contents
-  [`protosens.namespace`](#protosens.namespace)  - Finding and requiring namespaces automatically.
    -  [`from-filename`](#protosens.namespace/from-filename)
    -  [`in-cp-dir+`](#protosens.namespace/in-cp-dir+)
    -  [`in-path`](#protosens.namespace/in-path)
    -  [`in-path+`](#protosens.namespace/in-path+)
    -  [`require-cp-dir+`](#protosens.namespace/require-cp-dir+) - Requires all namespace filtered out by <code>f</code>.

-----
# <a name="protosens.namespace">protosens.namespace</a>


Finding and requiring namespaces automatically.




## <a name="protosens.namespace/from-filename">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/namespace/src/main/clj/protosens/namespace.clj#L17-L38) `from-filename`</a>
``` clojure

(from-filename filename)
(from-filename root filename)
```


## <a name="protosens.namespace/in-cp-dir+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/namespace/src/main/clj/protosens/namespace.clj#L44-L56) `in-cp-dir+`</a>
``` clojure

(in-cp-dir+)
(in-cp-dir+ option+)
```


## <a name="protosens.namespace/in-path">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/namespace/src/main/clj/protosens/namespace.clj#L60-L83) `in-path`</a>
``` clojure

(in-path path)
(in-path path option+)
```


## <a name="protosens.namespace/in-path+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/namespace/src/main/clj/protosens/namespace.clj#L88-L103) `in-path+`</a>
``` clojure

(in-path+ path+)
(in-path+ path+ option+)
```


## <a name="protosens.namespace/require-cp-dir+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/namespace/src/main/clj/protosens/namespace.clj#L109-L141) `require-cp-dir+`</a>
``` clojure

(require-cp-dir+ f)
(require-cp-dir+ f option+)
```


Requires all namespace filtered out by `f`.

   `f` takes a namespace as a simple and must:

   - Return `nil` if the namespace should not be required
   - Return an argument for `require` otherwise

   Namespaces are required one by one and prints what is happening.
