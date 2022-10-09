(ns protosens.maestro.module.requirer

  "Generating \"requirer\" namespaces for modules.
  
   A module requirer namespace is a namespace that requires all other namespaces provided
   by a module.

   Concretely, those namespaces are found by collecting all `:extra-paths` for an alias and
   the aliases it requires, filtering out those that do not start with `:maestro/root`.

   Several use cases exists for this. For instance, an application might systematically require
   such a requirer namespace to ensure that everything is executed (e.g. namespaces with
   `defmethods`).

   See [[generate]] about generating requirers.

   In some situtation, a module has its own `deps.edn` (see [[protosens.maestro.git.lib]]).
   It can then be verified by executing its requirer namespace. It ensures that the whole
   module can be required in its production state, without any test dependencies and such.
   Also, verification can happen on several platforms (e.g. Clojure CLI + Babashka).

   See [[verify]]."

  (:require [babashka.fs         :as bb.fs]
            [clojure.java.io     :as java.io]
            [clojure.string      :as string]
            [protosens.deps.edn  :as $.deps.edn]
            [protosens.edn.read  :as $.edn.read]
            [protosens.maestro   :as $.maestro]
            [protosens.namespace :as $.namespace]
            [protosens.process   :as $.process]))


(declare verify-command)


;;;;;;;;;; Miscellaneous


(defn alias+

  "Finds aliases to work with.
  
   These steps are tried successively until one succeeds:

   - Fetch collection under `:maestro.module.requirer/alias+`
   - Reads first CLI arg (a single alias or a vector of aliases)
   - Get all existing aliases in `basis`
  
   `basis` may contain `:maestro.module.requirer/alias-filter`, a `(fn [alias data])`
   deciding whether the alias is selected."

  [basis]

  (let [alias+      (or (not-empty (basis :maestro.module.requirer/alias+))
                        (when-some [arg (some-> (first *command-line-args*)
                                                ($.edn.read/string))]
                          (if (vector? arg)
                            arg
                            [arg])))
        alias->data (basis :aliases)
        filt        (or (basis :maestro.module.requirer/alias-filter)
                        (constantly true))]
    (sort-by first
             (filter (fn [[alias data]]
                       (when-some [ns-sym (:maestro.module.requirer/namespace data)]
                         (when-not (symbol? ns-sym)
                           ($.maestro/fail (str "Namespace must be a symbol in alias: "
                                                alias)))
                         (when-not (string? (data :maestro.module.requirer/path))
                           ($.maestro/fail (str "Requirer path must be a string in alias: "
                                                alias)))
                         (filt alias
                               data)))
                     (if (seq alias+)
                       (map (partial find
                                     alias->data)
                            alias+)
                       alias->data)))))


;;;;;;;;;; Generation


