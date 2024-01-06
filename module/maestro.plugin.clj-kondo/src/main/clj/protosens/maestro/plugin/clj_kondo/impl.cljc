(ns protosens.maestro.plugin.clj-kondo.impl

  ^:no-doc

  #?(:bb (:import (java.io StringWriter)))
  (:require #?(:bb      [babashka.deps :as bb.deps]
               :clj     [protosens.process :as $.process])
            #?(:bb      [pod.borkdude.clj-kondo :as clj-kondo]
               :default [clj-kondo.core         :as clj-kondo])))


;;;;;;;;;;


(defn classpath

  []

  #?(:bb
     (let [writer (StringWriter.)]
       (binding [*out* writer]
         (bb.deps/clojure ["-Spath"]))
       [(str writer)])

     :clj
     (-> ($.process/run ["clojure"
                         "-Spath"])
         (:out)
         (slurp))))



(def run

  clj-kondo/run!)
