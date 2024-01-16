(ns protosens.maestro

  (:require [clojure.string                        :as C.string]
            [protosens.graph.dfs                   :as $.graph.dfs]
            [protosens.maestro.alias               :as $.maestro.alias]
            [protosens.maestro.node                :as $.maestro.node]
            [protosens.maestro.node.enter.default]
            [protosens.maestro.node.enter.diff]
            [protosens.maestro.node.enter.every]
            [protosens.maestro.node.enter.god]
            [protosens.maestro.node.enter.invert]
            [protosens.maestro.node.enter.shallow]
            [protosens.maestro.plugin              :as $.maestro.plugin]
            [protosens.maestro.qualifier           :as $.maestro.qualifier]
            [protosens.process                     :as $.process]
            [protosens.string                      :as $.string]
            [protosens.term.style                  :as $.term.style]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;; Printing a tree of selected nodes during a run


(def ^:private -color-tree

  $.term.style/fg-yellow)



(defn- -print-node

  [state-before-enter state-after-enter node]

  (when $.maestro.plugin/*print-path?*
    (let [not-visited-after? (fn [node]
                               (not ($.maestro.node/visited? state-after-enter
                                                             node)))
          color-node         (cond
                               ($.maestro.node/input? state-before-enter
                                                      node)
                               (str $.term.style/bg-green
                                    $.term.style/fg-black)
                               ;;
                               ($.maestro.node/accepted? state-after-enter
                                                         node)
                               $.term.style/fg-green
                               ;;
                               :else  ; rejected
                               $.term.style/fg-red)
          level              (if (some not-visited-after?
                                       ($.graph.dfs/pending-sibling+ state-before-enter))
                                  "├───────"
                                  ,
                                  "└───────")
          prior-level+       (C.string/join (map (fn [level]
                                                   (if-some [level-2 (next level)]
                                                     (if (some not-visited-after?
                                                               level-2)
                                                       "│       "   ;; Unvisited upstream nodes.
                                                       "·       ")  ;; Scheduled upstream nodes have been visited.
                                                     "        "))   ;; Neither.
                                                 (-> state-before-enter
                                                     ($.graph.dfs/frontier)
                                                     (rest)
                                                     (reverse))))]
      (println (str -color-tree
                    prior-level+
                    level
                    $.term.style/reset
                    color-node
                    node
                    " "
                    $.term.style/reset)))))



(defn- -print-tree-begin

  []

  (when $.maestro.plugin/*print-path?*
    (println (str -color-tree
                  "│"
                  $.term.style/reset))))



;;;;;;;;;; Main algorithms


(defn- -enter-node

  [state]

  (let [node ($.graph.dfs/node state)]
    (when-not (keyword? node)
      ($.maestro.plugin/fail (format "Node `%s` is not a keyword"
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



(defn- -init-state

  [deps-edn node+]

  (-> {::deps.edn deps-edn}
      ($.maestro.qualifier/init-state)
      ($.maestro.node/init-state node+)))


;;;


(defn run

  [node+ deps-edn]

  (let [node-2+ ($.maestro.node/expand-input node+)]
    (-print-tree-begin)
    (-> (-init-state deps-edn
                     node-2+)
        ($.graph.dfs/walk -enter-node
                          node-2+))))



(defn run-string

  [string-node+ deps-edn]

  (let [node+ (map keyword
                   (or (-> string-node+
                           (C.string/split #":")
                           (next))
                       ($.maestro.plugin/fail "Given input nodes are not keywords")))]
    (run node+
         deps-edn)))


;;;;;;;;;; Tasks


(defn- -clojure

  [command arg+]

  (-> ($.process/shell (cons command
                             (map (fn [arg]
                                    (let [-? ($.string/cut-out arg
                                                               0
                                                               2)]
                                      (if (contains? #{"-A"
                                                       "-M"
                                                       "-T"
                                                       "-X"}
                                                     -?)
                                        (let [alias+ (-> arg
                                                         ($.string/trunc-left 2)
                                                         (run-string ($.maestro.plugin/read-deps-edn))
                                                         ($.maestro.alias/accepted))]
                                          (when $.maestro.plugin/*print-path?*
                                            (println))
                                          (str -?
                                               (C.string/join ""
                                                              alias+)))
                                        arg)))
                                  arg+)))
      ($.process/exit-code)))



(defn clj

  [arg+]

  (binding [$.maestro.plugin/*print-path?* true]
    (-clojure "clj"
              arg+)))



(defn clojure

  [arg+]

  (-clojure "clojure"
            arg+))
