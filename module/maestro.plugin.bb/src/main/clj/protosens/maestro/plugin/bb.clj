(ns protosens.maestro.plugin.bb

  (:import (java.io StringWriter))
  (:require [clojure.java.io          :as C.java.io]
            [clojure.pprint           :as C.pprint]
            [clojure.string           :as C.string]
            [protosens.edn.read       :as $.edn.read]
            [protosens.maestro        :as $.maestro]
            [protosens.maestro.plugin :as $.maestro.plugin]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;; Private side effects


(defn- -write-file

  [bb-edn path]

  (println path)
  ($.maestro.plugin/step "Writing new `bb.edn`")
  (with-open [file (C.java.io/writer "bb.edn")]
    (C.pprint/pprint bb-edn
                 file))
  ($.maestro.plugin/done "Ready to use Babashka"))


;;;;;;;;;; Task


(defn ^:no-doc -run

  [alias bb-edn bb-maestro-edn deps-maestro-edn]

  (let [path       (StringWriter.)
        sorted     (binding [*out*                          path
                             $.maestro.plugin/*print-path?* true]
                     ($.maestro/run [alias]
                                    deps-maestro-edn))
        bb-edn-new (merge bb-maestro-edn
                          (-> sorted
                              (::$.maestro/deps-edn)
                              (select-keys [:deps
                                            :paths])))]
    (when-not (= bb-edn-new
                 bb-edn)
      [bb-edn-new
       (str path)])))



(defn run

  [alias]

  ($.maestro.plugin/intro "maestro.plugin.bb")
  ($.maestro.plugin/step "Computing new `bb.edn` from `bb.maestro.edn` and `deps.maestro.edn`")
  ($.maestro.plugin/step (format "Selecting everything required for alias `%s`"
                                 alias))
  (if-some [[bb-edn
             path]  (-run alias
                          ($.edn.read/file "bb.edn")
                          ($.edn.read/file "bb.maestro.edn")
                          ($.edn.read/file "deps.maestro.edn"))]
    (-write-file bb-edn
                 path)
    ($.maestro.plugin/done "Nothing changed")))
