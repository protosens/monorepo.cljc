(ns protosens.maestro.plugin.quickdoc

  "Maestro plugin generating Markdown documentation for modules using [Quickdoc](https://github.com/borkdude/quickdoc).

   Works with Babashka out of the box. For Clojure JVM, add the JVM flavor of Quickdoc to your dependencies.

   Attention, it is necessary adding `clj-kondo` to your `bb.edn` file as a [Babashka pod](https://github.com/babashka/pods):

   ```clojure
   {:pods
    {clj-kondo/clj-kondo {:version \"2022.09.08\"}}}
   ```"

  (:require [babashka.fs              :as bb.fs]
            [protosens.maestro.plugin :as $.maestro.plugin]
            [quickdoc.api             :as quickdoc]))


;;;;;;;;;; Tasks


(defn module+

  []

  ($.maestro.plugin/intro "maestro.plugin.quickdoc/module+")
  ($.maestro.plugin/safe
    (delay
      (let [deps-edn ($.maestro.plugin/read-deps-maestro-edn)
            option+  (deps-edn :maestro.plugin.quickdoc/option+)]
        (when (empty? option+)
          ($.maestro.plugin/fail "Options for Quickdoc not provided in `deps.maestro.edn`"))
        ($.maestro.plugin/step "Generating API documentation:")
        (doseq [[alias
                 path-output
                 path-src+]  (keep (fn [[alias definition]]
                                     (when-some [path-output (:maestro.plugin.quickdoc/output definition)]
                                       [alias
                                        path-output
                                        (or (not-empty (definition :extra-paths))
                                            ($.maestro.plugin/fail (format "Alias `%s` does not have any `:extra-paths`"
                                                                           alias)))]))
                                   (->> ($.maestro.plugin/read-deps-maestro-edn)
                                        (:aliases)
                                        (sort-by first)))]
          (bb.fs/create-dirs (bb.fs/parent path-output))
          (quickdoc/quickdoc (assoc option+
                                    :outfile      path-output
                                    :source-paths path-src+))
          ($.maestro.plugin/step 1
                                 (format "%s  ->  %s"
                                         alias
                                         path-output)))
        ($.maestro.plugin/done "API documentation is ready")))))
