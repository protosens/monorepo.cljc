(ns protosens.deps.edn.diff.git

  (:require [protosens.deps.edn.diff.rev :as-alias $.deps.edn.diff.rev]
            [protosens.git               :as       $.git]
            [protosens.path              :as       $.path]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;;


(defn path+

  [state path+]

  (map (comp $.path/normalized
             $.path/from-string)
       ($.git/diff-path+ (state ::$.deps.edn.diff.rev/old)
                         (state ::$.deps.edn.diff.rev/new)
                         path+)))
