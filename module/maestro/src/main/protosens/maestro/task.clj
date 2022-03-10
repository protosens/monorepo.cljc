(ns protosens.maestro.task
  
  (:require [clojure.string             :as string]
            [protosens.maestro.required :as $.maestro.required]))


;;;;;;;;;;


(defn alias+
  

  ([]

   (-> ($.maestro.required/create-basis)
       ($.maestro.required/cli-arg)
       (alias+)))


  ([basis]

   (-> basis
       $.maestro.required/search
       $.maestro.required/print)))


(defn pprint-cp


  ([]

   (pprint-cp (slurp *in*)))


  ([raw-cp]

   (run! println
         (-> (map string/trim-newline
                  (string/split raw-cp
                                (re-pattern ":")))
             sort))))
