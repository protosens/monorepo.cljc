(ns protosens.task.deps

  (:refer-clojure :exclude [sync])
  (:require [babashka.fs                        :as bb.fs]
            [protosens.edn.read                 :as $.edn.read]
            [protosens.maestro                  :as $.maestro]
            [protosens.maestro.plugin           :as $.maestro.plugin]
            [protosens.maestro.plugin.bb        :as $.maestro.plugin.bb]
            [protosens.maestro.plugin.clj-kondo :as $.maestro.plugin.clj-kondo]))


;;;;;;;;;;


(def ^:private -path-cached-bb-maestro-edn

  "./private/tmp/deps/bb.maestro.edn")



(def ^:private -path-cached-deps-maestro-edn

  "./private/tmp/deps/deps.maestro.edn")


;;;


(defn- -cache

  [path-target path-target-cached]

  (bb.fs/create-dirs (bb.fs/parent path-target-cached))
  (bb.fs/copy path-target
              path-target-cached
              {:replace-existing true}))



(defn- -changed?

  [path-target path-target-cached]

  (or (not (bb.fs/exists? path-target-cached))
      (not= ($.edn.read/file path-target-cached)
            ($.edn.read/file path-target))))


(defn- -changed-bb-maestro-edn?

  []

  (-changed? "./bb.maestro.edn"
             -path-cached-bb-maestro-edn))



(defn- -changed-deps-maestro-edn?

  []

  (-changed? "./deps.maestro.edn"
             -path-cached-deps-maestro-edn))



(defn sync

  []

  ($.maestro.plugin/intro "protosens.task.deps/sync")
  ($.maestro.plugin/safe
    (delay
      (let [changed-bb?   (-changed-bb-maestro-edn?)
            changed-deps? (-changed-deps-maestro-edn?)]
        ($.maestro.plugin/step (if (or changed-bb?
                                       changed-deps?)
                                 "Before preparing `deps.edn`, some tools need to be synced with `deps.maestro.edn`"
                                 "Will prepare `deps.edn` from `deps.maestro.edn`"))
        ($.maestro.plugin/done "Ready to proceed")
        ;;
        ;; Syncs `bb.edn`.
        (when (or changed-bb?
                  changed-deps?)
          ($.maestro.plugin.bb/sync :module/task)
          (-cache "./bb.maestro.edn"
                  -path-cached-bb-maestro-edn))
        ;;
        ;; Syncs Clj-kondo.
        (when changed-deps?
          (binding [*command-line-args* [":GOD"]]
            ($.maestro/sync))
          ($.maestro.plugin.clj-kondo/prepare)
          (-cache "./deps.maestro.edn"
                  -path-cached-deps-maestro-edn)))
      ;;
      ;; Prepares `deps.edn` for user-given nodes.
      (binding [*command-line-args* (or (not-empty *command-line-args*)
                                        [":GOD"])]
        ($.maestro/sync)))))
