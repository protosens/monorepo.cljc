(ns protosens.graph.dfs)


;;;;;;;;;;


(defn deeper

  [state node+]

  (update state
          ::stack
          conj
          node+))



(defn depth

  [state]

  (dec (count (state ::stack))))



(defn node

  [state]

  (first (peek (state ::stack))))



(defn parent


  ([state]

   (parent state
           nil))


  ([state not-found]

   (let [stack   (state ::stack)
         stack-2 (pop stack)]
     (if (seq stack-2)
       (first (peek stack-2))
       not-found))))



(defn path

  [state]

  (map first
       (state ::stack)))



(defn pending

  [state]

  (pop (state ::stack)))



(defn remaining-sibling+

  [state]

  (rest (peek (state ::stack))))



(defn walk


  ([state enter node+]

   (walk state
         enter
         nil
         node+))


  ([state enter exit node+]

   (let [exit-2 (or exit
                    identity)]
     (loop [exiting? false
            stack    (list node+)
            state-2  state]
       (if (seq stack)
         (if exiting?
           ;;
           ;; Exiting.
           (let [state-3 (exit-2 (assoc state-2
                                        ::stack
                                        stack))
                 stack-2 (state-3 ::stack)]
             (recur false
                    (if (identical? stack-2
                                    stack)
                      (conj (pop stack)
                            (rest (peek stack)))
                      stack-2)
                    state-3))
           ;;
           ;; Entering.
           (if-some [level (seq (peek stack))]
             ;;
             ;; Node to process.
             (let [state-3 (enter (assoc state-2
                                         ::stack
                                         stack))
                   stack-2 (state-3 ::stack)]
               (if (identical? stack-2
                               stack)
                 ;;
                 ;; Cannot go deeper, mark node for exit.
                 (recur true
                        stack
                        state-3)
                 ;;
                 ;; Go deeper.
                 (recur false
                        stack-2
                        state-3)))
             ;;
             ;; Level processed, mark potential parent for exit.
             (recur true
                    (pop stack)
                    state-2)))
         ;;
         ;; End.
         (dissoc state-2
                 ::stack))))))
