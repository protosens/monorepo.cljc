(ns protosens.maestro.idiom.listing

  "Geneting a Markdown files listing existing modules.
  
   See [[main]]."

  (:require [babashka.fs       :as bb.fs]
            [clojure.java.io   :as java.io]
            [clojure.string    :as string]
            [protosens.maestro :as $.maestro]
            [protosens.string  :as $.string]))


;;;;;;;;;;


(defn module+

  "Returns modules exposed modules and private ones.
  
   More precisely, adds to the basis `:maestro.idiom.listing/public` pointing to modules having a
   `:maestro.module.expose/name` and `:maestro.idiom.listing/private` pointing to the rest.

   Reminder: modules are aliases with a `:maestro/root`.

   Alias data is also prepared:

   - `:maestro/doc` is truncated to its first line
   - `:maestro/root` is relativized to the parent directory of `path-list`

   `path-list` illustrates the file where those modules will be listed."


  ([path-list]

   (module+ nil
            path-list))


  ([proto-basis path-list]

   (let [path-list-dir (or (bb.fs/parent path-list)
                           ".")
         basis         ($.maestro/ensure-basis proto-basis)
         module+       (group-by (comp boolean
                                       :maestro.module.expose/name
                                       second)
                                 (filter (comp :maestro/root
                                               second)
                                         (basis :aliases)))
         prepare       (fn [alias+]
                         (not-empty (reduce (fn [acc [alias alias-data]]
                                              (assoc acc
                                                     alias
                                                     (-> alias-data
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
                                                                                         %))))))
                                            {}
                                            alias+)))]
     (assoc basis
            :maestro.idiom.listing/private (prepare (module+ false))
            :maestro.idiom.listing/public  (prepare (module+ true))))))



(defn table

  "Prints a Markdown table for the prepared modules.

   Either private or public modules from [[module+]].

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
              alias-data] (sort-by first
                                   prepared-module+)]
       (println (format "| [`%s`](./%s) | %s |"
                        (f-name alias)
                        (alias-data :maestro/root)
                        (alias-data :maestro/doc)))))))


;;;


(defn main

  "Generates a Markdown file under `path-list` listing exposed and private modules.
  
   This function is opinionated. For a more custom behavior, see [[module+]] and possibly [[table]].

   `path-list` is typically the path to the `README.md` file in a directory hosting all those modules."


  ([path-list]

   (main nil
         path-list))


  ([proto-basis path-list]

   (let [{:as                         basis
          :maestro.idiom.listing/keys [private
                                       public]} (module+ proto-basis
                                                         path-list)]
     (with-open [writer (java.io/writer path-list)]
       (binding [*out* writer]
         (println "# Modules")
         (if (or private
                 public)
           (do
             (when public
               (println)
               (println "Publicly available as [Git dependencies](https://clojure.org/guides/deps_and_cli#_using_git_libraries) for [Clojure CLI](https://clojure.org/guides/deps_and_cli):")
               (println)
               (table public
                      basis))
             (when private
               (println)
               (println "Private, not meant for public use:")
               (println)
               (table private
                      basis)))
           (println "No available module."))))
     basis)))
