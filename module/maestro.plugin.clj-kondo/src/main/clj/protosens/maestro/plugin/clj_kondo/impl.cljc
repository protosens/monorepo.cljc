(ns protosens.maestro.plugin.clj-kondo.impl

  ^:no-doc

  (:require #?(:bb      [pod.borkdude.clj-kondo :as clj-kondo]
               :default [clj-kondo.core         :as clj-kondo])))


;;;;;;;;;;


(def run

  clj-kondo/run!)
