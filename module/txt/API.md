# Table of contents
-  [`protosens.txt`](#protosens.txt)  - Collection of string manipulation utilities.
    -  [`count-leading-space`](#protosens.txt/count-leading-space) - Returns the number of whitespaces in the given string.
    -  [`cut-out`](#protosens.txt/cut-out) - Returns the sub-string of <code>s</code> starting at <code>i-begin</code> (inclusive) and ending at <code>i-end</code> (exclusive).
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

## <a name="protosens.txt/cut-out">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/txt/src/main/clj/protosens/txt.clj#L26-L35) `cut-out`</a>
``` clojure

(cut-out s i-begin i-end)
```


Returns the sub-string of `s` starting at `i-begin` (inclusive) and ending
   at `i-end` (exclusive).

## <a name="protosens.txt/newline">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/txt/src/main/clj/protosens/txt.clj#L39-L45) `newline`</a>
``` clojure

(newline)
```


Returns the platform-dependend line separator.

## <a name="protosens.txt/realign">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/txt/src/main/clj/protosens/txt.clj#L49-L80) `realign`</a>
``` clojure

(realign s)
```


Realign all lines in the given string.
   Useful for printing multi-line EDN strings.
 
   More precisely:

   - Leading whitespace is truncated on the first line
   - Other lines are truncated by the smallest leading whitespace of them all
  
   Also see [`count-leading-space`](#protosens.txt/count-leading-space).
