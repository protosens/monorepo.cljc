(ns protosens.maestro.plugin.clj-kondo

  (:require [protosens.classpath                     :as $.classpath]
            [protosens.deps.edn                      :as $.deps.edn]
            [protosens.maestro                       :as $.maestro]
            [protosens.maestro.alias                 :as $.maestro.alias]
            [protosens.maestro.plugin                :as $.maestro.plugin]
            [protosens.maestro.plugin.clj-kondo.impl :as $.maestro.plugin.clj-kondo.impl]
            [protosens.term.style                    :as $.term.style]))


;;;;;;;;;; Helpers


(defn- -alias+

  [deps-edn]

  (binding [$.maestro.plugin/*print-path?* true]
    (-> ($.maestro/run-string (or (first *command-line-args*)
                                  ":GOD")
                              deps-edn)
        ($.maestro.alias/accepted))))


;;;;;;;;;; Tasks


(defn lint

  []

  ($.maestro.plugin/intro "maestro.plugin.clj-kondo/lint")
  ($.maestro.plugin/safe
    (delay
      ($.maestro.plugin/step "Linting all `:extra-paths`")
      (let [deps-edn ($.maestro.plugin/read-deps-edn)
            alias+   (-alias+ deps-edn)
            path+    ($.deps.edn/extra-path+ deps-edn
                                             alias+)
            analysis   ($.maestro.plugin.clj-kondo.impl/run
                         {:lint     path+
                          :parallel true})
            summary    (analysis :summary)
            n-error    (summary :error)
            n-warning  (summary :warning)]
        (if (or (> n-error
                   0)
                (> n-warning
                   0))            
            (do
              ($.maestro.plugin/step "Files to fix:")
              (doseq [[file
                       data] (sort-by first
                                      (group-by :filename
                                                (analysis :findings)))]
                ($.maestro.plugin/step 1
                                       (str $.term.style/bold
                                            file
                                            $.term.style/reset))
                (doseq [data-2  (sort (fn [data-a data-b]
                                          (let [row-a (data-a :row)
                                                row-b (data-b :row)]
                                            (if (= row-a
                                                   row-b)
                                              (compare (data-a :col)
                                                       (data-b :col))
                                              (compare row-a
                                                       row-b))))
                                        data)]
                  ($.maestro.plugin/step 2
                                         (format "%s%s:%s [%s]%s %s"
                                                 $.term.style/fg-red
                                                 (data-2 :row)
                                                 (data-2 :col)
                                                 (name (data-2 :level))
                                                 $.term.style/reset
                                                 (data-2 :message)))))
              ($.maestro.plugin/fail (format "%d error%s, %d warning%s"
                                             n-error
                                             (if (> n-error
                                                    1)
                                               "s"
                                               "")
                                             n-warning
                                             (if (> n-warning
                                                    1)
                                               "s"
                                               ""))))
          ($.maestro.plugin/done "Everything is fine"))))))



(defn prepare

  []

  ($.maestro.plugin/intro "maestro.plugin.clj-kondo/prepare")
  ($.maestro.plugin/safe
    (delay
      ($.maestro.plugin/step "Preparing everything for Clj-kondo")
      ($.maestro.plugin/step "Computing classpath based on `deps.edn`")
      (let [deps-edn ($.maestro.plugin/read-deps-edn)
            alias+   (-alias+ deps-edn)
            cp       ($.classpath/compute alias+)]
        ($.maestro.plugin/step "Running analysis")
        (println)
        ($.maestro.plugin.clj-kondo.impl/run
          {:copy-configs true
           :dependencies true
           :lint         [cp]
           :parallel     true})
        ($.maestro.plugin/done "Clj-kondo is ready")))))
