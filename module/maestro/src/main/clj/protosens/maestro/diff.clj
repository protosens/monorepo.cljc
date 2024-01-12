(ns protosens.maestro.diff

  (:require [clojure.set                         :as       C.set]
            [protosens.deps.edn                  :as       $.deps.edn]
            [protosens.deps.edn.alias.definition :as       $.deps.edn.alias.definition]
            [protosens.edn.read                  :as       $.edn.read]
            [protosens.maestro.diff              :as-alias $.maestro.diff]
            [protosens.maestro.diff.deps         :as-alias $.maestro.diff.deps]
            [protosens.maestro.diff.rev          :as-alias $.maestro.diff.rev]
            [protosens.maestro.plugin            :as       $.maestro.plugin]
            [protosens.git                       :as       $.git]
            [protosens.path                      :as       $.path]))


;;;;;;;;;;


(defn ^:no-doc -rev

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

   (let [rev-old              (or (-rev state
                                        ::$.maestro.diff.rev/old)
                                  ($.git/commit-sha 0))
         rev-new              (-rev state
                                    ::$.maestro.diff.rev/new)
         deps-maestro-edn-new (if rev-new
                                ($.maestro.plugin/read-deps-maestro-edn rev-new)
                                ($.edn.read/file "./deps.maestro.edn"))]
     (-> state
         (assoc ::$.maestro.diff.deps/old ($.maestro.plugin/read-deps-maestro-edn rev-old)
                ::$.maestro.diff.deps/new deps-maestro-edn-new
                ::$.maestro.diff.rev/old  rev-old)
         (update ::$.maestro.diff/unprocessed
                 #(-> (or %
                          (-> deps-maestro-edn-new
                              (:aliases)
                              (keys)))
                      (set)))))))



(defn mark-processed+

  [state alias+]

  (update state
          ::$.maestro.diff/unprocessed
          C.set/difference
          (set alias+)))



(defn added

  [state]

  (let [added (C.set/difference (state ::$.maestro.diff/unprocessed)
                                (-> (get-in state
                                            [::$.maestro.diff.deps/old
                                             :aliases])
                                    (keys)
                                    (set)))]
    (-> state
        (assoc ::$.maestro.diff/added
               added)
        (mark-processed+ added))))



(defn =definition?

  [definition-old definition-new]

  (and ($.deps.edn.alias.definition/=extra-dep+ definition-old
                                                definition-new)
       ($.deps.edn.alias.definition/=extra-path+ definition-old
                                                 definition-new)
       (= (set (:maestro/platform+ definition-old))
          (set (:maestro/platform+ definition-new)))))



(defn modified-definition

  [state]

  (let [alias->definition-old (get-in state
                                      [::$.maestro.diff.deps/old
                                       :aliases])
        alias->definition-new (get-in state
                                      [::$.maestro.diff.deps/new
                                       :aliases])
        modified              (into #{}
                                    (filter (fn [alias]
                                              (not (=definition? (alias->definition-old alias)
                                                                 (alias->definition-new alias)))))
                                    (state ::$.maestro.diff/unprocessed))]
    (-> state
        (assoc ::$.maestro.diff/modified-definition
               modified)
        (mark-processed+ modified))))



(defn diff-path+

  [state path+]

  (map (comp $.path/normalized
             $.path/from-string)
       ($.git/diff-path+ (state ::$.maestro.diff.rev/old)
                         (state ::$.maestro.diff.rev/new)
                         path+)))



(defn modified-path+

  [state]

  ;; Ignores untracked files.
  ;;
  ;; It is much faster diffing all paths at once and figure out dirty aliases than
  ;; diffing each alias with its own `git` invocation.

  (let [unprocessed  (state ::$.maestro.diff/unprocessed)
        alias->path+ ($.deps.edn/alias->path+ (state ::$.maestro.diff.deps/new)
                                              unprocessed)
        diff-file+   (diff-path+ state
                                 (apply concat
                                        (vals alias->path+)))
        modified     (into #{}
                           (keep (fn [[alias path+]]
                                   (when (some (fn [path]
                                                 (let [path-2 (-> path
                                                                  ($.path/from-string)
                                                                  ($.path/normalized))]
                                                   (some (fn [diff-file]
                                                           ($.path/starts-with? diff-file
                                                                                path-2))
                                                         diff-file+)))
                                               path+)
                                     alias)))
                           alias->path+)]
    (-> state
        (assoc ::$.maestro.diff/modified-path+
               modified)
        (assoc ::$.maestro.diff/clean
               (C.set/difference unprocessed
                                 modified))
        (dissoc ::$.maestro.diff/unprocessed))))



(defn run

  ([]

   (run nil))


  ([state]

   (-> state
       (init)
       (added)
       (modified-definition)
       (modified-path+))))



(defn dirty

  [state]

  (C.set/union (state ::$.maestro.diff/added)
               (state ::$.maestro.diff/modified-definition)
               (state ::$.maestro.diff/modified-path+)))
