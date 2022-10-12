(ns protosens.bench

  (:require [criterium.core         :as       criterium]
            [protosens.bench        :as-alias $.bench]
            [protosens.bench.report :as       $.bench.report]))


;;;;;;;;;; Single operation


(defn run


  ([f]

   (run f
        nil))


  ([f option+]

   {::$.bench/result (criterium/benchmark* f
                                           option+)
    ::$.bench/type   :run}))


;;;;;;;;;; Multi-operations


(defn- -fastest

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
                                      (double (/ (from-result (data ::$.bench/result))
                                                 (from-result (data-target ::$.bench/result))))))
                             (sorted-map)
                             (filter (fn [[id-target _data-target]]
                                       (not= id-target
                                             id))
                                     scenario+))))
            (sorted-map)
            scenario+)))


;;;


(defn run+


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
                                                                                      option+))))
         id->ratio+  (-ratio+ scenario-2+
                              option+)]
     {::$.bench/fastest    (-fastest id->ratio+)
      ::$.bench/id->ratio+ id->ratio+
      ::$.bench/scenario+  scenario-2+
      ::$.bench/type       :run+})))


;;;;;;;;;; Reporting
     

(def type->reporter

  {:run  $.bench.report/run
   :run+ $.bench.report/run+})

;;;


(defn report

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
