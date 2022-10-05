# Table of contents
-  [`protosens.requirer`](#protosens.requirer) 
    -  [`bb`](#protosens.requirer/bb) - Exactly like [[clojure-cli]] but uses Babashka instead of Clojure CLI.
    -  [`clojure-cli`](#protosens.requirer/clojure-cli) - In a new process, requires all namespaces found with [[namespace+]].

-----
# <a name="protosens.requirer">protosens.requirer</a>






## <a name="protosens.requirer/bb">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/requirer/src/main/clj/protosens/requirer.clj#L38-L62) `bb`</a>
``` clojure

(bb deps-edn)
(bb deps-edn option+)
```


Exactly like [`clojure-cli`](#protosens.requirer/clojure-cli) but uses Babashka instead of Clojure CLI.

## <a name="protosens.requirer/clojure-cli">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/requirer/src/main/clj/protosens/requirer.clj#L65-L93) `clojure-cli`</a>
``` clojure

(clojure-cli deps-edn)
(clojure-cli deps-edn option+)
```


In a new process, requires all namespaces found with [[namespace+]].
  
   This is useful for ensuring that a project fully compiles for production without any
   tests dependencies and such.

   Namespaces are required one by one using Clojure CLI.

   Returns `true` if the process completed with a zero status, meaning everything has been
   required without any problem.
