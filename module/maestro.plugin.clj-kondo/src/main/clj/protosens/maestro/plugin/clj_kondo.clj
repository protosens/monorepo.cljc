(ns protosens.maestro.plugin.clj-kondo

  (:require [protosens.classpath                     :as $.classpath]
            [protosens.maestro.plugin                :as $.maestro.plugin]
            [protosens.maestro.plugin.clj-kondo.impl :as $.maestro.plugin.clj-kondo.impl]
            [protosens.term.style                    :as $.term.style]))


;;;;;;;;;; Tasks


(defn lint


  ([]

   (lint nil))


  ([deps-edn]

   ($.maestro.plugin/intro "maestro.plugin.clj-kondo/lint")
   ($.maestro.plugin/safe
     (delay
       ($.maestro.plugin/step "Linting `:paths`")
       (let [deps-edn-2 (or deps-edn
                            ($.maestro.plugin/read-file-edn "deps.edn"))
             path+      (deps-edn-2 :paths)
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
           ($.maestro.plugin/done "Everything is fine")))))))



(defn prepare

  []

  ($.maestro.plugin/intro "maestro.plugin.clj-kondo/prepare")
  ($.maestro.plugin/safe
    (delay
      ($.maestro.plugin/step "Preparing everything for Clj-kondo")
      ($.maestro.plugin/step "Computing classpath based on `deps.edn`")
      (let [cp ($.classpath/compute)]
        ($.maestro.plugin/step "Running analysis")
        ($.maestro.plugin.clj-kondo.impl/run
          {:copy-configs true
           :dependencies true
           :lint         [cp]
           :parallel     true})
        ($.maestro.plugin/done "Clj-kondo is ready")))))
