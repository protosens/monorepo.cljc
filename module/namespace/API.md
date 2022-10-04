# Table of contents
-  [`protosens.namespace`](#protosens.namespace)  - Finding and requiring namespaces automatically.
    -  [`require-found`](#protosens.namespace/require-found) - Requires all namespace filtered out by <code>f</code>.
    -  [`search`](#protosens.namespace/search) - Searches for all namespaces available in the given paths.

-----
# <a name="protosens.namespace">protosens.namespace</a>


Finding and requiring namespaces automatically.




## <a name="protosens.namespace/require-found">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/namespace/src/main/clj/protosens/namespace.clj#L39-L64) `require-found`</a>
``` clojure

(require-found f)
```


Requires all namespace filtered out by `f`.

   `f` takes a namespace as a simple and must:

   - Return `nil` if the namespace should not be required
   - Return an argument for `require` otherwise

   Namespaces are required one by one and prints what is happening.

## <a name="protosens.namespace/search">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/namespace/src/main/clj/protosens/namespace.clj#L13-L33) `search`</a>
``` clojure

(search)
(search path+)
```


Searches for all namespaces available in the given paths.

   By default, search in the current classpath.
