(ns protosens.maestro.plugin.bb

  (:import (java.io StringWriter))
  (:refer-clojure :exclude [sync])
  (:require [clojure.java.io          :as C.java.io]
            [clojure.pprint           :as C.pprint]
            [protosens.maestro        :as $.maestro]
            [protosens.maestro.plugin :as $.maestro.plugin]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;; Private side effects


(defn- -read-bb-edn

  []

  ($.maestro.plugin/read-file-edn "bb.edn"))



(defn- -write-bb-edn

  [bb-edn tree-string]

  (println tree-string)
  ($.maestro.plugin/step "Writing new `bb.edn`")
  (with-open [file (C.java.io/writer "bb.edn")]
    (C.pprint/pprint bb-edn
                 file))
  ($.maestro.plugin/done "Ready to use Babashka"))


;;;;;;;;;; Task


(defn ^:no-doc -sync

  [node bb-edn bb-maestro-edn deps-maestro-edn]

  (let [tree-stream (StringWriter.)
        maestro-run (binding [*out*                          tree-stream
                              $.maestro.plugin/*print-path?* true]
                      ($.maestro/run [node]
                                     deps-maestro-edn))
        bb-edn-new  (-> bb-maestro-edn
                        (update :tasks
                                (partial into
                                         (sorted-map)))
                        (merge (-> maestro-run
                                   (::$.maestro/deps-edn)
                                   (select-keys [:deps
                                                 :paths]))))]
    (when-not (= bb-edn-new
                 bb-edn)
      [bb-edn-new
       (str tree-stream)])))



(defn- -sync-from-task

  [node deps-maestro-edn]

  (-sync node
         (-read-bb-edn)
         ($.maestro.plugin/read-file-edn "bb.maestro.edn")
         (or deps-maestro-edn
             ($.maestro.plugin/read-deps-maestro-edn))))



(defn check

  [node]

  ($.maestro.plugin/intro "maestro.plugin.bb/check")
  ($.maestro.plugin/step "Checking if `bb.edn` is in sync with `bb.maestro.edn` and `deps.maestro.edn`")
  ($.maestro.plugin/safe
    (delay
      (if (-sync-from-task node
                           nil)
        ($.maestro.plugin/fail "`bb.edn` is not in sync")
        ($.maestro.plugin/done "`bb.edn` is in sync")))))



(defn sync


  ([node]

   (sync node
         nil))


  ([node deps-maestro-edn]

   ($.maestro.plugin/intro "maestro.plugin.bb/sync")
   ($.maestro.plugin/step "Syncing `bb.edn` with `bb.maestro.edn` and `deps.maestro.edn`")
   ($.maestro.plugin/safe
     (delay
       ($.maestro.plugin/step (format "Selecting everything required for node `%s`"
                                      node))
       (if-some [[bb-edn
                  tree-string] (-sync-from-task node
                                                deps-maestro-edn)]
         (-write-bb-edn bb-edn
                        tree-string)
         ($.maestro.plugin/done "Nothing changed"))))))
