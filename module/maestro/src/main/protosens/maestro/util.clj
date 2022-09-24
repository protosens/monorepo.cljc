(ns protosens.maestro.util

  "Utilities needed by Maestro.
  
   Not meant for regular users."

  {:no-doc true})


;;;;;;;;;; Map operations


(defn append-at

  [hmap k x+]

  (update hmap
          k
          (fn [x-old+]
            (into (vec x-old+)
                  x+))))


(defn prepend-at

  [hmap k x+]

  (update hmap
          k
          (fn [x-old+]
            (into (vec x+)
                  x-old+))))


;;;;;;;;;; Processes


(defn- -require-for-bb

  ;;
  
  [sym]

  (try
    (requiring-resolve sym)
    (catch Exception _ex
      (throw (RuntimeException. "This currently only work in Babashka")))))



(def d*clojure

  "Delay for `babahska.tasks/clojure`.
   
   <!> Only works in Babashka."

  (delay
    (-require-for-bb 'babashka.tasks/clojure)))



(def d*shell

  "Delay for `babahska.tasks/shell`.
   
   <!> Only works in Babashka."

  (delay
    (-require-for-bb 'babashka.tasks/shell)))
