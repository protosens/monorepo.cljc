(ns protosens.maestro

  (:refer-clojure :exclude [sync])
  (:require [clojure.java.io                       :as C.java.io]
            [clojure.pprint                        :as C.pprint]
            [clojure.string                        :as C.string]
            [protosens.deps.edn                    :as $.deps.edn]
            [protosens.edn.read                    :as $.edn.read]
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



(defn- -flatten-deps-edn

  [state]

  (update state
          ::deps-edn
          (fn [deps-edn]
            (let [alias->definition (deps-edn :aliases)]
              ($.deps.edn/flatten deps-edn
                                  (filter (fn [alias]
                                            (not (false? (get-in alias->definition
                                                                 [alias
                                                                  :maestro/flatten?]))))
                                          ($.maestro.alias/accepted state)))))))



(defn- -init-state

  [deps-maestro-edn node+]

  (-> {::deps-edn         (assoc deps-maestro-edn
                                 :aliases
                                 {})
       ::deps-maestro-edn deps-maestro-edn}
      ($.maestro.qualifier/init-state)
      ($.maestro.node/init-state node+)))


;;;


(defn run

  [node+ deps-maestro-edn]

  (let [node-2+ ($.maestro.node/expand-input node+)]
    (-print-tree-begin)
    (-> (-init-state deps-maestro-edn
                     node-2+)
        ($.graph.dfs/walk -enter-node
                          node-2+)
        (-flatten-deps-edn))))



(defn run-string

  [string-node+ deps-maestro-edn]

  (let [node+ (map keyword
                   (or (-> string-node+
                           (C.string/split #":")
                           (next))
                       ($.maestro.plugin/fail "Given input nodes are not keywords")))]
    (run node+
         deps-maestro-edn)))


;;;;;;;;;; Tasks


(defn- -write-deps-edn

  [deps-edn]

  ($.maestro.plugin/step "Writing selection to `deps.edn`")
  (with-open [writer (C.java.io/writer "deps.edn")]
    (C.pprint/pprint deps-edn
                     writer)))



(defn sync

  
  ([]

   (sync nil))


  ([string-node+]

   (sync string-node+
         nil))


  ([string-node+ deps-maestro-edn]

   ($.maestro.plugin/intro "maestro/sync")
   ($.maestro.plugin/step "Selecting required nodes")
   ($.maestro.plugin/safe
     (delay
       (let [alias-str-2      (or string-node+
                                  (first *command-line-args*)
                                  ($.maestro.plugin/fail "No input nodes given as arguments"))
             deps-maestro-edn-2 (or deps-maestro-edn
                                    (try
                                      ($.edn.read/file "deps.maestro.edn")
                                      (catch Throwable ex
                                        ($.maestro.plugin/fail "Unable to read `deps.maestro.edn`"
                                                               ex))))
             deps-edn         (binding [$.maestro.plugin/*print-path?* true]
                                (-> (run-string alias-str-2
                                                deps-maestro-edn-2)
                                    (::deps-edn)))]
         (-write-deps-edn deps-edn)
         ($.maestro.plugin/done "`deps.edn` is ready")
         deps-edn)))))
