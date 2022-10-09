# Table of contents
-  [`protosens.symbol`](#protosens.symbol)  - Collection of helpers for handling symbols.
    -  [`ends-with?`](#protosens.symbol/ends-with?) - Returns <code>true</code> if the given <code>sym</code> ends with <code>x</code>.
    -  [`includes?`](#protosens.symbol/includes?) - Returns <code>true</code> if the given <code>sym</code> include <code>x</code>.
    -  [`join`](#protosens.symbol/join) - Joins the given collection of symbols.
    -  [`qualify`](#protosens.symbol/qualify) - Qualifies or requalifies <code>sym</code> in terms of <code>namespace</code>.
    -  [`replace`](#protosens.symbol/replace) - Replaces <code>match</code>es in the given <code>sym</code>.
    -  [`replace-first`](#protosens.symbol/replace-first) - Replaces the first occurence of <code>match</code> in the given <code>sym</code>.
    -  [`split`](#protosens.symbol/split) - Splits the given <code>sym</code>.
    -  [`starts-with?`](#protosens.symbol/starts-with?) - Returns <code>true</code> if the given <code>sym</code> starts with <code>x</code>.
    -  [`stringify`](#protosens.symbol/stringify) - Transforms <code>x</code> into a string only if it is a symbol.

-----
# <a name="protosens.symbol">protosens.symbol</a>


Collection of helpers for handling symbols.

   Often similar to [`clojure.string`](https://clojuredocs.org/clojure.string).
   Actually, functions from this namesapce typically work with strings instead of input symbols but
   providing symbols better conveys the intent.




## <a name="protosens.symbol/ends-with?">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/symbol/src/main/clj/protosens/symbol.clj#L19-L26) `ends-with?`</a>
``` clojure

(ends-with? sym x)
```


Returns `true` if the given `sym` ends with `x`.

## <a name="protosens.symbol/includes?">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/symbol/src/main/clj/protosens/symbol.clj#L30-L37) `includes?`</a>
``` clojure

(includes? sym x)
```


Returns `true` if the given `sym` include `x`.

## <a name="protosens.symbol/join">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/symbol/src/main/clj/protosens/symbol.clj#L41-L60) `join`</a>
``` clojure

(join segment+)
(join separator segment+)
```


Joins the given collection of symbols.
  
   Default separator is `.`.

## <a name="protosens.symbol/qualify">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/symbol/src/main/clj/protosens/symbol.clj#L64-L71) `qualify`</a>
``` clojure

(qualify namespace sym)
```


Qualifies or requalifies `sym` in terms of `namespace`.

## <a name="protosens.symbol/replace">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/symbol/src/main/clj/protosens/symbol.clj#L90-L102) `replace`</a>
``` clojure

(replace sym match replacement)
```


Replaces `match`es in the given `sym`.
  
   Like [`clojure.string/replace`](https://clojuredocs.org/clojure.string/replace)
   but inputs can be symbols.

## <a name="protosens.symbol/replace-first">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/symbol/src/main/clj/protosens/symbol.clj#L106-L117) `replace-first`</a>
``` clojure

(replace-first sym match replacement)
```


Replaces the first occurence of `match` in the given `sym`.

   Other than that, exactly like [`replace`](#protosens.symbol/replace).

## <a name="protosens.symbol/split">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/symbol/src/main/clj/protosens/symbol.clj#L121-L139) `split`</a>
``` clojure

(split sym)
(split regex-separator sym)
```


Splits the given `sym`.
  
   Default separator is `.`.

## <a name="protosens.symbol/starts-with?">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/symbol/src/main/clj/protosens/symbol.clj#L143-L150) `starts-with?`</a>
``` clojure

(starts-with? sym x)
```


Returns `true` if the given `sym` starts with `x`.

## <a name="protosens.symbol/stringify">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/symbol/src/main/clj/protosens/symbol.clj#L154-L163) `stringify`</a>
``` clojure

(stringify x)
```


Transforms `x` into a string only if it is a symbol.

-----
