(ns protosens.maestro.plugin.clj-kondo

  "Maestro plugin for linting Clojure code via Clj-kondo.
  
   Assumes it is already and `clj-kondo` is available in the shell."

  (:refer-clojure :exclude [import])
  (:require [protosens.maestro.classpath :as $.maestro.classpath]
            [protosens.maestro.required  :as $.maestro.required]
            [protosens.maestro.util      :as $.maestro.util]))


;;;;;;;;;;


(defn prepare

  []

  (let [cp (-> ($.maestro.required/create-basis)
               (:aliases)
               (keys)
               ($.maestro.classpath/compute))]
    (@$.maestro.util/d*shell "clj-kondo --parallel --copy-configs --lint" cp "--dependencies")))



(defn lint
        
  []

  (apply @$.maestro.util/d*shell
         "clj-kondo --parallel --lint"
         (mapcat :extra-paths
                  (-> ($.maestro.required/create-basis)
                      :aliases
                      vals))))
