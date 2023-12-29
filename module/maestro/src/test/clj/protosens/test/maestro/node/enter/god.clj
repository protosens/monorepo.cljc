(ns protosens.test.maestro.node.enter.god

  (:require [clojure.test           :as    T]
            [protosens.maestro      :as    $.maestro]
            [protosens.test.maestro :refer [-t-path]]))


;;;;;;;;;;


(T/deftest enter

  (-t-path "Everything is required"
           [:GOD]
           {:m/a {:maestro/require [:m/b
                                    :t/a]}
            :m/b {:maestro/require [:t/b]}
            :t/a {}
            :t/b {}}
           [[:GOD 0] [:m 1] [:t 1] [:m/a 1] [:m/b 2] [:t/b 3] [:t/a 2]]))
