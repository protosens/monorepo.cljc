(ns protosens.requirer

  (:require [clojure.string      :as string]
            [protosens.deps.edn  :as $.deps.edn]
            [protosens.namespace :as $.namespace]
            [protosens.process   :as $.process]))


;;;;;;;;;; Private


(defn- -require

  ;; Core implementation for different commands.

  [deps-edn command option+]

  (-> ($.process/shell (concat command
                               (mapcat (fn [nmspace]
                                         ["-e" (format "(println \"(require '%s)\")"
                                                      nmspace) 
                                          "-e" (format "(require '%s)"
                                                       nmspace)])
                                        (-> deps-edn
                                            ($.deps.edn/path+ (:alias+ option+))
                                            ($.namespace/search)
                                            (sort))))

                       (-> (:protosens.process/option+ option+)
                           (assoc :dir
                                  (deps-edn :deps/root))))
      ($.process/success?)))


;;;;;;;;;; Public


(defn bb

  "Exactly like [[clojure-cli]] but uses Babashka instead of Clojure CLI."


  ([deps-edn]

   (bb deps-edn
       nil))


  ([deps-edn option+]

   (-require deps-edn
             (let [alias+ (:alias+ option+)]
               ["bb"
                "-e"
                ;; Note: `./` local root does not work.
                (format "(babashka.deps/add-deps '%s
                                                 {:aliases %s})"
                        deps-edn
                        (if (seq alias+)
                          (vec alias+)
                          "nil"))])
             option+)))


(defn clojure-cli

  "In a new process, requires all namespaces found with [[namespace+]].
  
   This is useful for ensuring that a project fully compiles for production without any
   tests dependencies and such.

   Namespaces are required one by one using Clojure CLI.

   Returns `true` if the process completed with a zero status, meaning everything has been
   required without any problem."


  ([deps-edn]

   (clojure-cli deps-edn
                nil))


  ([deps-edn option+]

   (-require deps-edn
             (let [alias+ (:alias+ option+)]
               ["clojure"
                (cond->
                  "-M"
                  (seq alias+)
                  (str (string/join alias+)))])
             option+)))
