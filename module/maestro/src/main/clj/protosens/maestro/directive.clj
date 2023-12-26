(ns protosens.maestro.directive

  (:require [protosens.maestro        :as-alias $.maestro]
            [protosens.maestro.plugin :as       $.maestro.plugin]))


;;;;;;;;;;


(defmulti run
          (fn [_state nspace _nm]
            nspace))



(defmethod run
           :default

  [state nspace nm]

  (when nm
    ($.maestro.plugin/fail (format "%s/%s is neither an existing alias nor a directive"
                                   nspace
                                   nm)))
  (update state
          ::$.maestro/include
          conj
          nspace))


;;;


(defmethod run
           "GOD"

  [state nspace nm]

  (when nm
    ($.maestro.plugin/fail (format "`:GOD` directive should not be namespaced: `%s`"
                                   (keyword nspace
                                            nm))))
  (update state
          ::$.maestro/level
          (fn [level]
            (let [alias+ (keys (get-in state
                                       [::$.maestro/deps-maestro-edn
                                        :aliases]))]
              (concat (sort (into #{}
                                  (comp (keep namespace)
                                        (map keyword))
                                  alias+))
                      (sort alias+)
                      level)))))



(defmethod run
           "EVERY"

  [state _nspace nm]

  (cond->
    state
    nm
    (update ::$.maestro/level
            (fn [level]
              (cons (keyword nm)
                    (concat (sort (filter (fn [alias]
                                            (= (namespace alias)
                                               nm))
                                          (keys (get-in state
                                                        [::$.maestro/deps-maestro-edn
                                                         :aliases]))))
                            level))))))



(defmethod run
           "SHALLOW"

  [state _nspace nm]

  (cond->
    state
    nm
    (-> (update ::$.maestro/exclude
                conj
                nm)
        (update ::$.maestro/include
                disj
                nm))))
