(ns protosens.maestro.walk

  (:require [clojure.string              :as       C.string]
            [protosens.edn.read          :as       $.edn.read]
            [protosens.maestro           :as-alias $.maestro]
            [protosens.maestro.directive :as       $.maestro.directive]))


;;;;;;;;;; Private helpers


(defn- -conj-path

  [state kw]

  (update state
          ::$.maestro/path
          conj
          [kw
           (state ::$.maestro/depth)]))



(defn- -flatten-alias+

  [state]

  (update state
          ::$.maestro/result
          (fn [result]
            (reduce (fn [result-2 alias-def]
                      (-> result-2
                          (update :deps
                                  merge ;; TODO. Ensure that no dep gets overwritten?
                                  (:extra-deps alias-def))
                          (update :paths
                                  (fnil into
                                        [])
                                  (:extra-paths alias-def))))
                    result
                    (vals (result :aliases))))))



(defn- -processed?

  [state kw]

  (contains? (get-in state
                     [::$.maestro/result
                      :aliases])
             kw))



(defn- -transplant-def

  [state alias]

  (-> state
      (assoc-in [::$.maestro/result
                 :aliases
                 alias]
                (get-in state
                        [::$.maestro/deps
                         :aliases
                         alias]))
      (-conj-path alias)))


;;;;;;;;;; Main algorithms


(declare -walk
         -walk-next
         -walk+)



(defn- -walk-maybe-alias

  [state alias]

  (when (contains? (get-in state
                           [::$.maestro/deps
                            :aliases])
                   alias)
    (or (when (or (let [nspace (namespace alias)]
                    (and (not (contains? (state ::$.maestro/exclude)
                                         nspace))
                         (contains? (state ::$.maestro/include)
                                    nspace)))
                  (zero? (state ::$.maestro/depth)))
          (-> state
              (-transplant-def alias)
              (-walk-next alias)))
        state)))



(defn- -walk-directive-long

  [state kw]

  (let [nspace-str (namespace kw)
        nspace-kw  (keyword nspace-str)]
    (-> state
        (cond->
          (not (-processed? state
                            nspace-kw))
          (-> (-transplant-def nspace-kw)
              ($.maestro.directive/run nspace-str
                                       nil)
              (-walk nspace-kw)))
        (-conj-path kw)
        ($.maestro.directive/run nspace-str
                                 (name kw)))))



(defn- -walk-qualified

  [state kw]

  (or (-walk-maybe-alias state
                         kw)
      (-walk-directive-long state
                            kw)))



(defn- -walk-unqualified

  [state kw]

  (-> state
      (-transplant-def kw)
      ($.maestro.directive/run (name kw)
                               nil)
      (-walk-next kw)))


;;;


(defn- -walk-next

  [state kw]

  (-> state
      (update ::$.maestro/depth
              inc)
      (-walk kw)))



(defn- -walk

  [state kw]

  (-walk+ state
          (get-in state
                  [::$.maestro/result
                   :aliases
                   kw
                   :maestro/require])))



(defn -walk+

  [state kw+]

  (let [depth (state ::$.maestro/depth)
        level (state ::$.maestro/level)]
    (loop [state-2 (assoc state
                          ::$.maestro/level
                          kw+)]
      (if-some [level-2 (seq (state-2 ::$.maestro/level))]
        (recur
          (let [kw      (first level-2)
                state-3 (update state-2
                                ::$.maestro/level
                                rest)]
            (when-not (keyword? kw)
              (throw (IllegalArgumentException. (format "%s should be a keyword!"
                                                        (pr-str kw)))))
            (or (some-> (when-not (-processed? state-3
                                               kw)
                          (if (qualified-keyword? kw)
                            (-walk-qualified state-3
                                             kw) 
                            (-walk-unqualified state-3
                                               kw)))
                        (assoc ::$.maestro/depth
                               depth))
                state-3)))
        (assoc state-2
               ::$.maestro/level
               level)))))


;;;


(defn  run

  [alias+ dep+]

  ;; Need to dedupe input aliases because inputs are systematically visited once
  ;; the algorithm kicks in, as opposed to deps that are indeed deduped.
  ;;
  (let [alias-2+ (first
                    (reduce (fn [[alias-2+ visited+ :as state] alias]
                              (if (contains? visited+
                                             alias)
                                state
                                [(let [nspace (namespace alias)]
                                   (-> alias-2+
                                       (cond->
                                         nspace
                                         (conj (keyword nspace)))
                                       (conj alias)))
                                 (conj visited+
                                       alias)]))
                            [[]
                             #{}]
                            alias+))]
     (-> {::$.maestro/deps    dep+
          ::$.maestro/depth   0
          ::$.maestro/exclude #{}
          ::$.maestro/include #{}
          ::$.maestro/path    []
          ::$.maestro/result  (dissoc dep+
                                      :aliases)}
         (-walk+ alias-2+)
         (-flatten-alias+))))



(defn run-string

  [str-alias+ dep+]

  (run (map keyword
            (-> str-alias+
                (C.string/split #":")
                (rest)))
       dep+))
