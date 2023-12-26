(ns protosens.maestro.plugin.bb

  (:require [clojure.java.io    :as C.java.io]
            [clojure.pprint     :as C.pprint]
            [clojure.string     :as C.string]
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
  (println (format "- Computing everything required for alias `%s`"
                   alias))
  (let [sorted ($.maestro/-run (str alias)
                               nil)
        bb-edn (merge ($.edn.read/file "bb.maestro.edn")
                      (-> sorted
                          (::$.maestro/result)
                          (select-keys [:deps
                                        :paths])))]
    (if (= bb-edn
           ($.edn.read/file "bb.edn"))
      (println "- Done, nothing changed")
      (do
        (with-open [file (C.java.io/writer "bb.edn")]
          (C.pprint/pprint bb-edn
                       file))
        (println "- Prepared `bb.edn` with aliases:")
        (doseq [[alias
                 depth] (sorted ::$.maestro/path)]
          (println (format "%s%s"
                           (C.string/join (repeat (inc depth)
                                                  "  "))
                           alias)))
        (println "- Done, `bb.edn` is ready"))))
  nil)
