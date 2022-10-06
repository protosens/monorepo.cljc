(ns protosens.maestro.requirer

  (:require [babashka.fs         :as bb.fs]
            [clojure.java.io     :as java.io]
            [clojure.string      :as string]
            [protosens.edn.read  :as $.edn.read]
            [protosens.maestro   :as $.maestro]
            [protosens.namespace :as $.namespace]))


;;;;;;;;;;


(defn generate


  ([]

   (generate nil))


  ([basis]

   (let [basis-2     ($.maestro/ensure-basis basis)
         alias+      (into #{}
                           (or (basis-2 :maestro.requirer/alias+)
                               (some-> (first *command-line-args*)
                                       ($.edn.read/string))))
         alias->data (basis-2 :aliases)
         ]
     (doseq [[alias
              data] (if (seq alias+)
                      (filter (comp alias+
                                    first)
                              alias->data)
                      alias->data)
             :let   [requirer (:maestro/requirer data)]
             :when  requirer
             :let   [root    (data :maestro/root)
                     basis-3 ($.maestro/search (-> basis-2
                                                   (update :maestro/alias+
                                                           #(conj (vec %)
                                                                  alias))
                                                   (update :maestro/profile+
                                                           #(conj (vec %)
                                                                  'release))))]]
       (with-open [writer (java.io/writer ($.namespace/to-filename (second requirer)
                                                                   (first requirer)
                                                                   ".cljc"))]
         (binding [*out* writer]
           ($.namespace/main-ns (first requirer)
                                ($.namespace/in-path+ (filter (fn [path]
                                                                (string/starts-with? path
                                                                                     root))
                                                              ($.maestro/extra-path+ basis-3
                                                                                     (basis-3 :maestro/require)))))
           ))))))
