(ns protosens.maestro

  (:require [clojure.java.io          :as C.java.io]
            [clojure.pprint           :as C.pprint]
            [clojure.string           :as C.string]
            [protosens.edn.read       :as $.edn.read]
            [protosens.maestro.plugin :as $.maestro.plugin]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;; Generic graph implementations


(defn dfs

  [state enter exit node+]

  (loop [state-2 (assoc state
                        ::level (seq node+)
                        ::stack '())]
    (if-some [level (seq (state-2 ::level))]
      (let [node    (first level)
            state-3 (-> state-2
                        (dissoc ::level)
                        (update ::stack
                                conj
                                (rest level))
                        (enter node))]
        (-> state-3 
            (recur)))
      (if-some [stack (seq (state-2 ::stack))]
        (-> state-2
            (assoc ::level (first stack)
                   ::stack (rest stack))
            (exit)
            (recur))
        state-2))))


;;;;;;;;;; Private helpers


(defn- -conj-path

  [state node]

  (update state
          ::path
          conj
          [node
           (dec (count (state ::stack)))]))



(defn- -flatten-alias+

  [state]

  (update state
          ::deps-edn
          (fn [deps-edn]
            (reduce (fn [deps-edn-2 alias-def]
                      (-> deps-edn-2
                          (update :deps
                                  merge ;; TODO. Ensure that no dep gets overwritten?
                                  (:extra-deps alias-def))
                          (update :paths
                                  (fnil into
                                        [])
                                  (:extra-paths alias-def))))
                    deps-edn
                    (vals (deps-edn :aliases))))))



(defn- -next-level

  [state alias level]
  
  (-> state
      (-conj-path alias)
      (assoc ::level
             level)))



(defn- -require

  [state alias]

  (-> state
      (assoc-in [::deps-edn
                 :aliases
                 alias]
                (get-in state
                        [::deps-maestro-edn
                         :aliases
                         alias]))
      (-next-level alias
                   (get-in state
                           [::deps-maestro-edn
                            :aliases
                            alias
                            :maestro/require]))))


;;;;;;;;;; Dispatching keywords which may be aliases or "directives"


(defn- -search-dispatch

  [_state kw]

  (or (namespace kw)
      (name kw)))



(defmulti search

  #'-search-dispatch)


;;;


(defmethod search
           :default

  [state kw]

  (if (qualified-keyword? kw)
    ;;
    ;; Qualified.
    (if-some [definition (get-in state
                                 [::deps-maestro-edn
                                  :aliases
                                  kw])]
      (let [nspace (namespace kw)]
        (cond->
          state
          (or (and (not (contains? (state ::exclude)
                                   nspace))
                   (contains? (state ::include)
                              nspace))
              (contains? (state ::input)
                         kw))
          (-require kw)))
      ($.maestro.plugin/fail (format "Node `%s` does not exist"
                                     kw)))
    ;;
    ;; Unqualified.
    (-> state
        (update ::include
                conj
                (name kw))
        (-require kw))))



(defmethod search
           "EVERY"

  [state kw]

  (if-some [nm (name kw)]
    (-next-level state
                 kw
                 (cons (keyword nm)
                       (sort (filter (fn [alias]
                                       (= (namespace alias)
                                          nm))
                                     (keys (get-in state
                                                   [::deps-maestro-edn
                                                    :aliases]))))))
    state))



(defmethod search
           "GOD"

  [state kw]

  (when (qualified-keyword? kw)
    ($.maestro.plugin/fail (format "`:GOD` node should not be namespaced: `%s`"
                                   kw)))
  (-next-level state
               kw
               (let [alias+ (keys (get-in state
                                          [::deps-maestro-edn
                                           :aliases]))]
                 (concat (sort (into #{}
                                     (comp (keep namespace)
                                           (map keyword))
                                     alias+))
                         (sort alias+)))))



(defmethod search
           "SHALLOW"

  [state kw]

  (if-some [nm (name kw)]
    (-> state
        (-conj-path kw)
        (update ::exclude
                conj
                nm)
        (update ::include
                disj
                nm))
    state))


;;;;;;;;;; Main algorithms


(defn  run

  [node+ deps-maestro-edn]

  ;; Need to dedupe input aliases because inputs are systematically visited once
  ;; the algorithm kicks in, as opposed to deps that are indeed deduped.
  ;;
  (let [node-2+ (into []
                      (comp (mapcat (fn [alias]
                                      (if (qualified-keyword? alias)
                                        [(keyword (namespace alias))
                                         alias]
                                        [alias])))
                            (distinct))
                      node+)]
    (when $.maestro.plugin/*print-path?*
      (println "\033[33m│\033[32m"))
    (-> {::deps-edn         (dissoc deps-maestro-edn
                                              :aliases)
         ::deps-maestro-edn deps-maestro-edn
         ::input            (set node-2+)
         ::exclude          #{}
         ::include          #{}
         ::path             []
         ::visited          #{}}
        ,
        (dfs (fn enter [state-2 node]
               (when-not (keyword? node)
                 ($.maestro.plugin/fail (format "`%s` should be a keyword!"
                                                (pr-str node))))
               (if (not (contains? (state-2 ::visited)
                                   node))
                 (do
                   (when $.maestro.plugin/*print-path?*
                     (let [visited? (state-2 ::visited)
                           sibling? (boolean (some (comp not
                                                         visited?)
                                                   (some-> (state-2 ::stack)
                                                           peek)))]
                       (println (format "\033[33m%s%s\033[0m%s\033[0m"
                                        (C.string/join (map (fn [level]
                                                              (if (seq level)
                                                                (if (seq (filter (comp not
                                                                                     visited?)
                                                                               level))
                                                                  "│       "
                                                                  "·       ")
                                                                "        "))
                                                            (reverse (rest (state-2 ::stack)))))
                                        (if sibling?
                                          "├───────"
                                          ,
                                          "└───────")
                                        node))))
                   (-> state-2
                       (update ::visited
                               conj
                               node)
                       (search node)))
                 state-2))
             identity
             node-2+)
        ,
        (-flatten-alias+))))



(defn run-string

  [string-node+ deps-maestro-edn]

  (run (map (fn [x]
              (try
                (keyword x)
                (catch Exception _ex
                  ($.maestro.plugin/fail (format "Input `%s` is not a keyword"
                                                 (pr-str x))))))
            (-> string-node+
                (C.string/split #":")
                (rest)))
       deps-maestro-edn))


;;;;;;;;;; Tasks


(defn task

  
  ([]

   (task nil))


  ([alias-str]

   ($.maestro.plugin/intro "maestro")
   ($.maestro.plugin/step "Selecting required modules")
   (let [alias-str-2      (or alias-str
                              (first *command-line-args*)
                              ($.maestro.plugin/fail "No input aliases given"))
         deps-maestro-edn (try
                            ($.edn.read/file "deps.maestro.edn")
                            (catch Exception ex
                              ($.maestro.plugin/fail "Unable to read `deps.maestro.edn")))
         deps-edn         (binding [$.maestro.plugin/*print-path?* true]
                            (-> (run-string alias-str-2
                                            deps-maestro-edn)
                                (::deps-edn)))]
     (with-open [file (C.java.io/writer "deps.edn")]
       (C.pprint/pprint deps-edn
                        file))
     ($.maestro.plugin/step "Writing selection to `deps.edn`")
     ($.maestro.plugin/done "`deps.edn` is ready")
     
     deps-edn)))
