(ns protosens.maestro.user

  "Collection of helpers useful during development, often called in `user`."

  (:require [clojure.java.classpath       :as classpath]
            [clojure.tools.namespace.find :as namespace.find]))


;;;;;;;;;;


(defn require-filtered

  "Filters all namespaces found on the classpath and requires them.

   Useful to invoke in `user` for ensuring that all expected namespaces load correctly.

   Actually, it is useful defining in `user` a short function that calls this one.
   Since `user` is accessible from everywhere, it is an easy solution for quickly requiring needed
   namespaces from anywhere at the REPL.

   Options are:

   | Key               | Value | Default |
   |-------------------|-------|---------|
   | `:fail-fast?`     | Stop when requiring one namespace fails?                             | `true` |
   | `:map-namespace`  | Function used for mapping found namespaces                           | /      |
   | `:require.after`  | Function called with a namespace after requiring it                  | /      |
   | `:require.before` | Function called with a namespace before requiring it                 | /      |
   | `:require.fail`   | Function called with a namespace and an exception in case of failure | /      |

   The value returned by `:map-namespace`, if any, will be passed to `require` as well as to any
   `:require...` function."

  [option+]

  (let [fail-fast?     (not (false? (:fail-fast? option+)))
        map-namespace  (:map-namespace option+)
        require-after  (:require.after option+)
        require-before (:require.before option+)
        require-fail   (:require.fail option+)
        nspace+        (sort-by (fn [x]
                                  (cond
                                    (symbol? x)
                                    x
                                    ;;
                                    (vector? x)
                                    (first x)
                                    ;;
                                    :else
                                    (throw (IllegalArgumentException. (str "Cannot be passed to `require`: "
                                                                           x)))))
                                (cond->>
                                  (namespace.find/find-namespaces (classpath/classpath))
                                  map-namespace
                                  (keep map-namespace)))]
    (doseq [nmspace nspace+]
      (try
        (when require-before
          (require-before nmspace))
        (require nmspace)
        (when require-after
          (require-after nmspace))
        ;;
        (catch Throwable ex
          (when require-fail
            (require-fail nmspace
                          ex))
          (if fail-fast?
            (throw (ex-info (str "While requiring a namespace: "
                                 nmspace)
                            {:namespace nmspace}
                            ex))
            (println ex)))))
    nspace+))
