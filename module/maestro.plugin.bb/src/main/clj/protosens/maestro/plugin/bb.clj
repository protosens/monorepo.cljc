(ns protosens.maestro.plugin.bb

  (:import (java.io StringWriter))
  (:refer-clojure :exclude [sync])
  (:require [clojure.java.io          :as C.java.io]
            [clojure.pprint           :as C.pprint]
            [clojure.string           :as C.string]
            [protosens.edn.read       :as $.edn.read]
            [protosens.maestro        :as $.maestro]
            [protosens.maestro.plugin :as $.maestro.plugin]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;; Private side effects


(defn- -read-file

  [path]

  (try
    ($.edn.read/file path)
    (catch Throwable ex
      ($.maestro.plugin/fail (format "Unable to read file `%s`"
                                     path)
                             ex))))



(defn- -write-bb-edn

  [bb-edn tree-string]

  (println tree-string)
  ($.maestro.plugin/step "Writing new `bb.edn`")
  (with-open [file (C.java.io/writer "bb.edn")]
    (C.pprint/pprint bb-edn
                 file))
  ($.maestro.plugin/done "Ready to use Babashka"))


;;;;;;;;;; Task


(defn ^:no-doc -run

  [node bb-edn bb-maestro-edn deps-maestro-edn]

  (let [tree-stream (StringWriter.)
        sorted      (binding [*out*                          tree-stream
                              $.maestro.plugin/*print-path?* true]
                      ($.maestro/run [node]
                                     deps-maestro-edn))
        bb-edn-new  (merge bb-maestro-edn
                           (-> sorted
                               (::$.maestro/deps-edn)
                               (select-keys [:deps
                                             :paths])))]
    (when-not (= bb-edn-new
                 bb-edn)
      [bb-edn-new
       (str tree-stream)])))



(defn- -run-from-task

  [node]

  (-run node
        (-read-file "bb.edn")
        (-read-file "bb.maestro.edn")
        (-read-file "deps.maestro.edn")))



(defn check

  [node]

  ($.maestro.plugin/intro "maestro.plugin.bb/check")
  ($.maestro.plugin/step "Checking if `bb.edn` is in sync with `bb.maestro.edn` and `deps.maestro.edn`")
  ($.maestro.plugin/safe
    (delay
      (if (-run-from-task node)
        ($.maestro.plugin/fail "`bb.edn` is not in sync")
        ($.maestro.plugin/done "`bb.edn` is in sync")))))



(defn sync

  [node]

  ($.maestro.plugin/intro "maestro.plugin.bb/sync")
  ($.maestro.plugin/step "Syncing `bb.edn` with `bb.maestro.edn` and `deps.maestro.edn`")
  ($.maestro.plugin/safe
    (delay
      ($.maestro.plugin/step (format "Selecting everything required for node `%s`"
                                     node))
      (if-some [[bb-edn
                 tree-string] (-run-from-task node)]
        (-write-bb-edn bb-edn
                       tree-string)
        ($.maestro.plugin/done "Nothing changed")))))
