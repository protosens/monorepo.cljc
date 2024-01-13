(ns protosens.maestro.diff

  (:refer-clojure :exclude [print])
  (:require [protosens.deps.edn.diff       :as-alias $.deps.edn.diff]
            [protosens.deps.edn.diff.alias :as       $.deps.edn.diff.alias]
            [protosens.deps.edn.diff.rev   :as-alias $.deps.edn.diff.rev]
            [protosens.maestro.diff        :as-alias $.maestro.diff]
            [protosens.maestro.diff.deps   :as-alias $.maestro.diff.deps]
            [protosens.maestro.diff.rev    :as-alias $.maestro.diff.rev]
            [protosens.maestro.plugin      :as       $.maestro.plugin]
            [protosens.git                 :as       $.git]
            [protosens.term.style          :as       $.term.style]))


(set! *warn-on-reflection*
      true)

;;;;;;;;;;


(defn =definition

  [definition-old definition-new]

  (= (set (:maestro/platform+ definition-old))
     (set (:maestro/platform+ definition-new))))



(defn resolve-rev

  [state kw-rev]

  (when-some [rev (kw-rev state)]
    (or ($.git/resolve rev)
        (throw (ex-info (format "Git revision does not exist: %s"
                                (pr-str rev))
                        {:type                  ::$.maestro.diff.rev/bad
                         ::$.maestro.diff/rev   rev
                         ::$.maestro.diff/state state})))))



(defn init


  ([]

   (init nil))


  ([state]

   (let [rev-old              (or (resolve-rev state
                                               ::$.deps.edn.diff.rev/old)
                                  ($.git/commit-sha 0))
         rev-new              (resolve-rev state
                                           ::$.deps.edn.diff.rev/new)
         deps-maestro-edn-new ($.maestro.plugin/read-deps-maestro-edn rev-new)]
     (-> state
         (assoc ::$.deps.edn.diff.alias/=definition =definition
                ::$.deps.edn.diff.rev/old           rev-old
                ::$.deps.edn.diff/new               deps-maestro-edn-new
                ::$.deps.edn.diff/old               ($.maestro.plugin/read-deps-maestro-edn rev-old))
         ($.deps.edn.diff.alias/init)))))



(defn augmented

  ([]

   (augmented nil))


  ([state]

   (-> state
       (init)
       ($.deps.edn.diff.alias/augmented))))


;;;;;;;;;;


(defn print

  []

  ($.maestro.plugin/intro "protosens.maestro.diff/print")
  ($.maestro.plugin/safe
    (delay
      (let [rev-old             (first *command-line-args*)
            rev-new             (second *command-line-args*)
            _                   ($.maestro.plugin/step (format "Searching for changes in aliases between `%s` and `%s`"
                                                               (or rev-old
                                                                   "HEAD")
                                                               (or rev-new
                                                                   "working tree")))
            diffed              (augmented {::$.deps.edn.diff.rev/old rev-old
                                            ::$.deps.edn.diff.rev/new rev-new})
            added               (diffed ::$.deps.edn.diff.alias/added)
            modified-definition (diffed ::$.deps.edn.diff.alias/modified-definition)
            modified-path+      (diffed ::$.deps.edn.diff.alias/modified-path+)]
        (doseq [[title
                 alias+] [["Added"
                           added]
                          ,
                          ["Modified alias definitions"
                           modified-definition]
                          ,
                          ["Modified files"
                           modified-path+]]
                :when    (seq alias+)]
        ($.maestro.plugin/step (str $.term.style/bold
                                    title
                                    $.term.style/reset))
        (doseq [alias (sort alias+)]
          ($.maestro.plugin/step 1
                                 alias)))
        (if (seq ($.deps.edn.diff.alias/dirty diffed))
          ($.maestro.plugin/fail "Some aliases were modified")
          ($.maestro.plugin/done "No change detected"))))))
