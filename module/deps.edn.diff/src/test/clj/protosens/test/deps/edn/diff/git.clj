(ns protosens.test.deps.edn.diff.git

  (:require [clojure.test                      :as       T]
            [protosens.deps.edn.diff.git       :as       $.deps.edn.diff.git]
            [protosens.deps.edn.diff.rev       :as-alias $.deps.edn.diff.rev]
            [protosens.test.util.deps.edn.diff :as       $.test.util.deps.edn.diff]
            [protosens.path                    :as       $.path]))


;;;;;;;;;;


(T/deftest path+

  ($.test.util.deps.edn.diff/with-touched-path+
    (fn [dir+ file+]
      (T/is (= (map $.path/from-string
                    file+)
               ($.deps.edn.diff.git/path+ {::$.deps.edn.diff.rev/old "HEAD"}
                                          dir+))))))
