(ns protosens.maestro.qualifier

  ;; TODO. Could be refactor into a generic module? Allow/deny list?

  (:require [clojure.set :as C.set]))


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

  [state qualifier]

  (update state
          ::exclude
          conj
          qualifier))



(defn force-include

  [state qualifier]

  (-> state
      (unexclude qualifier)
      (include qualifier)))



(defn force-include+

  [state qualifier+]

  (let [qualifier-2+ (set qualifier+)]
    (-> state
        (unexclude+ qualifier-2+)
        (include+ qualifier-2+))))



(defn include

  [state qualifier]

  (update state
          ::include
          conj
          qualifier))



(defn include+

  [state qualifier+]

  (update state
          ::include
          C.set/union
          (set qualifier+)))



(defn included?

  [state qualifier]

  (and (not (contains? (state ::exclude)
                       qualifier))
       (contains? (state ::include)
                  qualifier)))



(defn uninclude

  [state qualifier]

  (update state
          ::include
          disj
          qualifier))



(defn unexclude

  [state qualifier]

  (update state
          ::exclude
          disj
          qualifier))



(defn unexclude+

  [state qualifier+]

  (update state
          ::exclude
          C.set/difference
          (set qualifier+)))
