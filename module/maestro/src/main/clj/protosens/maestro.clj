(ns protosens.maestro

  (:require [clojure.java.io    :as C.java.io]
            [clojure.pprint     :as C.pprint]
            [clojure.string     :as C.string]
            [protosens.edn.read :as $.edn.read]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;; Public utilities


(defn fail

  [^String message]

  (if (System/getProperty "babashka.version")
    (do
      (println "[ERROR]"
               message)
      (System/exit 1))
    (throw (Exception. message))))


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
          ::include
          conj
          nspace))


;;;


(defmethod directive
           "SHALLOW"

  [state _nspace nm]

  (cond->
    state
    nm
    (-> (update ::exclude
                conj
                nm)
        (update ::include
                disj
                nm))))


;;;;;;;;;; Private helpers


(defn- -conj-path

  [state kw]

  (update state
          ::path
          conj
          [kw
           (state ::depth)]))



(defn- -flatten-alias+

  [state]

  (update state
          ::result
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


(declare -walk
         -walk-next
         -walk+)



(defn -walk-maybe-alias

  [state alias]

  (when (contains? (get-in state
                           [::deps
                            :aliases])
                   alias)
    (or (when (or (let [nspace (namespace alias)]
                    (and (not (contains? (state ::exclude)
                                         nspace))
                         (contains? (state ::include)
                                    nspace)))
                  (zero? (state ::depth)))
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
              (directive nspace-str
                         nil)
              (-walk nspace-kw)))
        (-conj-path kw)
        (directive nspace-str
                   (name kw)))))



(defn -walk-qualified

  [state kw]

  (or (-walk-maybe-alias state
                         kw)
      (-walk-directive-long state
                            kw)))



(defn -walk-unqualified

  [state kw]

  (-> state
      (-transplant-def kw)
      (directive (name kw)
                 nil)
      (-walk-next kw)))


;;;


(defn- -walk-next

  [state kw]

  (-> state
      (update ::depth
              inc)
      (-walk kw)))



(defn- -walk

  [state kw]

  (-walk+ state
          (get-in state
                  [::result
                   :aliases
                   kw
                   :maestro/require])))



(defn- -walk+

  [state kw+]

  (let [depth (state ::depth)]
    (reduce (fn [state-2 kw]
              (when-not (keyword? kw)
                (throw (IllegalArgumentException. (format "%s should be a keyword!"
                                                          (pr-str kw)))))
              (or (some-> (when-not (-processed? state-2
                                                 kw)
                            (if (qualified-keyword? kw)
                              (-walk-qualified state-2
                                               kw) 
                              (-walk-unqualified state-2
                                                 kw)))
                          (assoc ::depth
                                 depth))
                  state-2))
            state
            kw+)))


;;;;;;;;;;


(defn ^:no-doc -run

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
                            (map keyword
                                 (-> (or alias+
                                         (first *command-line-args*)
                                         (throw (IllegalArgumentException. "Missing aliases")))
                                     (C.string/split #":")
                                     (rest)))))
        dep-2+    (or dep+
                      (try
                        ($.edn.read/file "deps.maestro.edn")
                        (catch Exception ex
                          (throw (ex-info "Unable to read `deps.maestro.edn"
                                          {}
                                          ex)))))]
     (-> {::deps    dep-2+
          ::depth   0
          ::exclude #{}
          ::include #{}
          ::path    []
          ::result  (dissoc dep-2+
                            :aliases)}
         (-walk+ alias-2+)
         (-flatten-alias+))))



(defn run

  
  ([]

   (run nil))


  ([alias-str]

   (run alias-str
        nil))


  ([alias-str dep+]

   (let [state  (-run alias-str
                      dep+)
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
