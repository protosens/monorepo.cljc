(ns protosens.maestro.plugin.deps-deploy

  "Maestro plugin for installing and deploying artifacts via `slipset/deps-deploy`.

   Works even better in combination with [[protosens.maestro.plugin.build]].

   Babashka tasks:

     [[clojars]]
     [[local]]"

  (:require [clojure.edn            :as edn]
            [protosens.maestro      :as $.maestro]
            [protosens.maestro.util :as $.maestro.util]))


;;;;;;;;;; Private


(defn- -fail

  ;; Used when something is not valid.

  [message]

  (throw (Exception. message)))


;;;;;;;;;; Tasks


(defn deploy

  "Core function for using `deps-deploy` via the `clojure` tool.
   Works only using Babashka.
  
   | Argument           | Value                               |
   |--------------------|-------------------------------------|
   |`alias-deps-deploy` | Alias providing `deps-deploy`       |
   |`installer`         | See `deps-deploy` documentation     |
   |`alias-deploy`      | Alias to deploy                     |
   |`env`               | Map of environment variables to set | 

   The alias data of `alias-deploy` may contain arguments for `deps-deploy` under
   `:maestro.plugin.deps-deploy/exec-args`. Those ones are filled-in based on alias data
   when not provided:

   | Key         | Value                                             |
   |-------------|---------------------------------------------------|
   | `:artifact` | Value of `:maestro.plugin.build.path/output`      |
   | `:pom-file` | \"pom.xml\" file assumed to be in `:maestro/root` |"

  [alias-deps-deploy installer alias-deploy env]

  (let [alias-data (-> ($.maestro/create-basis)
                       (get-in [:aliases
                                alias-deploy]))]
    (@$.maestro.util/d*clojure {:extra-env env}
                               (str "-X"
                                    alias-deps-deploy)
                               'deps-deploy.deps-deploy/deploy
                               (-> (alias-data :maestro.plugin.deps-deploy/exec-args)
                                   (assoc :installer
                                          installer)
                                   (update :artifact
                                           #(or %
                                                (alias-data :maestro.plugin.build.path/output)
                                                (throw (Exception. "Missing path to artifact"))))
                                   (update :pom-file
                                           #(or %
                                                (str (alias-data :maestro/root)
                                                     "/pom.xml")))))))


;;;


(defn clojars

  "Babashka task for deploying an artifact to Clojars.

   See [[deploy]] about `:maestro.plugin.deps-deploy/exec-args`.

   `username`, `path-token`, and `alias` are taken from command line arguments
   if not provided explicitly.

   `path-token` is the path to the file containing the Clojars deploy token to use."


  ([alias-deps-deploy]

   (let [[username
          path-token
          str-alias]  *command-line-args*]
     (clojars alias-deps-deploy
              (or username
                  (-fail "Username missing"))
              (or path-token
                  (-fail "Path to token missing"))
              (if str-alias
                (edn/read-string str-alias)
                (-fail "Alias to deploy missing")))))


  ([alias-deps-deploy username path-token alias-deploy]

   (deploy alias-deps-deploy
           :remote
           alias-deploy
           {"CLOJARS_USERNAME" username
            "CLOJARS_PASSWORD" (slurp path-token)})))



(defn local

  "Installs the given alias to the local Maven repository.

   Alias to install will be taken from the first command line argument if not provided
   explicitly.

   See [[deploy]] about `:maestro.plugin.deps-deploy/exec-args`."


  ([alias-deps-deploy]

   (local alias-deps-deploy
          (let [alias-deploy-str (first *command-line-args*)]
            (if alias-deploy-str
              (edn/read-string alias-deploy-str)
              (-fail "Alias to deploy missing")))))


  ([alias-deps-deploy alias-deploy]

   (deploy alias-deps-deploy
           :local
           alias-deploy
           nil)))
