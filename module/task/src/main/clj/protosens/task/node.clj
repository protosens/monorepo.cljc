(ns protosens.task.node

  (:require [protosens.task :as $.task]))


;;;;;;;;;;


(defn run

  [js-file]

  ($.task/shell ["node"
                 js-file]))
