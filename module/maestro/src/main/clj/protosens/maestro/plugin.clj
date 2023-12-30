(ns protosens.maestro.plugin

  (:require [clojure.string       :as C.string]
            [protosens.term.style :as $.term.style]))


(set! *warn-on-reflection*
      true)


(declare fail)


;;;;;;;;;; Private


(def ^:private -*first-intro?

  (atom true))



(defn- -babashka?

  []

  (some? (System/getProperty "babashka.version")))


;;;;;;;;;; Public


(def ^:dynamic *print-path?*

  false)


;;;


(defn done

  [message]

  (println)
  (println (str $.term.style/bold
                $.term.style/fg-green
                "[✓] "
                $.term.style/reset
                $.term.style/fg-green
                message
                $.term.style/fg-green
                $.term.style/reset)))



(defn fail


  ([message]

   (fail message
         nil))


  ([^String message ^Throwable exception-cause]

  (if (-babashka?)
    (do
      (println)
      (when exception-cause
        (.printStackTrace exception-cause)
        (println))
      (println (str $.term.style/bold
                    $.term.style/fg-red
                    "[x] "
                    $.term.style/reset
                    $.term.style/fg-red
                    message
                    $.term.style/reset))
      (System/exit 1))
    (throw (Exception. message
                       exception-cause)))))



(defn intro

  [plugin-name]

  (when-not (-> (reset-vals! -*first-intro?
                             false)
                (first))
    (println)
    (println "──────────"))
  (println)
  (println (str $.term.style/bold
                $.term.style/fg-cyan
                "["
                plugin-name
                "]"
                $.term.style/reset))
  (println))



(defn safe

  [delayed]

  (try
    @delayed
   (catch Throwable ex
     (fail "An unforeseen exception occured"
           ex))))



(defn step


  ([message]

   (step nil
         message))


  ([level message]

   (println (str (C.string/join (repeat (or level
                                            0)
                                        "    "))
                 $.term.style/fg-yellow
                 "• "
                 $.term.style/reset
                 message))))
