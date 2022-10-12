(ns protosens.bench

  "High-level helpers for [Criterium](https://github.com/hugoduncan/criterium).

   While Criterium is an excellent benchmarking library, the API is at times a little hard to grasp.
   This namespace tries to make things a little simpler and more intuitive. However, to fully make
   sense of what is going on, knowledge about Criterium is expected.
  
   See [[run]] for benchmarking single operations and [[run+]] for benchmarking and comparing several
   operations.

   Results from these functions can be printed in humanized form with [[report]]."

  (:require [criterium.core         :as       criterium]
            [protosens.bench        :as-alias $.bench]
            [protosens.bench.report :as       $.bench.report]))


;;;;;;;;;; Private


(def ^:private -option+

  ;; Default options for Criterium.
  ;;
  ;; Interned to retain more control over them.

  {:gc-before-sample      true
   :samples               60
   :target-execution-time 1e9
   :warmup-jit-period     1e10})


;;;;;;;;;; Single operation


(defn run

  "Benchmarks a single function.

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
   one could compare an operation with itself using [[run+]]. Ideally, the speed ratio should be
   virtually `1`.

   Result can be humanized with [[report]]."


  ([f]

   (run f
        nil))


  ([f option+]

   {::$.bench/result (criterium/benchmark* f
                                           (merge -option+
                                                  option+))
    ::$.bench/type   :run}))


;;;;;;;;;; Multi-operations


(defn- -fastest

  ;; Based on results from [[-ratio+]], establishes the fastest operation.

  [id->ratio+]

  (reduce (fn [acc target]
            (if (> (last target)
                   (last acc))
              target
              acc))
          (mapcat (fn [[id ratio+]]
                    (map (partial into
                                  [id])
                         ratio+))
                  id->ratio+)))



(defn- -ratio+

  ;; Computes performance ratios between pairs of operations.
  ;;
  ;; By default, uses the mean execution time per iteration as the comparator value.
  ;; E.g. `{:a {:b 2}}` means `:a` is twice as fast as `:b`. 

  [scenario+ option+]

  (let [from-result (or (::$.bench/from-result option+)
                        (comp first
                              :mean))]
    (reduce (fn [scenario-2+ [id data]]
              (assoc scenario-2+
                     id
                     (reduce (fn [acc-id [id-target data-target]]
                               (assoc acc-id
                                      id-target
                                      (double (/ (from-result (data-target ::$.bench/result))
                                                 (from-result (data ::$.bench/result))))))
                             (sorted-map)
                             (filter (fn [[id-target _data-target]]
                                       (not= id-target
                                             id))
                                     scenario+))))
            (sorted-map)
            scenario+)))


;;;


(defn run+

  "Runs benchmarks for several functions and compares results.

   Takes a map of `id` -> `function` (scenarios to compare).
   See [[run]] about supported Criterium options."


  ([scenario+]

   (run+ scenario+
         nil))


  ([scenario+ option+]

   (let [scenario-2+ (reduce (fn [scenario-2+ [k result]]
                               (assoc-in scenario-2+
                                         [k
                                          ::$.bench/result]
                                         result))
                             scenario+
                             (partition 2
                                        (interleave (keys scenario+)
                                                    (criterium/benchmark-round-robin* (map second
                                                                                           scenario+)
                                                                                      (merge -option+
                                                                                             option+)))))
         id->ratio+  (-ratio+ scenario-2+
                              option+)]
     {::$.bench/fastest    (-fastest id->ratio+)
      ::$.bench/id->ratio+ id->ratio+
      ::$.bench/scenario+  scenario-2+
      ::$.bench/type       :run+})))


;;;;;;;;;; Reporting
     

(def type->reporter

  "Reporters used for printing results in humanized form by type.

   They come from the [[protosens.bench.report]]."

  {:run  $.bench.report/run
   :run+ $.bench.report/run+})

;;;


(defn report

  "Prints result in humanized form.

   Pipe to this function values returned from [[run]] and [[run+]]."

  [x]

  (when-some [f (-> (merge type->reporter
                           (::$.bench/type->reporter x))
                    (get (x ::$.bench/type)))]
    (f x)))


;;;;;;;;;;


(comment


  (def r
       (run (fn [] (inc 42))))

  (report r)


  (def r-2
       (run+ {:a {:f (fn [] (inc 42))}
              :b {:f (fn [] (inc 42))}}))

  (report r-2)


  )
