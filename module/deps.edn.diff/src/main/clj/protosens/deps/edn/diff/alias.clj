(ns protosens.deps.edn.diff.alias

  (:require [clojure.set                         :as       C.set]
            [protosens.deps.edn                  :as       $.deps.edn]
            [protosens.deps.edn.alias.definition :as       $.deps.edn.alias.definition]
            [protosens.deps.edn.diff             :as-alias $.deps.edn.diff]
            [protosens.deps.edn.diff.alias       :as-alias $.deps.edn.diff.alias]
            [protosens.deps.edn.diff.git         :as       $.deps.edn.diff.git]
            [protosens.path                      :as       $.path]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;;


(defn init

  [state]
  
  (update state
          ::$.deps.edn.diff.alias/unprocessed
          #(-> (or %
                   (-> state
                       (get-in [::$.deps.edn.diff/new
                                :aliases])
                       (keys)))
               (set))))



(defn mark-processed+

  [state alias+]

  (update state
          ::$.deps.edn.diff.alias/unprocessed
          C.set/difference
          (set alias+)))



(defn added

  [state]

  (let [added (C.set/difference (state ::$.deps.edn.diff.alias/unprocessed)
                                (-> (get-in state
                                            [::$.deps.edn.diff/old
                                             :aliases])
                                    (keys)
                                    (set)))]
    (-> state
        (assoc ::$.deps.edn.diff.alias/added
               added)
        (mark-processed+ added))))



(defn =definition

  [definition-old definition-new]

  (and ($.deps.edn.alias.definition/=extra-dep+ definition-old
                                                definition-new)
       ($.deps.edn.alias.definition/=extra-path+ definition-old
                                                 definition-new)))



(defn modified-definition

  [state]

  (let [alias->definition-old (get-in state
                                      [::$.deps.edn.diff/old
                                       :aliases])
        alias->definition-new (get-in state
                                      [::$.deps.edn.diff/new
                                       :aliases])
        =definition-user      (or (state ::$.deps.edn.diff.alias/=definition)
                                  (fn [_definition-old _definition-true]
                                    true))
        modified              (into #{}
                                    (filter (fn [alias]
                                              (let [definition-old (alias->definition-old alias)
                                                    definition-new (alias->definition-new alias)]
                                                (not (and (=definition definition-old
                                                                       definition-new)
                                                          (=definition-user definition-old
                                                                            definition-new))))))
                                    (state ::$.deps.edn.diff.alias/unprocessed))]
    (-> state
        (assoc ::$.deps.edn.diff.alias/modified-definition
               modified)
        (mark-processed+ modified))))



(defn modified-path+

  [state]

  ;; Ignores untracked files.
  ;;
  ;; It is much faster diffing all paths at once and figure out dirty aliases than
  ;; diffing each alias with its own `git` invocation.

  (let [unprocessed  (state ::$.deps.edn.diff.alias/unprocessed)
        alias->path+ ($.deps.edn/alias->path+ (state ::$.deps.edn.diff/new)
                                              unprocessed)
        diff-file+   ($.deps.edn.diff.git/path+ state
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
        (assoc ::$.deps.edn.diff.alias/modified-path+
               modified)
        (assoc ::$.deps.edn.diff.alias/clean
               (C.set/difference unprocessed
                                 modified))
        (dissoc ::$.deps.edn.diff.alias/unprocessed))))



(defn augmented

  ([]

   (augmented nil))


  ([state]

   (-> state
       (init)
       (added)
       (modified-definition)
       (modified-path+))))



(defn dirty

  [state]

  (C.set/union (state ::$.deps.edn.diff.alias/added)
               (state ::$.deps.edn.diff.alias/modified-definition)
               (state ::$.deps.edn.diff.alias/modified-path+)))
