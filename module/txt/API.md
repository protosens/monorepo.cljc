# Table of contents
-  [`protosens.txt`](#protosens.txt)  - Collection of string manipulation utilities.
    -  [`count-leading-space`](#protosens.txt/count-leading-space) - Returns the number of whitespaces in the given string.
    -  [`cut-out`](#protosens.txt/cut-out) - Returns the sub-string of <code>s</code> starting at <code>i-begin</code> (inclusive) and ending at <code>i-end</code> (exclusive).
    -  [`newline`](#protosens.txt/newline) - Returns the platform-dependend line separator.
    -  [`realign`](#protosens.txt/realign) - Realign all lines in the given string.
    -  [`trunc-left`](#protosens.txt/trunc-left) - Returns the given <code>s</code>tring without the first <code>n</code> characters.

-----
# <a name="protosens.txt">protosens.txt</a>


Collection of string manipulation utilities.




## <a name="protosens.txt/count-leading-space">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/txt/src/main/clj/protosens/txt.clj#L19-L29) `count-leading-space`</a>
``` clojure

(count-leading-space s)
```


Returns the number of whitespaces in the given string.

## <a name="protosens.txt/cut-out">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/txt/src/main/clj/protosens/txt.clj#L33-L42) `cut-out`</a>
``` clojure

(cut-out s i-begin i-end)
```


Returns the sub-string of `s` starting at `i-begin` (inclusive) and ending
   at `i-end` (exclusive).

## <a name="protosens.txt/newline">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/txt/src/main/clj/protosens/txt.clj#L46-L52) `newline`</a>
``` clojure

(newline)
```


Returns the platform-dependend line separator.

## <a name="protosens.txt/realign">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/txt/src/main/clj/protosens/txt.clj#L56-L86) `realign`</a>
``` clojure

(realign s)
```


Realign all lines in the given string.
   Useful for printing multi-line EDN strings.
 
   More precisely:

   - Leading whitespace is truncated on the first line
   - Other lines are truncated by the smallest leading whitespace of them all
  
   Also see [`count-leading-space`](#protosens.txt/count-leading-space).

## <a name="protosens.txt/trunc-left">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/txt/src/main/clj/protosens/txt.clj#L90-L98) `trunc-left`</a>
``` clojure

(trunc-left s n)
```


Returns the given `s`tring without the first `n` characters.
