(ns protosens.maestro.idiom.listing

  "Geneting a Markdown files listing existing modules.
  
   See [[main]]."

  (:require [babashka.fs                     :as bb.fs]
            [clojure.java.io                 :as java.io]
            [clojure.string                  :as string]
            [protosens.maestro               :as $.maestro]
            [protosens.maestro.module.expose :as $.maestro.module.expose]
            [protosens.string                :as $.string]))


;;;;;;;;;;


(def default-list+

  "Default predicates for creating lists of modules.

   More precisely, a vector where items are maps such as:

   | Key     | Value                                |
   |---------|--------------------------------------|
   | `:pred` | `(fn [alias alias-data] include?)`   |
   | `:txt`  | Text preceding the list when printed |
  
   See [[create-list+]]."

  [{:pred (fn [_alias alias-data]
            ($.maestro.module.expose/exposed? alias-data))
    :txt   "Publicly available as [Git dependencies](https://clojure.org/guides/deps_and_cli#_using_git_libraries)
            for [Clojure CLI](https://clojure.org/guides/deps_and_cli):"}
   {:pred (fn [_alias alias-data]
            (not ($.maestro.module.expose/exposed? alias-data)))
    :txt  "Other modules:"}])



(defn create-list+

  "Creates lists of modules.
  
   Follows a vector of predicates used for building lists. See [[default-list+]], the default value
   that can be overwritten under `:maestro.idiom.listing/list+` in `proto-basis`.

   Modules are added to each list for which they pass the predicate, under `:module+`.
   Reminder: modules are aliases with a `:maestro/root`.
 
   Alias data is also prepared:

   - `:maestro/doc` is truncated to its first line
   - `:maestro/root` is relativized to the parent directory of `path-list`

   `path-list` illustrates the file where those modules will be listed."


  ([path-list]

   (create-list+ nil
                 path-list))


  ([proto-basis path-list]

   (let [path-list-dir (or (bb.fs/parent path-list)
                           ".")
         basis         ($.maestro/ensure-basis proto-basis)
         list+         (or (basis :maestro.idiom.listing/list+)
                           default-list+)
         pred+         (partition 2
                                  (interleave (range)
                                              (map :pred
                                                   list+)))]
     (reduce (fn [list-2+ [alias alias-data]]
               (if-some [i-pred+ (not-empty (keep (fn [[i pred]]
                                                    (when (pred alias
                                                                alias-data)
                                                      i))
                                                  pred+))]
                 (let [alias-data-2 (-> alias-data
                                        (update :maestro/doc
                                                (fn [doc]
                                                  (when doc
                                                    (let [doc-2 ($.string/first-line doc)]
                                                      (cond->
                                                        doc-2
                                                        (string/ends-with? doc-2
                                                                           ".")
                                                        ($.string/trunc-right 1))))))
                                        (update :maestro/root
                                                #(str (bb.fs/relativize path-list-dir
                                                                        %))))]
                   (reduce (fn [list-3+ i-pred]
                             (update-in list-3+
                                        [i-pred
                                         :module+]
                                        (fnil conj
                                              [])
                                        [alias
                                         alias-data-2]))
                           list-2+
                           i-pred+))
                 list-2+))
             list+
             (sort-by first
                      (filter (comp :maestro/root
                                    second)
                              (basis :aliases)))))))



(defn table

  "Prints a Markdown table for the prepared modules.

   More precisely, the vector of `[alias alias-data]` prepared for each list in [[create-list+]].

   Options may contain:

   | Key                           | Value                              | Default |
   |-------------------------------|------------------------------------|---------|
   | `:maestro.idiom.listing/name` | `(fn [alias-keyword] listed-name)` | `name`  |"


  ([prepared-module+]

   (table prepared-module+
          nil))


  ([prepared-module+ option+]

   (let [f-name (or (:maestro.idiom.listing/name option+)
                    name)]
     (println "| Module | Description |")
     (println "|---|---|")
     (doseq [[alias
              alias-data] prepared-module+]
       (println (format "| [`%s`](./%s) | %s |"
                        (f-name alias)
                        (alias-data :maestro/root)
                        (alias-data :maestro/doc)))))))


;;;


(defn main

  "Prints a Markdown file under `path-list` listing modules.

   Lists are created with [[create-list+]].

   Tables of modules are printed with [[table]] by default. This can be overwritten with an
   alternative function under `:maestro.idiom.listing/table`.

   `path-list` is typically the path to the `README.md` file in a directory hosting all those modules."


  ([path-list]

   (main nil
         path-list))


  ([proto-basis path-list]

   (let [basis   ($.maestro/ensure-basis proto-basis)
         list+   (create-list+ basis
                               path-list)
         table-2 (or (basis :maestro.idiom.listing/table)
                     table)]
     (with-open [writer (java.io/writer path-list)]
       (binding [*out* writer]
         (println "# Modules")
         (doseq [{:keys [module+
                         txt]}   list+
                 :when           (seq module+)]
           (println)
           (println ($.string/realign txt))
           (println)
           (table-2 module+
                    basis))))
     basis)))
