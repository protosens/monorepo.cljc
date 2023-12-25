(ns protosens.maestro.plugin.bb

  (:require [clojure.java.io    :as C.java.io]
            [clojure.pprint     :as C.pprint]
            [protosens.edn.read :as $.edn.read]
            [protosens.maestro  :as $.maestro]))


;;;;;;;;;;


(defn run

  [alias]

  (println)
  (println "---")
  (println)
  (println "[maestro.plugin.bb]")
  (println)
  (println "- Creating `bb.edn` from `bb.maestro.edn` and `deps.maestro.edn`")
  (let [merged (merge ($.edn.read/file "bb.maestro.edn")
                      (-> ($.maestro/-run (str alias)
                                          nil)
                          (::$.maestro/result)
                          (select-keys [:deps
                                        :paths])))]
    (with-open [file (C.java.io/writer "bb.edn")]
      (C.pprint/pprint merged
                       file)))
  (println "- `bb.edn` is ready")
  nil)
