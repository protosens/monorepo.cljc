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

   (let [alias-str-2      (or alias-str
                              (first *command-line-args*)
                              ($.maestro.plugin/fail "No input aliases given"))
         deps-maestro-edn (try
                            ($.edn.read/file "deps.maestro.edn")
                            (catch Exception ex
                              ($.maestro.plugin/fail "Unable to read `deps.maestro.edn")))
         state            ($.maestro.walk/run-string alias-str-2
                                                     deps-maestro-edn)
         deps-edn         (state ::deps-edn)]
     (with-open [file (C.java.io/writer "deps.edn")]
       (C.pprint/pprint deps-edn
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
     deps-edn)))
