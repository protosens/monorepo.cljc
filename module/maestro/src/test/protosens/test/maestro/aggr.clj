(ns protosens.test.maestro.aggr

  (:refer-clojure :exclude [alias])
  (:require [clojure.test           :as T]
            [protosens.maestro.aggr :as $.maestro.aggr]))


;;;;;;;;;;


(T/deftest alias

  (T/is (= {:maestro/require [:foo]}
           ($.maestro.aggr/alias {:maestro/require []}
                                 :foo))))


(T/deftest env

  (T/is (= {:maestro/env {}}
           ($.maestro.aggr/env {:maestro/env {}}
                               {:maestro/env {}}))
        "Nothing to merge")

  (T/is (= {:maestro/env {"a" "A"}}
           ($.maestro.aggr/env {:maestro/env {}}
                               {:maestro/env {"a" "A"}}))
        "Merging env for the first time")

  (T/is (= {:maestro/env {"a" "A"
                          "b" "B"}}
           ($.maestro.aggr/env {:maestro/env {"a" "A"}}
                               {:maestro/env {"b" "B"}}))
        "Merging into existing"))


(T/deftest default

  (T/is (= {:aliases         {:foo {:maestro/env {"a" "A"}}}
            :maestro/env     {"a" "A"}
            :maestro/require [:foo]}
           ($.maestro.aggr/default {:aliases         {:foo {:maestro/env {"a" "A"}}}
                                    :maestro/env     {}
                                    :maestro/require []}
                                   :foo))))
