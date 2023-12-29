(ns protosens.maestro.namespace

  ;; TODO. Could be refactor into a generic module?

  (:require [clojure.set       :as       C.set]
            [protosens.maestro :as-alias $.maestro]))


(set! *warn-on-reflection*
      true)


(declare include
         include+
         uninclude
         unexclude
         unexclude+)


;;;;;;;;;; Helpers


(defn ^:no-doc init-state

  [state]

  (assoc state
         ::exclude #{}
         ::include #{}))


;;;;;;;;;; API


(defn exclude

  [state nmspace]

  (update state
          ::exclude
          conj
          nmspace))



(defn force-include

  [state nmspace]

  (-> state
      (unexclude nmspace)
      (include nmspace)))



(defn force-include+

  [state nmspace+]

  (let [nmspace-2+ (set nmspace+)]
    (-> state
        (unexclude+ nmspace-2+)
        (include+ nmspace-2+))))



(defn include

  [state nmspace]

  (update state
          ::include
          conj
          nmspace))



(defn include+

  [state nmspace+]

  (update state
          ::include
          C.set/union
          (set nmspace+)))



(defn included?

  [state nmspace]

  (and (not (contains? (state ::exclude)
                       nmspace))
       (contains? (state ::include)
                  nmspace)))



(defn uninclude

  [state nmspace]

  (update state
          ::include
          disj
          nmspace))



(defn unexclude

  [state nmspace]

  (update state
          ::exclude
          disj
          nmspace))



(defn unexclude+

  [state nmspace+]

  (update state
          ::exclude
          C.set/difference
          (set nmspace+)))
