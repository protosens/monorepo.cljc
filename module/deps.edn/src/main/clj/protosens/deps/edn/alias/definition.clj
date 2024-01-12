(ns protosens.deps.edn.alias.definition

  (:require [protosens.path :as $.path]))


;;;;;;;;;;


(defn extra-dep+

  [definition]

  (not-empty (:extra-deps definition)))



(defn normalized-extra-path+

  [definition]

  ($.path/normalized+ (map $.path/from-string
                           (:extra-paths definition))))


;;;;;;;;;;


(defn =extra-dep+

  [definition-a definition-b]

  (= (extra-dep+ definition-a)
     (extra-dep+ definition-b)))



(defn =extra-path+

  [definition-a definition-b]

  (= (normalized-extra-path+ definition-a)
     (normalized-extra-path+ definition-b)))
