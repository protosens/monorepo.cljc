(ns protosens.maestro.namespace

  (:require [protosens.maestro :as-alias $.maestro]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;;


(defn exclude

  [state nmspace]

  (-> state
      (update ::$.maestro/exclude
              conj
              nmspace)
      (update ::$.maestro/include
              disj
              nmspace)))



(defn include

  [state nmspace]

  (update state
          ::$.maestro/include
          conj
          nmspace))



(defn included?

  [state nmspace]

  (and (not (contains? (state ::$.maestro/exclude)
                       nmspace))
       (contains? (state ::$.maestro/include)
                  nmspace)))
