(ns protosens.maestro

  (:require [clojure.java.io                       :as C.java.io]
            [clojure.pprint                        :as C.pprint]
            [clojure.string                        :as C.string]
            [protosens.edn.read                    :as $.edn.read]
            [protosens.graph.dfs                   :as $.graph.dfs]
            [protosens.maestro.namespace           :as $.maestro.namespace]
            [protosens.maestro.node                :as $.maestro.node]
            [protosens.maestro.plugin              :as $.maestro.plugin]
            [protosens.maestro.node.enter.default]
            [protosens.maestro.node.enter.every]
            [protosens.maestro.node.enter.god]
            [protosens.maestro.node.enter.shallow]
            [protosens.term.style                  :as $.term.style]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;; Main algorithms


(defn- -flatten-deps-edn

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



(def -color-edge

  $.term.style/fg-yellow)



(defn- -print-tree-start

  []

  (when $.maestro.plugin/*print-path?*
    (println (str -color-edge
                  "│"
                  $.term.style/reset))))



(defn- -print-node

  [state-before-enter state-after-enter node]

  (when $.maestro.plugin/*print-path?*
    (let [not-visited-after? (fn [node]
                               (not ($.maestro.node/visited? state-after-enter
                                                             node)))]
      (println (str -color-edge
                    (C.string/join (map (fn [level]
                                          (if-some [level-2 (next level)]
                                            (if (some not-visited-after?
                                                      level-2)
                                              "│       "
                                              "·       ")
                                            "        "))
                                        (reverse (rest ($.graph.dfs/frontier state-before-enter)))))
                    (if (some not-visited-after?
                                 ($.graph.dfs/pending-sibling+ state-before-enter))
                         "├───────"
                         ,
                         "└───────")
                    $.term.style/reset
                    (if ($.maestro.node/accepted? state-after-enter
                                                  node)
                      $.term.style/fg-green
                      $.term.style/fg-red)
                    node
                    $.term.style/reset)))))



(defn- -enter-node

  [state]

  (let [node ($.graph.dfs/node state)]
    (when-not (keyword? node)
      ($.maestro.plugin/fail (format "`%s` should be a keyword!"
                                     (pr-str node))))
    (if ($.maestro.node/visited? state
                                 node)
      state
      (let [state-2 ($.maestro.node/enter state
                                          node)]
        (-print-node state
                     state-2
                     node)
        state-2))))



(defn- -expand-input

  [node+]

  (into []
        (comp (mapcat (fn [alias]
                        (if (qualified-keyword? alias)
                          [(keyword (namespace alias))
                           alias]
                          [alias])))
              (distinct))
        node+))



(defn- -init-state

  [deps-maestro-edn node+]

  (-> {::deps-edn         (dissoc deps-maestro-edn
                                  :aliases)
       ::deps-maestro-edn deps-maestro-edn
       ::path             []}
      ($.maestro.namespace/init-state)
      ($.maestro.node/init-state node+)))



(defn  run

  [node+ deps-maestro-edn]

  (let [node-2+ (-expand-input node+)]
    (-print-tree-start)
    (-> (-init-state deps-maestro-edn
                     node-2+)
        ($.graph.dfs/walk -enter-node
                          node-2+)
        (-flatten-deps-edn))))



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


  ([string-node+]

   ($.maestro.plugin/intro "maestro")
   ($.maestro.plugin/step "Selecting required modules")
   (let [alias-str-2      (or string-node+
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
     ($.maestro.plugin/step "Writing selection to `deps.edn`")
     (with-open [file (C.java.io/writer "deps.edn")]
       (C.pprint/pprint deps-edn
                        file))
     ($.maestro.plugin/done "`deps.edn` is ready")
     deps-edn)))
