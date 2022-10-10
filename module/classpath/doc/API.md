# Table of contents
-  [`protosens.classpath`](#protosens.classpath)  - Simple classpath utilities that happens to be handy once in a while.
    -  [`compute`](#protosens.classpath/compute) - Computes the classpath for the given aliases.
    -  [`current`](#protosens.classpath/current) - Returns the current classpath.
    -  [`pprint`](#protosens.classpath/pprint) - Pretty-prints the given classpath.
    -  [`separator`](#protosens.classpath/separator) - Returns the platform-dependent separator used in the classpath.
    -  [`split`](#protosens.classpath/split) - Splits the given <code>classpath</code> into a vector of paths.

-----
# <a name="protosens.classpath">protosens.classpath</a>


Simple classpath utilities that happens to be handy once in a while.




## <a name="protosens.classpath/compute">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/classpath/src/main/clj/protosens/classpath.cljc#L16-L37) `compute`</a>
``` clojure

(compute)
(compute alias+)
```


Computes the classpath for the given aliases.

   By running `clojure -Spath ...` in the shell.
  
   Returns `nil` if something goes wrong.

## <a name="protosens.classpath/current">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/classpath/src/main/clj/protosens/classpath.cljc#L41-L48) `current`</a>
``` clojure

(current)
```


Returns the current classpath.

## <a name="protosens.classpath/pprint">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/classpath/src/main/clj/protosens/classpath.cljc#L52-L70) `pprint`</a>
``` clojure

(pprint classpath)
```


Pretty-prints the given classpath.
  
   Reads input from STDIN by default.
  
   Great match for [`compute`](#protosens.classpath/compute). Classpath is [`split`](#protosens.classpath/split) and sorted paths are printed
   line by line.

## <a name="protosens.classpath/separator">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/classpath/src/main/clj/protosens/classpath.cljc#L74-L80) `separator`</a>
``` clojure

(separator)
```


Returns the platform-dependent separator used in the classpath.

## <a name="protosens.classpath/split">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/classpath/src/main/clj/protosens/classpath.cljc#L83-L91) `split`</a>
``` clojure

(split classpath)
```


Splits the given `classpath` into a vector of paths.

-----
