(ns protosens.maestro.util

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
