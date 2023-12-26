(ns protosens.maestro

  (:require [clojure.java.io          :as C.java.io]
            [clojure.pprint           :as C.pprint]
            [clojure.string           :as C.string]
            [protosens.edn.read       :as $.edn.read]
            [protosens.maestro.plugin :as $.maestro.plugin]
            [protosens.maestro.walk   :as $.maestro.walk]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;; Main algorithm


(defn run

  
  ([]

   (run nil))


  ([alias-str]

   (let [state  ($.maestro.walk/run-string (or alias-str
                                               (first *command-line-args*)
                                               ($.maestro.plugin/fail "No input aliases given"))
                                           (try
                                             ($.edn.read/file "deps.maestro.edn")
                                             (catch Exception ex
                                               ($.maestro.plugin/fail "Unable to read `deps.maestro.edn"))))
         result (state ::result)]
     (with-open [file (C.java.io/writer "deps.edn")]
       (C.pprint/pprint result
                        file))
     (println)
     (println "[maestro]")
     (println)
     (println "- Prepared `deps.edn` for:")
     (println)
     (doseq [[alias
              depth] (state ::path)]
       (println (format "%s%s"
                        (C.string/join (repeat (inc depth)
                                               "  "))
                        alias)))
     result)))
