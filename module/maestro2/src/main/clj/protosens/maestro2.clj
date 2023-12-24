(ns protosens.maestro2

  (:require [clojure.string :as C.string]))


;;;;;;;;;;


(defmulti process-mode
          (fn [_state nspace _nm]
            nspace))



(defmethod process-mode
           :default

  [state nspace nm]

  (when nm
    (throw (Exception. (format "%s/%s is neither an existing alias nor directive"
                               nspace
                               nm))))
  (update state
          ::filter
          conj
          (name nspace)))


;;;


(defmethod process-mode
           :shallow*

  [state _nspace nm]

  (cond->
    state
    nm
    (update ::filter
            disj
            nm)))


;;;;;;;;;;


(defn- -run

  [state alias+]

  (let [depth (state ::depth)]
    (reduce (fn [state-2 alias]
              (or (when-not (contains? (get-in state-2
                                               [::result
                                                :aliases])
                                       alias)
                    (if (qualified-keyword? alias)
                      (or (when-some [alias-def (get-in state-2
                                                        [::deps
                                                         :aliases
                                                         alias])]
                            (or (when (or (contains? (state-2 ::filter)
                                                     (namespace alias))
                                          (zero? depth))
                                  (-> state-2
                                      (assoc-in [::result
                                                 :aliases
                                                 alias]
                                                alias-def)
                                      (update ::path
                                              conj
                                              alias)
                                      (assoc ::depth
                                             (inc depth))
                                      (-run (:maestro/require alias-def))
                                      (assoc ::depth
                                             depth)))
                                state-2))
                          (-> state-2
                              (cond->
                                (not (contains? (get-in state-2
                                                        [::result
                                                         :aliases])
                                                (keyword (namespace alias))))
                                (-> (assoc-in [::result
                                               :aliases
                                               (keyword (namespace alias))]
                                              (get-in state-2
                                                      [::deps
                                                       :aliases
                                                       (keyword (namespace alias))]))
                                    (update ::path
                                            conj
                                            (keyword (namespace alias)))
                                    (process-mode (keyword (namespace alias))
                                                  nil)
                                    (assoc ::depth
                                           (inc depth))
                                    (-run (:maestro/require  (get-in state-2
                                                                     [::deps
                                                                      :aliases
                                                                      (keyword (namespace alias))])))
                                    (assoc ::depth
                                           depth)))
                              (update ::path
                                      conj
                                      alias)
                              (process-mode (keyword (namespace alias))
                                            (name alias))))
                      (let [mode-def (get-in state-2
                                             [::deps
                                              :aliases
                                              alias])]
                        (-> state-2
                            (assoc-in [::result
                                       :aliases
                                       alias]
                                      mode-def)
                            (update ::path
                                    conj
                                    alias)
                            (process-mode alias
                                          nil)
                            (assoc ::depth
                                   (inc depth))
                            (-run (:maestro/require mode-def))
                            (assoc ::depth
                                   depth)))))
                  state-2))
            state
            alias+)))


;;;;;;;;;;


(defn run

  
  ([]

   (run nil))


  ([alias+]

   (run alias+
        nil))


  ([alias+ dep+]

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
                                         *command-line-args*)
                                     (C.string/split #":")
                                     (rest)))))]
     (-> {::deps   dep+
          ::depth  0
          ::filter #{}
          ::path   []
          ::result {}}
         (-run alias-2+)
         ;(::result)
         ))))
