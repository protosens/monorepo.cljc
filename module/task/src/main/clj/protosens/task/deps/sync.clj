(ns protosens.task.deps.sync

  ;; TODO. Clearly the core idea should be refactored into a separate module.

  (:require [babashka.fs                        :as bb.fs]
            [protosens.edn.read                 :as $.edn.read]
            [protosens.maestro.plugin           :as $.maestro.plugin]
            [protosens.maestro.plugin.bb        :as $.maestro.plugin.bb]
            [protosens.maestro.plugin.clj-kondo :as $.maestro.plugin.clj-kondo]
            [protosens.path                     :as $.path]))


;;;;;;;;;;


(defn init

  [dir-cache patch]

  {::cache    dir-cache
   ::changed? {}
   ::commit   {}
   ::patch    patch})



(defn read-file

  [path]

  (try
    ($.edn.read/file path)
    (catch Exception _ex
      nil)))



(defn read-target

  [path]

  (or (read-file path)
      ($.maestro.plugin/fail (format "Problem while reading target file `%s` as EDN"
                                     path))))



(defn path-cache

  [state path]

  (format "%s/%s"
          (state ::cache)
          path))



(defn read-cache

  [state path]

  (read-file (path-cache state
                         path)))



(defn path+

  [state]

  (into #{}
        (mapcat second)
        (state ::patch)))



(defn changed?

  [state]

  (reduce (fn [state-2 path]
            (if (contains? (state-2 ::changed?)
                           path)
              state-2
              (let [target (read-target path)
                    cached (read-cache state
                                       path)]
                (if (= cached
                       target)
                  (assoc-in state-2
                            [::changed?
                             path]
                            false)
                  (-> state-2
                      (assoc-in [::changed?
                                 path]
                                true)
                      (assoc-in [::commit
                                 path]
                                target))))))
          state
          (path+ state)))



(defn action+

  [state]

  (let [changed? (state ::changed?)]
    (keep (fn [[action path+]]
            (when (some changed?
                        path+)
              action))
          (state ::patch))))



(defn sync-bb

  []

  ($.maestro.plugin.bb/sync :module/task))



(defn lint-prepare

  []

  (binding [*command-line-args* [":GOD"]]
    ($.maestro.plugin.clj-kondo/prepare)))



(defn commit

  [state]

  ($.maestro.plugin/intro "protosens.task.deps/commit")
  ($.maestro.plugin/safe
    (delay
      (if (not-empty (state ::commit))
        (do
          ($.maestro.plugin/step "Caching state for diffing next time")
          (doseq [[path
                   edn] (state ::commit)
                  :let  [path-2 (path-cache state
                                            path)]]
            ($.maestro.plugin/step 1
                                   (apply format "`./%s`  ->  `./%s`"
                                          (map (comp str
                                                     $.path/normalized
                                                     $.path/from-string)
                                               [path
                                                path-2])))
            (-> path-2
                (bb.fs/parent)
                (bb.fs/create-dirs))
            (spit path-2
                  edn)))
        ($.maestro.plugin/step "Nothing to cache"))
      ($.maestro.plugin/done "Done syncing"))))



(defn run

  []

  ($.maestro.plugin/intro "protosens.task.deps/sync")
  ($.maestro.plugin/safe
    (delay
      ($.maestro.plugin/step "Computing what needs to sync with `deps.edn`")
      (let [state (-> (init "./private/tmp/sync/"
                            [[sync-bb
                              ["./bb.maestro.edn"
                               "./deps.edn"]]
                             ,
                             [lint-prepare
                              ["./deps.edn"]]])
                      (changed?))
            action-2+ (action+ state)
            n-action  (count action-2+)]
        ($.maestro.plugin/done (format "%d action%s to perform"
                                       n-action
                                       (if (> n-action
                                              1)
                                         "s"
                                         "")))
        (doseq [action action-2+]
          (action))
        (commit state)))))
