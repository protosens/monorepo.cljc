# Table of contents
-  [`protosens.string`](#protosens.string)  - Collection of string manipulation utilities.
    -  [`count-leading-space`](#protosens.string/count-leading-space) - Returns the number of whitespaces in the given string.
    -  [`cut-out`](#protosens.string/cut-out) - Returns the sub-string of <code>s</code> starting at <code>i-begin</code> (inclusive) and ending at <code>i-end</code> (exclusive).
    -  [`n-first`](#protosens.string/n-first) - Returns a sub-string of <code>s</code> composed of the N first characters.
    -  [`n-last`](#protosens.string/n-last) - Returns a sub-string of <code>s</code> composed of the N last characters.
    -  [`newline`](#protosens.string/newline) - Returns the platform-dependend line separator.
    -  [`realign`](#protosens.string/realign) - Realign all lines in the given string.
    -  [`trunc-left`](#protosens.string/trunc-left) - Truncates from the left.
    -  [`trunc-right`](#protosens.string/trunc-right) - Truncates from the right.

-----
# <a name="protosens.string">protosens.string</a>


Collection of string manipulation utilities.




## <a name="protosens.string/count-leading-space">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/string/src/main/clj/protosens/string.clj#L19-L31) `count-leading-space`</a>
``` clojure

(count-leading-space s)
```


Returns the number of whitespaces in the given string.
  
   More precisely, `\space` characters.

## <a name="protosens.string/cut-out">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/string/src/main/clj/protosens/string.clj#L35-L44) `cut-out`</a>
``` clojure

(cut-out s i-begin i-end)
```


Returns the sub-string of `s` starting at `i-begin` (inclusive) and ending
   at `i-end` (exclusive).

## <a name="protosens.string/n-first">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/string/src/main/clj/protosens/string.clj#L48-L56) `n-first`</a>
``` clojure

(n-first s n-char)
```


Returns a sub-string of `s` composed of the N first characters.

## <a name="protosens.string/n-last">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/string/src/main/clj/protosens/string.clj#L60-L70) `n-last`</a>
``` clojure

(n-last s n-char)
```


Returns a sub-string of `s` composed of the N last characters.

## <a name="protosens.string/newline">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/string/src/main/clj/protosens/string.clj#L74-L80) `newline`</a>
``` clojure

(newline)
```


Returns the platform-dependend line separator.

## <a name="protosens.string/realign">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/string/src/main/clj/protosens/string.clj#L84-L114) `realign`</a>
``` clojure

(realign s)
```


Realign all lines in the given string.
   Useful for printing multi-line EDN strings.
 
   More precisely:

   - Leading whitespace is truncated on the first line
   - Other lines are truncated by the smallest leading whitespace of them all
  
   Also see [`count-leading-space`](#protosens.string/count-leading-space).

## <a name="protosens.string/trunc-left">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/string/src/main/clj/protosens/string.clj#L118-L128) `trunc-left`</a>
``` clojure

(trunc-left s n)
```


Truncates from the left.
  
   Returns the given `s`tring without the first `n` characters.

## <a name="protosens.string/trunc-right">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/string/src/main/clj/protosens/string.clj#L132-L143) `trunc-right`</a>
``` clojure

(trunc-right s n)
```


Truncates from the right.
  
   Returns the given `s`tring without the last `n` characters.

-----
