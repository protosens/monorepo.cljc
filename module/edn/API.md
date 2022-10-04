# Table of contents
-  [`protosens.edn.read`](#protosens.edn.read)  - Reading EDN data.
    -  [`file`](#protosens.edn.read/file) - Reads the first object from the file at the given <code>path</code>.
    -  [`string`](#protosens.edn.read/string) - Reads the first object in the given string.

-----
# <a name="protosens.edn.read">protosens.edn.read</a>


Reading EDN data.

   Accepted options for reading are:

   | Key               | Value                                               | Default |
   |-------------------|-----------------------------------------------------|---------|
   | `:default-reader` | Used when `:tag->reader` falls short                | `nil`   |
   | `:end`            | Value returned when the end of the input is reached | Throws  |
   | `:tag->reader     | Map of tagged reader functions                      | `{}`    |

   The default reader (if provided) is only used when there is no reader function for a given tag
   in `:tag->reader`.




## <a name="protosens.edn.read/file">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/edn/src/main/clj/protosens/edn/read.clj#L46-L63) `file`</a>
``` clojure

(file path)
(file path option+)
```


Reads the first object from the file at the given `path`.

## <a name="protosens.edn.read/string">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/edn/src/main/clj/protosens/edn/read.clj#L67-L81) `string`</a>
``` clojure

(string s)
(string s option+)
```


Reads the first object in the given string.
