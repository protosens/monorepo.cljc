(ns protosens.maestro.plugin

  (:require [clojure.string       :as C.string]
            [protosens.edn.read   :as $.edn.read]
            [protosens.git        :as $.git]
            [protosens.term.style :as $.term.style]))


(set! *warn-on-reflection*
      true)


(declare fail)


;;;;;;;;;; Private


(def ^:no-doc ^:dynamic -*exit-on-fail?*

  (some? (System/getProperty "babashka.version")))



(def ^:private -*first-intro?

  (atom true))


;;;;;;;;;; Core utilities for plugins


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
                $.term.style/reset))
  (println))



(defn fail


  ([message]

   (fail message
         nil))


  ([^String message ^Throwable exception-cause]

  (if -*exit-on-fail?*
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
      (println)
      (System/exit 1))
    (throw (ex-info message
                    {:type ::failure}
                    exception-cause)))))



(defn intro

  [plugin-name]

  (when-not (-> (reset-vals! -*first-intro?
                             false)
                (first))
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


;;;;;;;;;; Reading EDN files


(defn read-file-edn

  [path]

  (try
    ($.edn.read/file path)
    (catch Throwable ex
      (fail (format "Unable to read file `%s`"
                    path)
            ex))))


;;;


(defn read-deps-edn


  ([]

   (read-deps-edn nil))


  ([rev]

   (let [path "./deps.edn"]
     (if rev
       (-> ($.git/show-file rev
                            path)
            (slurp)
            ($.edn.read/string))
       (read-file-edn path)))))
