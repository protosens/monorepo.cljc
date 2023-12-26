(ns protosens.maestro.plugin.bb

  (:require [clojure.java.io        :as C.java.io]
            [clojure.pprint         :as C.pprint]
            [clojure.string         :as C.string]
            [protosens.edn.read     :as $.edn.read]
            [protosens.maestro      :as-alias $.maestro]
            [protosens.maestro.walk :as $.maestro.walk]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;; Private side effects


(defn- -print-path

  [path]

  (println "- Done, prepared `bb.edn` with aliases:")
  (println)
  (doseq [[alias
           depth] path]
    (println (format "%s%s"
                     (C.string/join (repeat (inc depth)
                                            "  "))
                     alias))))



(defn- -write-file

  [bb-edn path]

  (println "- Writing new `bb.edn`")
  (with-open [file (C.java.io/writer "bb.edn")]
    (C.pprint/pprint bb-edn
                 file))
  (-print-path path))


;;;;;;;;;; Task


(defn ^:no-doc -run

  [alias bb-edn bb-maestro-edn deps-maestro-edn]

  (let [sorted     ($.maestro.walk/run [alias]
                                       deps-maestro-edn)
        bb-edn-new (merge bb-maestro-edn
                          (-> sorted
                              (::$.maestro/deps-edn)
                              (select-keys [:deps
                                            :paths])))]
    (when-not (= bb-edn-new
                 bb-edn)
      [bb-edn-new
       (sorted ::$.maestro/path)])))



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
  (if-some [[bb-edn
             path]  (-run alias
                          ($.edn.read/file "bb.edn")
                          ($.edn.read/file "bb.maestro.edn")
                          ($.edn.read/file "deps.maestro.edn"))]
    (-write-file bb-edn
                 path)
    (println "- Done, nothing changed")))
