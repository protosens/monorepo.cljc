(ns protosens.process

  (:require [babashka.process :as bb.process]))


;;;;;;;;;; Launching processes


(defn shell


  ([arg+]

   (shell arg+
          nil))


  ([arg+ option+]

   (bb.process/process arg+
                       (merge {:err      :inherit
                               :in       :inherit
                               :out      :inherit
                               :shutdown bb.process/destroy-tree}
                              option+))))


;;;;;;;;;; Feedback on processes


(defn exit-code

  [process]

  (:exit (deref process)))



(defn success?

  [process]

  (zero? (exit-code process)))
