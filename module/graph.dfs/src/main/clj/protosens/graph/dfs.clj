(ns protosens.graph.dfs)


;;;;;;;;;;


(defn deeper

  [state node+]

  (assoc state
         ::level
         node+))



(defn depth

  [state]

  (dec (count (state ::stack))))



(defn pending

  [state]

  (rest (state ::stack)))



(defn sibling+

  [state]

  (peek (state ::stack)))



(defn walk

  [state enter exit node+]

  (loop [state-2 (assoc state
                        ::level (seq node+)
                        ::stack '())]
    (if-some [level (seq (state-2 ::level))]
      (let [node (first level)]
        (-> state-2
            (dissoc ::level)
            (update ::stack
                    conj
                    (rest level))
            (enter node)
            (recur)))
      (if-some [stack (seq (state-2 ::stack))]
        (-> state-2
            (assoc ::level (peek stack)
                   ::stack (pop stack))
            (exit)
            (recur))
        state-2))))
