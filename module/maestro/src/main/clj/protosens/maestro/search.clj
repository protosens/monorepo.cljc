(ns protosens.maestro.search

  (:require [protosens.maestro :as-alias $.maestro]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;;


(defn conj-path

  [state node]

  (update state
          ::$.maestro/path
          conj
          [node
           (dec (count (state ::$.maestro/stack)))]))



(defn deeper

  [state node level]
  
  (-> state
      (conj-path node)
      (assoc ::$.maestro/level
             level)))



(defn input?

  [state node]

  (contains? (state ::$.maestro/input)
             node))
