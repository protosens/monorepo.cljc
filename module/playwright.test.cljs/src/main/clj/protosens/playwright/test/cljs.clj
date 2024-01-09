(ns protosens.playwright.test.cljs

  (:import (com.microsoft.playwright BrowserType
                                     ConsoleMessage
                                     Playwright)
           (java.io File)
           (java.util.function Consumer))
  (:require [clojure.string :as C.string]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;;


(defn- -run

  [playwright->browser-type ^String file]

  (with-open [playwright (Playwright/create)]
    (let [^BrowserType browser-type (playwright->browser-type playwright)
                       p*end        (promise)
                       file-2       (-> file
                                        (File.)
                                        (.getCanonicalPath))]
      (with-open [browser (.launch browser-type)
                  page    (.newPage browser)]

        (println)
        (println (format "Testing against %s %s with file `%s`"
                         (.name browser-type)
                         (.version browser)
                         file-2))
        (println)
        (.onConsoleMessage page
                           (reify Consumer
                             (accept [_this message]
                                 (let [text (.text ^ConsoleMessage message)]
                                   (println text)
                                   (when-some [[_
                                                str-n-failure
                                                str-n-error]   (re-find #"(\d+)\s+failures,\s+(\d+)\s+errors\."
                                                                        text)]
                                       (let [exit-code (if (or (not= str-n-failure
                                                                     "0")
                                                               (not= str-n-error
                                                                     "0"))
                                                           1
                                                           0)]
                                           (deliver p*end
                                                    exit-code)))))))
        (.navigate page
                   (format "file://%s"
                           file-2))
        (System/exit @p*end)))))


;;;


(defn chromium

  [{:keys [file]}]

  (-run (fn [^Playwright p]
          (.chromium p))
        file))



(defn firefox

  [{:keys [file]}]

  (-run (fn [^Playwright p]
          (.firefox p))
        file))



(defn webkit

  [{:keys [file]}]

  (-run (fn [^Playwright p]
          (.webkit p))
        file))


;;;


(defn -main

  [& [file]]

  (chromium {:file file}))


;;;;;;;;;;


(comment

  (firefox {:file "private/tmp/browser_test/index.html"})
  (chromium {:file "test_playwright.html"})
  (webkit {:file "private/tmp/browser_test/index.html"})

  )
