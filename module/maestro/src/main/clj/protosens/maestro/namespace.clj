(ns protosens.maestro.namespace

  (:require [protosens.maestro :as-alias $.maestro]))


(set! *warn-on-reflection*
      true)


(declare include)


;;;;;;;;;; Helpers


(defn ^:no-doc init-state

  [state]

  (assoc state
         ::exclude #{}
         ::include #{}))


;;;;;;;;;; API


(defn exclude

  [state nmspace]

  (-> state
      (update ::exclude
              conj
              nmspace)
      (update ::include
              disj
              nmspace)))



(defn force-include

  [state nmspace]

  (-> state
      (update ::exclude
              disj
              nmspace)
      (include nmspace)))



(defn include

  [state nmspace]

  (update state
          ::include
          conj
          nmspace))



(defn included?

  [state nmspace]

  (and (not (contains? (state ::exclude)
                       nmspace))
       (contains? (state ::include)
                  nmspace)))
