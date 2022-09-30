(ns protosens.maestro.git.lib

  "Aliases that contains a name under `:maestro.git.lib/name` can be exposed publicly as
   git libraries and consumed from Clojure CLI via `:deps/root`.

   A name is a symbol `<organization>/<artifact>` such as `com.acme/some-lib`.

   In order to do so, each such module must have its own `deps.edn` file.
   See [[expose]] and [[task]]."

  (:require [babashka.fs               :as bb.fs]
            [clojure.java.io           :as java.io]
            [clojure.pprint            :as pprint]
            [clojure.string            :as string]
            [protosens.maestro         :as $.maestro]
            [protosens.maestro.profile :as $.maestro.profile]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;;


(defn gitlib?

  "Returns true if an alias (given its data) is meant to be exposed as a git library."

  [alias-data]

  (alias-data :maestro.git.lib/name))



(defn prepare-deps-edn

  "Computes the content of the `deps.edn` file for the given `alias` meant to be exposed
   as a git library.

   The algorithm uses [[protosens.maestro/search]] starting with `basis`.
   The `release` profile is activated by default.
  
   For each required alias:

     - Merge `:extra-deps`
     - If the required alias is itself exposed as a git library, require it as a `:local/root` dependency
     - If not, merge `:extra-paths`

   Fails if a path to merge is not a child of the `:maestro/root` of the alias.

   Returns a map with:

   | Key                              | Value                                           |
   |----------------------------------|-------------------------------------------------|
   | `:maestro/require`               | Vector of required aliases                      |
   | `:maestro.git.lib/deps.edn`      | `deps.edn` map                                  |
   | `:maestro.git.lib.path/deps.edn` | Path where the `deps.edn` map should be written |"

  [basis git-sha alias]

  (let [alias->data (basis :aliases)
        data        (alias->data alias)
        root-dir    (data :maestro/root)
        _           (when-not root-dir
                      (throw (Exception. (str "Missing root directory for alias: "
                                              alias))))
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
                                            {:deps/root (data-required :maestro/root)
                                             :git/sha   git-sha
                                             :git/url   (basis :maestro.git.lib/url)})
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
                                                 (str (bb.fs/relativize root-dir
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



(defn write-deps-edn

  "Default way of writing a `deps-edn` file by pretty-printing it to the given `path`."

  [path deps-edn]

  (let [parent (bb.fs/parent path)]
    (when-not (bb.fs/exists? parent)
      (bb.fs/create-dirs parent)))
  (with-open [writer (java.io/writer path)]
    (binding [*out* writer]
      (println ";; This is a file generated by Maestro for allowing this module to be consumed")
      (println ";; as a git dependency via `:deps/root`.")
      (println ";;")
      (pprint/pprint deps-edn)))
  path)


;;;


(defn expose

  "Generates custom `deps.edn` files for all aliases having in there data a name (see namespace
   description) as well as a `:maestro/root` (path to the root directory of that alias).

   The algorithm is descrived in [[prepare-deps-edn]].

   When a `deps.edn` file has been computed, it is written to disk by [[write-deps-edn]]. This
   can be overwritten by providing an alternative function under `:maestro.git.lib/write`.
   
   Returns a map where keys are aliased for which a `deps.edn` file has been generated and values
   are the data returned from [[prepare-deps-edn]] without the `deps.edn` content."


  ([git-sha]

   (expose git-sha
           nil))


  ([git-sha basis]

   (let [basis-2     (-> basis
                         ($.maestro/ensure-basis)
                         ($.maestro.profile/append+ ['release]))
         alias->data (basis-2 :aliases)
         basis-3     (-> basis-2
                         (assoc :maestro/alias+
                                (vec (keys alias->data)))
                         ($.maestro/search))
         gitlib+     (filterv (fn [alias]
                                (if-some [data (alias->data alias)]
                                  (gitlib? data)
                                  (throw (Exception. (str "No data for alias: "
                                                          alias)))))
                              (basis-3 :maestro/require))
         write       (or (:maestro.git.lib/write basis)
                         write-deps-edn)]
     (into (sorted-map)
           (map (fn [alias]
                  (let [prepared (prepare-deps-edn basis-2
                                                   git-sha
                                                   alias)]
                    (write (prepared :maestro.git.lib.path/deps.edn)
                           (prepared :maestro.git.lib/deps.edn))
                    [alias (dissoc prepared
                                   :maestro.git.lib/deps.edn)])))
           (sort gitlib+)))))



(defn task

  "Uses and pretty-prints [[expose]].

   Output prints modules that have been exposed, the path to their `deps.edn` and which
   aliases they each required."


  ([]

   (task nil))


  ([basis]

   (if-some [git-sha (or (:maestro.git.lib/sha basis)
                         (first *command-line-args*))]
     (doseq [[alias
              feedback] (expose git-sha
                                basis)]
       (println (format "%s -> %s"
                        alias
                        (feedback :maestro.git.lib.path/deps.edn)))
       (doseq [alias-child (sort (filter (fn [alias-child]
                                           (not= alias-child
                                                 alias))
                                         (feedback :maestro/require)))]
         (println " "
                  alias-child))
       (println))
     (throw (Exception. "SHA git exposing Git libraries not provided")))))
