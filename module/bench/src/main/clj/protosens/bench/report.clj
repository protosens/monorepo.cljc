(ns protosens.bench.report

  (:require [criterium.core         :as       criterium]
            [protosens.bench        :as-alias $.bench]
            [protosens.bench.report :as-alias $.bench.report]))


;;;;;;;;;; Private


(defn- -report

  [result option+]

  (apply criterium/report-result
         result
         (::$.bench.report/option+ option+)))


;;;;;;;;;; Public


(defn run

  [run]

  (-report (run ::$.bench/result)
           run)
  run)



(defn run+

  [run+]

  (println "[RESULTS]")
  (println)
  (doseq [[id
           data] (run+ ::$.bench/scenario+)]
    (println id)
    (println)
    (-report (data ::$.bench/result)
             run+)
    (println)
    (println "-----"))
  (println "-----")
  (println)
  (println "[SPEED RATIOS]")
  (println)
  (doseq [[id
           id-target->ratio+] (run+ ::$.bench/id->ratio+)]
    (println id)
    (doseq [[id-target
             ratio]    id-target->ratio+]
      (println " "
               id-target)
      (println "   "
               (str ratio
                    "x"))))
  (println)
  (println "[CONCLUSION]")
  (println)
  (let [[id-fast
         id-slow
         ratio]  (run+ ::$.bench/fastest)]
    (println (format "%s is %.2f faster than %s"
                     id-fast
                     ratio
                     id-slow)))
  run+)
