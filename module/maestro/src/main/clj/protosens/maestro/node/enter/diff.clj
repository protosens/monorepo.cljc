(ns protosens.maestro.node.enter.diff

  (:require [protosens.deps.edn.diff.alias :as       $.deps.edn.diff.alias]
            [protosens.deps.edn.diff.rev   :as-alias $.deps.edn.diff.rev]
            [protosens.maestro             :as-alias $.maestro]
            [protosens.maestro.alias       :as       $.maestro.alias]
            [protosens.maestro.diff        :as       $.maestro.diff]
            [protosens.maestro.node        :as       $.maestro.node]
            [protosens.term.style          :as       $.term.style]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;;


(defn enter

  [state node]

  (if (qualified-keyword? node)
    (let [dirty (-> {::$.deps.edn.diff.alias/unprocessed ($.maestro.alias/accepted state)
                     ::$.deps.edn.diff.rev/old           (name node)}
                    ($.maestro.diff/augmented)
                    ($.deps.edn.diff.alias/dirty))
          input (vec dirty)]
      (println)
      (println (str $.term.style/bold
                    $.term.style/fg-red
                    "········································"
                    $.term.style/reset))
      (println)
      (-> state
          ($.maestro.node/init-state input)
          ($.maestro.node/accept node
                                 input)))
    ($.maestro.node/accept state
                           node)))



(defmethod $.maestro.node/enter
           "DIFF"

  [state node]

  (enter state
         node))
