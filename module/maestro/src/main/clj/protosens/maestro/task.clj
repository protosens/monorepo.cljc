(ns protosens.maestro.task

  (:require [protosens.maestro        :as $.maestro]
            [protosens.maestro.plugin :as $.maestro.plugin]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;;


(defn clj

  []

  ($.maestro.plugin/intro "maestro.task/clj")
  ($.maestro.plugin/safe
    (delay
      ($.maestro.plugin/step "Will run `clj` after expanding given aliases with Maestro")
      (System/exit ($.maestro/clj *command-line-args*)))))



(defn clojure

  []

  (System/exit ($.maestro/clojure *command-line-args*)))



(defn graph

  []

  ($.maestro.plugin/intro "maestro.task/graph")
  ($.maestro.plugin/step "Graphing given nodes by following `deps.edn` with Maestro")
  ($.maestro.plugin/safe
    (delay
      (let [node-str (or (first *command-line-args*)
                         ($.maestro.plugin/fail "No input nodes given as arguments"))
            deps-edn ($.maestro.plugin/read-deps-edn)]
        (binding [$.maestro.plugin/*print-path?* true]
          ($.maestro/run-string node-str
                                deps-edn))
        ($.maestro.plugin/done "Done")))))
