(ns protosens.playwright.test.cljs

  (:import (com.microsoft.playwright Browser 
                                     BrowserType
                                     ConsoleMessage
                                     Page
                                     Playwright)
           (java.io File
                    StringWriter)
           (java.util.function Consumer))
  (:require [protosens.term.style :as $.term.style]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;; Public values


(def ^:dynamic *exit-when-done?*

  true)


;;;;;;;;;; Private helpers
;;         Read backwards starting from [[-run]].


(defn- -message->exit-code

  [text]

  (when-some [[_
               str-n-failure
               str-n-error]  (re-find #"(\d+)\s+failures,\s+(\d+)\s+errors\."
                                      text)]
    (if (or (not= str-n-failure
                  "0")
            (not= str-n-error
                  "0"))
      1
      0)))



(defn- -monitor-logging

  [^Page page p*exit-code]

  (let [on-message (reify Consumer
                     (accept [_this console-message]
                         (let [message (.text ^ConsoleMessage console-message)]
                           (println message)
                           (some->> (-message->exit-code message)
                                    (deliver p*exit-code)))))]
    (.onConsoleMessage page
                       on-message)
    page))



(defn -open-page

  [^Browser browser file]

  (let [p*exit-code (promise)
        page        (.newPage browser)]
    (-monitor-logging page
                      p*exit-code)
    (.navigate page
               (format "file://%s"
                       file))
    @p*exit-code))



(defn- -intro

  [browser version]

  (println)
  (println (format "%s%sTesting against %s %s %s"
                   $.term.style/bold
                   $.term.style/fg-cyan
                   browser
                   version
                   $.term.style/reset))
  (println))



(defn- -resolve-file

  [^String file]

  (let [file-2 (File. file)]
    (when-not (.exists file-2)
      (throw (IllegalArgumentException. (format "File not found: %s"
                                                file))))
    (.getCanonicalPath file-2)))



(defn- -browser-type

  ^BrowserType

  [^Playwright playwright kw]

  (case kw
    :chromium (.chromium playwright)
    :firefox  (.firefox playwright)
    :webkit   (.webkit playwright)
    (throw (IllegalArgumentException. (format "Unrecognised browser type: %s"
                                              (pr-str kw))))))


;;;


(defn- -run

  [browser-type file]

  (with-open [playwright (Playwright/create)]
    (let [browser-type (-browser-type playwright
                                      browser-type)
          file-2       (-resolve-file file)
          browser      (.launch browser-type)]
        (-intro (.name browser-type)
                (.version browser))
        (-open-page browser
                    file-2))))


;;;;;;;;;; Core public functions


(defn- -terminate

  [exit-code]

  (cond->
    exit-code
    *exit-when-done?*
    (System/exit)))



(defn- -capture-output

  [delayed]

  (binding [*out* (StringWriter.)]
    [@delayed
     (str *out*)]))



(defn all

  [{:keys [file]}]

  ;; Playwright is not thread-safe but it is fine creating 1 runtime / thread.

  (let [f*browser+ (mapv (fn [browser-type]
                           (future
                             (-capture-output
                               (delay
                                 (-run browser-type
                                       file)))))
                         [:chromium
                          :firefox
                          :webkit])]
    (let [output+ (map (comp second
                             deref)
                       f*browser+)]
      (println (first output+))
      (doseq [output (rest output+)]
        (println)
        (println "──────────")
        (println)
        (println output)))
    (let [exit-code+ (map (comp first
                                deref)
                          f*browser+)
          exit-code  (or (some #(= %
                                   1)
                               exit-code+)
                         0)]
      (-terminate exit-code))))



(defn chromium

  [{:keys [file]}]

  (-> (-run :chromium
            file)
      (-terminate)))



(defn firefox

  [{:keys [file]}]

  (-> (-run :firefox
            file)
      (-terminate)))



(defn webkit

  [{:keys [file]}]

  (-> (-run :webkit
            file)
      (-terminate)))


;;;;;;;;;; Dev


(comment


  ;; Must compile CLJS tests first.


  (binding [*exit-when-done?* false]
    (all {:file "test/playwright.html"}))

  (binding [*exit-when-done?* false]
    (chromium {:file "test/playwright.html"}))

  (binding [*exit-when-done?* false]
    (firefox {:file "test/playwright.html"}))

  (binding [*exit-when-done?* false]
    (webkit {:file "test/playwright.html"}))


  )