(defn generate

  "Task generating requirer namespaces for modules.

   For this to work, a module must have the following in its alias data:

   | Key                                  | Value                                 | Mandatory? |
   |--------------------------------------|---------------------------------------|------------|
   | `:maestro.module.requirer/namespace` | Symbol for the requirer namespace     | Yes        |
   | `:maestro.module.requirer/path`      | Directory where the file is generated | Yes        |
   | `:maestro.module.requirer/exclude+   | Namespaces that must not be required  | No         |

   The CLJC file will be generated in the given respecting the directory structure of
   Clojure namespaces.

   Prints feedback about what is being generated and which namespaces are being required.
 
   See [[alias+]] about selecting aliases."


  ([]

   (generate nil))


  ([proto-basis]

   (let [basis ($.maestro/ensure-basis proto-basis)]
     (reduce (fn [acc [alias data]]
               (let [main-ns  (data :maestro.module.requirer/namespace)
                     filename (-> (data :maestro.module.requirer/path)
                                  ($.namespace/to-filename main-ns
                                                           ".cljc"))
                     root     (data :maestro/root)
                     basis-2  ($.maestro/search (-> basis
                                                    (update :maestro/alias+
                                                            #(conj (vec %)
                                                                   alias))
                                                    (update :maestro/profile+
                                                            #(conj (vec %)
                                                                   'release))))
                     ns+      (filterv (comp not
                                             (into #{main-ns}
                                                   (data :maestro.module.requirer/exclude+)))
                                       ($.namespace/in-path+ (filter (fn [path]
                                                                       (string/starts-with? path
                                                                                            root))
                                                                     ($.deps.edn/extra-path+ basis-2
                                                                                             (basis-2 :maestro/require)))))]
                 (println alias)
                 (println)
                 (println (format "  %s -> %s"
                                  main-ns
                                  filename))
                 (bb.fs/create-dirs (bb.fs/parent filename))
                 (with-open [writer (java.io/writer filename)]
                   (binding [*out* writer]
                     ($.namespace/main-ns main-ns
                                          ns+)))
                 (println)
                 (println "  Require")
                 (doseq [ns (sort ns+)]
                   (println "   "
                            ns))
                 (println)
                 (println)
                 (assoc acc
                        alias
                        {:filename filename
                         :namespace main-ns
                         :require+  ns+})))
             {}
             (alias+ basis)))))


;;;;;;;;;; Verification


(defn verify

  "Task verifying modules by executing their requirer namespaces.

   Assumes:
  
   - [[generate]] has been run first
   - Modules have the relevant data described in [[generate]]
   - Modules to verify has their own `deps.edn` under their `:maestro/root`

   Execution happens on all platforms indicated in alias data under `:maestro/platform+`.
   Defaults to `[:jvm]`. See [[verify-command]]."


  ([]

   (verify nil))


  ([proto-basis]

   (let [basis ($.maestro/ensure-basis proto-basis)]
     (reduce (fn [acc [alias data]]
               (println alias)
               (let [root (data :maestro/root)]
                 (when-not root
                   ($.maestro/fail "  Missing root directory for that module"))
                 (when-not (bb.fs/exists? (str root
                                               "/deps.edn"))
                   ($.maestro/fail "  No `deps.edn` in the root directory of the module")))
               (let [main-ns (data :maestro.module.requirer/namespace)
                     path    (data :maestro.module.requirer/path)]
                 (when-not (bb.fs/exists? ($.namespace/to-filename path
                                                                   main-ns
                                                                   ".cljc"))
                   ($.maestro/fail "  Requirer namespace for that module has not been generated"))
                 (reduce (fn [acc-2 platform]
                           (println " "
                                    platform)
                           (let [process  ($.process/run (verify-command platform
                                                         main-ns)
                                          {:dir (data :maestro/root)})
                                 success? ($.process/success? process)]
                             (if success?
                               {:success? true}
                               (let [err ($.process/err process)]
                                 (doseq [line (string/split-lines err)]
                                   (println "     "
                                            line))
                                 (println)
                                 (println)
                                 ($.maestro/fail (format "Failed to verify %s on platform %s"
                                                         alias
                                                         platform)))))
                           (update-in acc-2
                                      [alias
                                       :maestro/platform+]
                                      (fnil conj
                                            [])
                                      platform))
                         acc
                         (or (not-empty (sort (set (data :maestro/platform+))))
                             [:jvm]))))
             {}
             (alias+ basis)))))

;;;


(defmulti verify-command

  "Creates a shell command for the verification process depending on the platform to test.
  
   Used by [[verify]].

   The shell command is vector starting with the actuall shell command and the rest
   are individual arguments.

   Currently supported platforms:

   - `:bb`  (Babashka)
   - `:jvm` (Clojure JVM)" 

  (fn [platform _main-ns]
    platform))



(defmethod verify-command

  :bb

  [_platform main-ns]

  ["bb"
   "-e" (format "(babashka.deps/add-deps {:deps {'x%s/x%s {:local/root \".\"}}})"
                ;; Seems it is best generating a random artifact name.
                (str (random-uuid))
                (str (random-uuid)))
   "-e" (format "(require '%s)"
                main-ns)
   "-e" (format "(%s/-main)"
                main-ns)])


(defmethod verify-command

  :jvm

  [_platform main-ns]

  ["clojure" "-M" "-m" main-ns])

