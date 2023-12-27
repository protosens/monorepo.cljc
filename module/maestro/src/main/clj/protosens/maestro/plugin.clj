(ns protosens.maestro.plugin)


;;;;;;;;;; Private


(def ^:private -*first-intro?

  (atom true))


;;;;;;;;;;


(defn done

  [message]

  (println)
  (println (format "\033[1m\033[32m[✓]\033[0m \033[32m%s\033[0m"
                   message)))



(defn fail

  [^String message]

  (if (System/getProperty "babashka.version")
    (do
      (println)
      (println (format "\033[1m\033[31m[x]\033[0m \033[31m%s\033[0m"
                       message))
      (System/exit 1))
    (throw (Exception. message))))



(defn intro

  [plugin-name]

  (when-not (-> (reset-vals! -*first-intro?
                             false)
                (first))
    (println)
    (println "──────────"))
  (println)
  (println (format "\033[1m\033[36m[%s]\033[0m"
                   plugin-name))
  (println))
