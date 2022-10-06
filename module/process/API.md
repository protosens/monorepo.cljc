# Table of contents
-  [`protosens.process`](#protosens.process)  - Spawning processes from Babahska or Clojure JVM.
    -  [`await`](#protosens.process/await) - Awaits the termination of the given <code>process</code>.
    -  [`destroy`](#protosens.process/destroy) - Detroys the given <code>process</code> and all its descendant.
    -  [`err`](#protosens.process/err) - Like [[out]] but for STDERR.
    -  [`exit-code`](#protosens.process/exit-code) - Returns the exit code of the given <code>process</code>.
    -  [`out`](#protosens.process/out) - Captures and returns STDOUT as a string.
    -  [`run`](#protosens.process/run) - Runs the given <code>command</code> and returns a process.
    -  [`shell`](#protosens.process/shell) - Exactly like [[run]] but STDIO is set to <code>:inherit</code>.
    -  [`success?`](#protosens.process/success?) - Returns <code>true</code> is the <code>process</code> terminated with a zero status code.

-----
# <a name="protosens.process">protosens.process</a>


Spawning processes from Babahska or Clojure JVM.

   This is a light wrapper over [`babashka.process`](https://github.com/babashka/process).
   The main purpose is to maintain a collection utilities commonly needed by the rest
   of the repository.
  
   See [`run`](#protosens.process/run).




## <a name="protosens.process/await">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/process/src/main/clj/protosens/process.clj#L97-L105) `await`</a>
``` clojure

(await process)
```


Awaits the termination of the given `process`.
  
   Returns the process with an `:exit` code.

## <a name="protosens.process/destroy">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/process/src/main/clj/protosens/process.clj#L109-L115) `destroy`</a>
``` clojure

(destroy process)
```


Detroys the given `process` and all its descendant.

## <a name="protosens.process/err">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/process/src/main/clj/protosens/process.clj#L119-L126) `err`</a>
``` clojure

(err process)
```


Like [`out`](#protosens.process/out) but for STDERR.

## <a name="protosens.process/exit-code">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/process/src/main/clj/protosens/process.clj#L130-L138) `exit-code`</a>
``` clojure

(exit-code process)
```


Returns the exit code of the given `process`.

   Blocks until termination if needed.

## <a name="protosens.process/out">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/process/src/main/clj/protosens/process.clj#L142-L151) `out`</a>
``` clojure

(out process)
```


Captures and returns STDOUT as a string.
  
   Trims whitespace at the end (typically, a new line).

## <a name="protosens.process/run">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/process/src/main/clj/protosens/process.clj#L41-L75) `run`</a>
``` clojure

(run command)
(run command option+)
```


Runs the given `command` and returns a process.

   Supported options are:

   | Key         | Value                       | Default                |
   |-------------|-----------------------------|------------------------|
   | `:dir`      | Working directory           | Current directory      |
   | `:env`      | Environment variables map   | `nil`                  |
   | `:err`      | STDERR                      | A Java `OutputStream`  |
   | `:in`       | STDIN                       | A Java `InputStream`   |
   | `:out`      | STDOUT                      | A Java `OutputStream`  |
   | `:shutdown` | Shutdown hook               | [`destroy`](#protosens.process/destroy)            |

   STDIO arguments must be compatible with `clojure.java.io/copy` or be set to `:inherit`
   (meaning they will be inherited from the current process).

   The shutdown function is executed on clean-up.
  
   **Note:** this uses `babasha.process/process` but there is no guarantee other options will
   be supported in the future.

## <a name="protosens.process/shell">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/process/src/main/clj/protosens/process.clj#L19-L37) `shell`</a>
``` clojure

(shell command)
(shell command option+)
```


Exactly like [`run`](#protosens.process/run) but STDIO is set to `:inherit`.

## <a name="protosens.process/success?">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/process/src/main/clj/protosens/process.clj#L155-L163) `success?`</a>
``` clojure

(success? process)
```


Returns `true` is the `process` terminated with a zero status code.
  
   Blocks until temrination if needed.

-----
