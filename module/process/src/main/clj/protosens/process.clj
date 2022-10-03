(ns protosens.process

  (:refer-clojure :exclude [await])
  (:require [babashka.process :as bb.process]
            [clojure.string   :as string]))


;;;;;;;;;; Launching processes


(defn shell


  ([command]

   (shell command
          nil))


  ([command option+]

   (bb.process/process command
                       (merge {:err      :inherit
                               :in       :inherit
                               :out      :inherit
                               :shutdown bb.process/destroy-tree}
                              option+))))



(defn run


  ([command]
   
   (run command
        nil))


  ([command option+]

   (bb.process/process command
                       (merge {:shutdown bb.process/destroy-tree}
                              option+))))


;;;;;;;;;; Feedback on processes


(defn- -slurp

  ;;

  [process k]

  (-> (get process
           k)
      (slurp)
      (string/trimr)
      (not-empty)))



(defn await 

  [process]

  (deref process))



(defn err

  [process]

  (-slurp process
          :err))



(defn exit-code

  [process]

  (:exit (await process)))



(defn out

  [process]

  (-slurp process
          :out))



(defn success?

  [process]

  (zero? (exit-code process)))
