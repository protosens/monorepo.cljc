(ns protosens.maestro.git.lib

  (:require [babashka.fs       :as fs]
            [clojure.java.io   :as java.io]
            [clojure.pprint    :as pprint]
            [clojure.string    :as string]
            [protosens.maestro :as $.maestro]))


;;;;;;;;;;


(defn gitlib?

  [alias-data]

  (alias-data :maestro.git.lib/name))



(defn write-deps-edn

  [root-dir deps-edn]

  (let [path (str root-dir
                  "/deps.edn")]
    (with-open [writer (java.io/writer path)]
      (binding [*out* writer]
        (println ";; This is a generated file allowing this module to be consumed")
        (println ";; as a git dependency via `:deps/root`.")
        (println ";;")
        (pprint/pprint deps-edn)))
    path))


(defn gen-deps

  
  ([]

   (gen-deps nil))


  ([basis]

   (let [basis-2     ($.maestro/ensure-basis basis)
         alias->data (basis-2 :aliases)
         basis-3     (-> basis-2
                         (assoc :maestro/alias+
                                (vec (keys alias->data)))
                         ($.maestro/search))
         required    (filterv (comp gitlib?
                                    alias->data)
                              (basis-3 :maestro/require))]
     (doseq [alias required]
       (let [data     (alias->data alias)
             root-dir (data :maestro/root)]
         (write-deps-edn
           root-dir
           (-> (reduce (fn [deps-edn alias-required]
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
                       (-> basis-2
                           (assoc :maestro/alias+
                                  [alias])
                           ($.maestro/search)
                           (:maestro/require)))
               (update :paths
                       (comp vec
                             sort)))))))))


(comment

  (gen-deps {:maestro/profile+ []})


  )
