# Table of contents
-  [`protosens.namespace`](#protosens.namespace)  - Finding and requiring namespaces automatically.
    -  [`find+`](#protosens.namespace/find+) - Finds all namespaces available in the given paths.
    -  [`require-found`](#protosens.namespace/require-found) - Requires all namespace filtered out by <code>f</code>.

-----
# <a name="protosens.namespace">protosens.namespace</a>


Finding and requiring namespaces automatically.




## <a name="protosens.namespace/find+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/namespace/src/main/clj/protosens/namespace.clj#L13-L33) `find+`</a>
``` clojure

(find+)
(find+ path+)
```


Finds all namespaces available in the given paths.

   By default, search in the current classpath.

## <a name="protosens.namespace/require-found">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/namespace/src/main/clj/protosens/namespace.clj#L37-L62) `require-found`</a>
``` clojure

(require-found f)
```


Requires all namespace filtered out by `f`.

   `f` takes a namespace as a simple and must:

   - Return `nil` if the namespace should not be required
   - Return an argument for `require` otherwise

   Namespaces are required one by one and prints what is happening.
