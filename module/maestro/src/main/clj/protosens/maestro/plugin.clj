(ns protosens.maestro.plugin)


;;;;;;;;;;


(defn fail

  [^String message]

  (if (System/getProperty "babashka.version")
    (do
      (println "[ERROR]"
               message)
      (System/exit 1))
    (throw (Exception. message))))
