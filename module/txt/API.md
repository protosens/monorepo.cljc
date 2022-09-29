# Table of contents
-  [`protosens.txt`](#protosens.txt)  - Collection of string manipulation utilities.
    -  [`count-leading-space`](#protosens.txt/count-leading-space) - Returns the number of whitespaces in the given string.
    -  [`newline`](#protosens.txt/newline) - Returns the platform-dependend line separator.
    -  [`realign`](#protosens.txt/realign) - Realign all lines in the given string.

-----
# <a name="protosens.txt">protosens.txt</a>


Collection of string manipulation utilities.




## <a name="protosens.txt/count-leading-space">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/txt/src/main/clj/protosens/txt.clj#L12-L22) `count-leading-space`</a>
``` clojure

(count-leading-space s)
```


Returns the number of whitespaces in the given string.

## <a name="protosens.txt/newline">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/txt/src/main/clj/protosens/txt.clj#L26-L32) `newline`</a>
``` clojure

(newline)
```


Returns the platform-dependend line separator.

## <a name="protosens.txt/realign">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/txt/src/main/clj/protosens/txt.clj#L36-L64) `realign`</a>
``` clojure

(realign s)
```


Realign all lines in the given string.
 
   Relative to the first one by truncating the smallest leading whitespace in subsequent once.
   Useful for printing multi-line EDN strings.
  
   Also see [`count-leading-space`](#protosens.txt/count-leading-space).
