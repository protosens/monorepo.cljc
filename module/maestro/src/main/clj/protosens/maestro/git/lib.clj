(ns protosens.maestro.git.lib

  (:require [babashka.fs               :as fs]
            [clojure.java.io           :as java.io]
            [clojure.pprint            :as pprint]
            [clojure.string            :as string]
            [protosens.maestro         :as $.maestro]
            [protosens.maestro.profile :as $.maestro.profile]))


;;;;;;;;;;


(defn gitlib?

  [alias-data]

  (alias-data :maestro.git.lib/name))



(defn write-deps-edn

  [path deps-edn]

  (with-open [writer (java.io/writer path)]
    (binding [*out* writer]
      (println ";; This is a generated file allowing this module to be consumed")
      (println ";; as a git dependency via `:deps/root`.")
      (println ";;")
      (pprint/pprint deps-edn)))
  path)



(defn prepare-deps-edn

  [basis alias]

  (let [alias->data (basis :aliases)
        data        (alias->data alias)
        root-dir    (data :maestro/root)
        required    (-> basis
                        (assoc :maestro/alias+
                               [alias])
                        ($.maestro/search)
                        (:maestro/require))
        deps-edn    (reduce (fn [deps-edn alias-required]
                              (let [data-required (alias->data alias-required)
                                    deps-edn-2    (update deps-edn
                                                          :deps
                                                          merge
                                                          (data-required :extra-deps))]
                                (if (and (not= alias-required
                                               alias)
                                         (gitlib? data-required))
                                  (assoc-in deps-edn-2
                                            [:deps
                                             (data-required :maestro.git.lib/name)]
                                            {:local/root (str (fs/relativize root-dir
                                                                             (data-required :maestro/root)))})
                                  (update deps-edn-2
                                          :paths
                                          into
                                          (map (fn [path]
                                                 (when-not (string/starts-with? path
                                                                                root-dir)
                                                   (throw (ex-info "Path to add does not belong"
                                                                   {:alias/git-lib  alias
                                                                    :alias/required alias-required
                                                                    :path           path
                                                                    :root           root-dir})))
                                                 (str (fs/relativize root-dir
                                                                     path))))
                                          (data-required :extra-paths)))))
                            {:deps  (sorted-map)
                             :paths #{}}
                            required)]
    {:maestro/require               required
     :maestro.git.lib/deps.edn      (update deps-edn
                                            :paths
                                            (comp vec
                                                  sort))
     :maestro.git.lib.path/deps.edn (str root-dir
                                         "/deps.edn")}))



(defn gen-deps

  
  ([]

   (gen-deps nil))


  ([basis]

   (let [basis-2     (-> basis
                         ($.maestro/ensure-basis)
                         ($.maestro.profile/append+ ['release]))
         alias->data (basis-2 :aliases)
         basis-3     (-> basis-2
                         (assoc :maestro/alias+
                                (vec (keys alias->data)))
                         ($.maestro/search))
         gitlib+     (filterv (fn [alias]
                                (let [data (alias->data alias)]
                                  (and (gitlib? data)
                                       (or (data :maestro/root)
                                           (throw (Exception. (str "Missing root in alias data: "
                                                                   alias)))))))
                              (basis-3 :maestro/require))]
     (into (sorted-map)
           (map (fn [alias]
                  (let [prepared (prepare-deps-edn basis-2
                                                   alias)]
                    (write-deps-edn (prepared :maestro.git.lib.path/deps.edn)
                                    (prepared :maestro.git.lib/deps.edn))
                    [alias (dissoc prepared
                                   :maestro.git.lib/deps.edn)])))
           gitlib+))))



(defn task

  
  ([]

   (task nil))


  ([basis]

   (-> (gen-deps basis)
       (pprint/pprint))))


(comment

  (task {:maestro/profile+ []})


  )
