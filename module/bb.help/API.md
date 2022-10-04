# Table of contents
-  [`protosens.bb.help`](#protosens.bb.help)  - Print extra documentation for Babashka tasks.
    -  [`print`](#protosens.bb.help/print) - Pretty-prints data maps returned from other functions.
    -  [`printer+`](#protosens.bb.help/printer+) - Default printers used by [[print]].
    -  [`task`](#protosens.bb.help/task) - Pretty-prints extra documentation for a task (if there is any).
    -  [`undocumented-task+`](#protosens.bb.help/undocumented-task+) - Returns a sorted list of tasks which do not have a <code>:protosens/doc</code>.
-  [`protosens.bb.help.print`](#protosens.bb.help.print)  - Default printers used by [[protosens.bb.help/print]].
    -  [`no-task`](#protosens.bb.help.print/no-task) - When no task has been provided as input.
    -  [`no-task+`](#protosens.bb.help.print/no-task+) - When the <code>bb.edn</code> file does not have any task.
    -  [`not-found`](#protosens.bb.help.print/not-found) - When the given task does not exist.
    -  [`task`](#protosens.bb.help.print/task) - When the given task has been found.
    -  [`undocumented-task+`](#protosens.bb.help.print/undocumented-task+) - Prints undocumented tasks.

-----
# <a name="protosens.bb.help">protosens.bb.help</a>


Print extra documentation for Babashka tasks.




## <a name="protosens.bb.help/print">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/bb.help/src/main/clj/protosens/bb/help.clj#L114-L134) `print`</a>
``` clojure

(print data)
```


Pretty-prints data maps returned from other functions.

   See [`task`](#protosens.bb.help/task), [`undocumented-task+`](#protosens.bb.help/undocumented-task+).
  
   Those data maps have a `:type` this function uses for dispatching them to a
   printer function located under `:printer+`.
   Uses [`printer+`](#protosens.bb.help/printer+) by default but any custom one can be provided under `:printer+`
   to be merged with those.

## <a name="protosens.bb.help/printer+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/bb.help/src/main/clj/protosens/bb/help.clj#L138-L148) `printer+`</a>

Default printers used by [`print`](#protosens.bb.help/print).

   They come from the [`protosens.bb.help.print`](#protosens.bb.help.print) namespace.

## <a name="protosens.bb.help/task">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/bb.help/src/main/clj/protosens/bb/help.clj#L35-L80) `task`</a>
``` clojure

(task)
(task option+)
```


Pretty-prints extra documentation for a task (if there is any).

   Extra documentation may be specified in a task under `:protosens/doc`.
   Multi-line strings will be realigned.

   Options may contain:

   | Key     | Value                                          | Default       |
   |---------|------------------------------------------------|---------------|
   | `:bb`   | Path to the Babashka config file hosting tasks | `"bb.edn"`  |
   | `:task` | Task to print (without extension)              | First CLI arg |
  
   Returns a data map that can be printed with [`print`](#protosens.bb.help/print).

## <a name="protosens.bb.help/undocumented-task+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/bb.help/src/main/clj/protosens/bb/help.clj#L83-L108) `undocumented-task+`</a>
``` clojure

(undocumented-task+)
(undocumented-task+ option+)
```


Returns a sorted list of tasks which do not have a `:protosens/doc`.

   Options may be:

   | Key   | Value                                          | Default      |
   |-------|------------------------------------------------|--------------|
   | `:bb` | Path to the Babashka config file hosting tasks | `"bb.edn"` |
  
   The return value is a data map that can be printed with [`print`](#protosens.bb.help/print).

-----
# <a name="protosens.bb.help.print">protosens.bb.help.print</a>


Default printers used by [`protosens.bb.help/print`](#protosens.bb.help/print).




## <a name="protosens.bb.help.print/no-task">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/bb.help/src/main/clj/protosens/bb/help/print.clj#L11-L24) `no-task`</a>
``` clojure

(no-task data)
```


When no task has been provided as input.
  
   Prints available tasks (documented ones).

## <a name="protosens.bb.help.print/no-task+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/bb.help/src/main/clj/protosens/bb/help/print.clj#L28-L34) `no-task+`</a>
``` clojure

(no-task+ _data)
```


When the `bb.edn` file does not have any task.

## <a name="protosens.bb.help.print/not-found">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/bb.help/src/main/clj/protosens/bb/help/print.clj#L38-L51) `not-found`</a>
``` clojure

(not-found data)
```


When the given task does not exist.

   Also prints `:no-task` ([`no-task`](#protosens.bb.help.print/no-task) by default).

## <a name="protosens.bb.help.print/task">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/bb.help/src/main/clj/protosens/bb/help/print.clj#L55-L69) `task`</a>
``` clojure

(task data)
```


When the given task has been found.
  
   Prints its docstring and `:protosens/doc` (if any).

## <a name="protosens.bb.help.print/undocumented-task+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/bb.help/src/main/clj/protosens/bb/help/print.clj#L73-L86) `undocumented-task+`</a>
``` clojure

(undocumented-task+ data)
```


Prints undocumented tasks.
