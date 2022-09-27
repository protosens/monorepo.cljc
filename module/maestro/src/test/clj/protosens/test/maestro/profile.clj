(ns protosens.test.maestro.profile
  
  (:require [clojure.test              :as T]
            [protosens.maestro.profile :as $.maestro.profile]))


;;;;;;;;;;


(T/deftest append+

  (T/is (= {:maestro/profile+ '[a b]}
           ($.maestro.profile/append+ {}
                                      '[a b]))
        "No existing profile")

  (T/is (= {:maestro/profile+ '[a b c d]}
           ($.maestro.profile/append+ {:maestro/profile+ '[a b]}
                                      '[c d]))
        "To existing profiles"))


(T/deftest prepend+

  (T/is (= {:maestro/profile+ '[a b]}
           ($.maestro.profile/prepend+ {}
                                       '[a b]))
        "No existing profile")

  (T/is (= {:maestro/profile+ '[c d a b]}
           ($.maestro.profile/prepend+ {:maestro/profile+ '[a b]}
                                       '[c d]))
        "To existing profiles"))
