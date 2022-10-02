# Table of contents
-  [`protosens.classpath`](#protosens.classpath)  - Simple classpath utilities.
    -  [`compute`](#protosens.classpath/compute) - Computes the classpath for the given aliases.
    -  [`pprint`](#protosens.classpath/pprint) - Pretty-prints the given classpath.
    -  [`separator`](#protosens.classpath/separator) - Returns the platform-dependent separator used in the classpath.
    -  [`split`](#protosens.classpath/split) - Splits the given <code>classpath</code> into a vector of paths.

-----
# <a name="protosens.classpath">protosens.classpath</a>


Simple classpath utilities.




## <a name="protosens.classpath/compute">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/classpath/src/main/clj/protosens/classpath.clj#L15-L28) `compute`</a>
``` clojure

(compute alias+)
```


Computes the classpath for the given aliases.

   By running `clojure -Spath ...` in the shell.

## <a name="protosens.classpath/pprint">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/classpath/src/main/clj/protosens/classpath.clj#L32-L49) `pprint`</a>
``` clojure

(pprint)
(pprint classpath)
```


Pretty-prints the given classpath.
  
   Reads input from STDIN by default.
  
   Great match for [`compute`](#protosens.classpath/compute). Classpath is [`split`](#protosens.classpath/split) and sorted paths are printed.

## <a name="protosens.classpath/separator">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/classpath/src/main/clj/protosens/classpath.clj#L53-L59) `separator`</a>
``` clojure

(separator)
```


Returns the platform-dependent separator used in the classpath.

## <a name="protosens.classpath/split">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/classpath/src/main/clj/protosens/classpath.clj#L62-L70) `split`</a>
``` clojure

(split classpath)
```


Splits the given `classpath` into a vector of paths.
