(ns protosens.maestro.plugin.kaocha

  "Maestro plugin for the [Kaocha](https://github.com/lambdaisland/kaocha) test runner.
  
   Reliably computes source and test paths for aliases you are working with.
   No need to maintain several test suites manually."

  (:require [babashka.fs        :as bb.fs]
            [protosens.deps.edn :as $.deps.edn]
            [protosens.maestro  :as $.maestro]))


;;;;;;;;;;


(defn prepare

  "Produces and EDN file supplementing the Kaocha EDN configuraton file.

   Assumes `basis` already went through [[protosens.maestro/search]].

   This EDN file will contain all source and tests paths for aliases in `:maestro/require`.
   More precisely, test paths are deduced from aliases that were required by activing the `test` profile.
   Source paths are all the remaining paths.
  
   The path for that EDN file must be provided under `:maestro.plugin.kaocha/path`. It should be
   ignored in `.gitignore` as it is not meant to be checked out.

   Your Kaocha configuration file can then refer to it (substituting `<PATH>`):

   ```clojure
   #kaocha/v1
   {:tests [#meta-merge [{:id                 :unit
                          :kaocha/ns-patterns [\".+\"]}
                         #include \"<PATH>\"]]}
   ```"

  [basis]

  (let [path (basis :maestro.plugin.kaocha/path)]
    (when-not path
      ($.maestro/fail "Kaocha plugin for Maestro require a path"))
    (bb.fs/create-dirs (bb.fs/parent path))
    (spit path
          {:kaocha/source-paths ($.deps.edn/path+ basis
                                                  ($.maestro/not-by-profile+ basis
                                                                             '[test]))
           :kaocha/test-paths   ($.deps.edn/extra-path+ basis
                                                        ($.maestro/by-profile+ basis
                                                                               '[test]))})
    basis))
