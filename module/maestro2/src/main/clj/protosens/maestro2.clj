(ns protosens.maestro2

  (:require [clojure.string :as C.string]))


;;;;;;;;;; Directives


(defmulti directive
          (fn [_state nspace _nm]
            nspace))



(defmethod directive
           :default

  [state nspace nm]

  (when nm
    (throw (Exception. (format "%s/%s is neither an existing alias nor a directive"
                               nspace
                               nm))))
  (update state
          ::filter
          conj
          nspace))


;;;


(defmethod directive
           "shallow*"

  [state _nspace nm]

  (cond->
    state
    nm
    (update ::filter
            disj
            nm)))


;;;;;;;;;; Private helpers


(defn- -conj-path

  [state kw]

  (update state
          ::path
          conj
          [kw
           (state ::depth)]))



(defn- -processed?

  [state kw]

  (contains? (get-in state
                     [::result
                      :aliases])
             kw))



(defn- -transplant-def

  [state alias]

  (-> state
      (assoc-in [::result
                 :aliases
                 alias]
                (get-in state
                        [::deps
                         :aliases
                         alias]))
      (-conj-path alias)))


;;;;;;;;;; Main algorithm


(declare -run
         -run-next
         -run+)



(defn -run-maybe-alias

  [state alias]

  (when (contains? (get-in state
                           [::deps
                            :aliases])
                   alias)
    (or (when (or (contains? (state ::filter)
                             (namespace alias))
                  (zero? (state ::depth)))
          (-> state
              (-transplant-def alias)
              (-run-next alias)))
        state)))



(defn- -run-directive-long

  [state kw]

  (let [nspace-str (namespace kw)
        nspace-kw  (keyword nspace-str)]
    (-> state
        (cond->
          (not (-processed? state
                            nspace-kw))
          (-> (-transplant-def nspace-kw)
              (directive nspace-str
                         nil)
              (-run nspace-kw)))
        (-conj-path kw)
        (directive nspace-str
                   (name kw)))))



(defn -run-qualified

  [state kw]

  (or (-run-maybe-alias state
                        kw)
      (-run-directive-long state
                           kw)))



(defn -run-unqualified

  [state kw]

  (-> state
      (-transplant-def kw)
      (directive (name kw)
                 nil)
      (-run-next kw)))


;;;


(defn- -run-next

  [state kw]

  (-> state
      (update ::depth
              inc)
      (-run kw)))



(defn- -run

  [state kw]

  (-run+ state
         (get-in state
                 [::result
                  :aliases
                  kw
                  :maestro/require])))



(defn- -run+

  [state kw+]

  ;(_t :run kw+ state)
  (let [depth (state ::depth)]
    (reduce (fn [state-2 kw]
              ;(_t :reduce kw state-2)
              (or (some-> (when-not (-processed? state-2
                                                 kw)
                            (if (qualified-keyword? kw)
                              (-run-qualified state-2
                                              kw) 
                              (-run-unqualified state-2
                                                kw)))
                          (assoc ::depth
                                 depth))
                  state-2))
            state
            kw+)))


;;;;;;;;;;


(defn run

  
  ([]

   (run nil))


  ([alias+]

   (run alias+
        nil))


  ([alias+ dep+]

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
                            (map keyword
                                 (-> (or alias+
                                         (first *command-line-args*)
                                         (throw (IllegalArgumentException. "Missing aliases")))
                                     (C.string/split #":")
                                     (rest)))))]
     (-> {::deps   dep+
          ::depth  0
          ::filter #{}
          ::path   []
          ::result {}}
         (-run+ alias-2+)
         ;(::result)
         ))))
