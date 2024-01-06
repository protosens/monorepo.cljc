# Table of contents
-  [`protosens.bench`](#protosens.bench)  - High-level helpers for [Criterium](https://github.com/hugoduncan/criterium).
    -  [`report`](#protosens.bench/report) - Prints result in humanized form.
    -  [`run`](#protosens.bench/run) - Benchmarks a single function.
    -  [`run+`](#protosens.bench/run+) - Runs benchmarks for several functions and compares results.
    -  [`type->reporter`](#protosens.bench/type->reporter) - Reporters used for printing results in humanized form by type.
-  [`protosens.bench.report`](#protosens.bench.report)  - Printers for reporting results in humanized form.
    -  [`run`](#protosens.bench.report/run) - Default reporter for single runs.
    -  [`run+`](#protosens.bench.report/run+) - Default reporter for multi-runs.

-----
# <a name="protosens.bench">protosens.bench</a>


High-level helpers for [Criterium](https://github.com/hugoduncan/criterium).

   While Criterium is an excellent benchmarking library, the API is at times a little hard to grasp.
   This namespace tries to make things a little simpler and more intuitive. However, to fully make
   sense of what is going on, knowledge about Criterium is expected.
  
   See [`run`](#protosens.bench/run) for benchmarking single operations and [`run+`](#protosens.bench/run+) for benchmarking and comparing several
   operations.

   Results from these functions can be printed in humanized form with [`report`](#protosens.bench/report).




## <a name="protosens.bench/report">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/lab.bench/src/main/clj/protosens/bench.clj#L190-L201) `report`</a>
``` clojure

(report x)
```


Prints result in humanized form.

   Pipe to this function values returned from [`run`](#protosens.bench/run) and [`run+`](#protosens.bench/run+).

## <a name="protosens.bench/run">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/lab.bench/src/main/clj/protosens/bench.clj#L37-L80) `run`</a>
``` clojure

(run f)
(run f option+)
```


Benchmarks a single function.

   Notable Criterium options to provide may be:

   | Key                      | Value                                            | Default |
   |--------------------------|--------------------------------------------------|---------|
   | `:gc-before-sample`      | Run garbage-collection before each sample?       | `true`  |
   | `:samples`               | Number of samples                                | `60`    |
   | `:target-execution-time` | Target duration for a single sample (nanos)      | `1e9`   |
   | `:warmup-jit-period`     | Period for runnning code before sampling (nanos) | `1e10`  |

   Running garbage-collection before each simple is best effort.

   A higher number of samples results in higher accuracy. But often, there is no need to overdo it.

   Target execution time per sample influences the number of times `f` is executed per sample.
   Hence, it should probably be adjusted if `f` takes a long time to complete otherwise samples will
   be small.

   Warmup period executes `f` without measuring it in the hope that JIT will kick-in as to not 
   bias forecoming sampling. Again, it should probably be higher if `f` takes a long time to
   complete to maximize the likelihood of JIT kicking-in.

   Overall, once should tweak these parameters if results are not consistent enough. For instance,
   one could compare an operation with itself using [`run+`](#protosens.bench/run+). Ideally, the speed ratio should be
   virtually `1`.

   Result can be humanized with [`report`](#protosens.bench/report).

## <a name="protosens.bench/run+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/lab.bench/src/main/clj/protosens/bench.clj#L137-L172) `run+`</a>
``` clojure

(run+ scenario+)
(run+ scenario+ option+)
```


Runs benchmarks for several functions and compares results.

   Takes a map of `id` -> `{:f f}`.
   Scenarios to compare, identified uniquely, pointing to map containing at least the function
   to benchmark under `:f`.

   See [`run`](#protosens.bench/run) about supported Criterium options.

## <a name="protosens.bench/type->reporter">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/lab.bench/src/main/clj/protosens/bench.clj#L178-L185) `type->reporter`</a>

Reporters used for printing results in humanized form by type.

   They come from the [`protosens.bench.report`](#protosens.bench.report).

-----

-----
# <a name="protosens.bench.report">protosens.bench.report</a>


Printers for reporting results in humanized form.




## <a name="protosens.bench.report/run">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/lab.bench/src/main/clj/protosens/bench/report.clj#L27-L37) `run`</a>
``` clojure

(run run)
```


Default reporter for single runs.
  
   See [`protosens.bench/run`](#protosens.bench/run).

## <a name="protosens.bench.report/run+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/lab.bench/src/main/clj/protosens/bench/report.clj#L41-L85) `run+`</a>
``` clojure

(run+ run+)
```


Default reporter for multi-runs.
  
   See [`protosens.bench/run+`](#protosens.bench/run+).
  
   Prints individual results as well as comparisons.
