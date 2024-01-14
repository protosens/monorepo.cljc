(ns protosens.maestro.plugin.bb

  (:import (java.io StringWriter))
  (:refer-clojure :exclude [sync])
  (:require [clojure.java.io             :as       C.java.io]
            [clojure.pprint              :as       C.pprint]
            [protosens.deps.edn          :as       $.deps.edn]
            [protosens.maestro           :as       $.maestro]
            [protosens.maestro.alias     :as       $.maestro.alias]
            [protosens.maestro.plugin    :as       $.maestro.plugin]
            [protosens.maestro.plugin.bb :as-alias $.maestro.plugin.bb]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;; Private side effects


(defn- -read-bb-edn

  [state]

  (assoc state
         ::$.maestro.plugin.bb/bb.edn
         ($.maestro.plugin/read-file-edn "bb.edn")))



(defn- -write-bb-edn

  [state]

  (println (state ::$.maestro.plugin.bb/tree))
  ($.maestro.plugin/step "Writing new `./bb.edn`")
  (with-open [file (C.java.io/writer "./bb.edn")]
    (C.pprint/pprint (state ::$.maestro.plugin.bb/bb.edn)
                     file))
  ($.maestro.plugin/done "Ready to use Babashka"))


;;;;;;;;;; Task


(defn ^:no-doc -sync

  [state]

  (let [tree-stream   (StringWriter.)
        deps-edn      (state ::$.maestro/deps.edn)
        node          (state ::$.maestro.plugin.bb/node)
        maestro-state (binding [*out*                          tree-stream
                                $.maestro.plugin/*print-path?* true]
                        ($.maestro/run [node]
                                       deps-edn))
        alias+        ($.maestro.alias/accepted maestro-state)
        bb-edn-new    (-> (state ::$.maestro.plugin.bb/bb.maestro.edn)
                          (update :tasks
                                  (partial into
                                           (sorted-map)))
                          (merge (-> ($.deps.edn/flatten deps-edn
                                                         alias+)
                                     (select-keys [:deps
                                                   :paths]))))]
    (if (= bb-edn-new
           (state ::$.maestro.plugin.bb/bb.edn))
      (dissoc state
              ::$.maestro.plugin.bb/bb.edn)
      (assoc state
             ::$.maestro.plugin.bb/bb.edn bb-edn-new
             ::$.maestro.plugin.bb/tree   (str tree-stream)))))



(defn- -sync-from-task

  [node]

  (-> {::$.maestro.plugin.bb/bb.edn         ($.maestro.plugin/read-file-edn "./bb.edn")
       ::$.maestro.plugin.bb/bb.maestro.edn ($.maestro.plugin/read-file-edn "./bb.maestro.edn")
       ::$.maestro.plugin.bb/node           node
       ::$.maestro/deps.edn                 ($.maestro.plugin/read-deps-edn)}
      (-sync)))



(defn check

  [node]

  ($.maestro.plugin/intro "maestro.plugin.bb/check")
  ($.maestro.plugin/step "Checking if `bb.edn` is in sync with `bb.maestro.edn` and `deps.maestro.edn`")
  ($.maestro.plugin/safe
    (delay
      (let [state (-sync-from-task node)]
        (if (state ::$.maestro.plugin.bb/bb.edn)
          ($.maestro.plugin/fail "`bb.edn` is not in sync")
          ($.maestro.plugin/done "`bb.edn` is in sync"))
        state))))



(defn sync

  [node]

  ($.maestro.plugin/intro "maestro.plugin.bb/sync")
  ($.maestro.plugin/step "Syncing `bb.edn` with `bb.maestro.edn` and `deps.edn`")
  ($.maestro.plugin/safe
    (delay
      ($.maestro.plugin/step (format "Selecting everything required for node `%s`"
                                     node))
      (let [state (-sync-from-task node)]
        (if (state ::$.maestro.plugin.bb/bb.edn)
          (-write-bb-edn state)
          (do
            ($.maestro.plugin/done "Nothing changed")
            state))))))
