(ns protosens.maestro.plugin.kaocha

  "Maestro plugin for the Kaocha test runner reliably computing source and test paths for aliases you are
   working with. No need to maintain several test suites manually."

  (:require [babashka.fs             :as bb.fs]
            [protosens.maestro       :as $.maestro]
            [protosens.maestro.alias :as $.maestro.alias]))


;;;;;;;;;;


(defn prepare

  "Given a `basis` that went through [[protosens.maestro/search]], produces an EDN file
   at containing `:kaocha/source-paths` and `:kaocha/test-paths`.

   The path for that EDN file must be provided under `:maestro.plugin.kaocha/path`. It should be
   ignored in `.gitignore` as it is not meant to be checked out.

   Your Kaocha configuration file can then refer to it:

   ```clojure
   #kaocha/v1
   {:tests [#meta-merge [{:id                 :unit
                          :kaocha/ns-patterns [\".+\"]}
                         #include \"<PATH>\"]]}
   ```

   Test paths are deduced from aliases that were required by activing the `test` profile`. Source
   paths are all the remaining paths."

  [basis]

  (let [path (basis :maestro.plugin.kaocha/path)]
    (when-not path
      ($.maestro/fail "Kaocha plugin for Maestro require a path"))
    (bb.fs/create-dirs (bb.fs/parent path))
    (spit path
          {:kaocha/source-paths ($.maestro.alias/extra-path+ basis
                                                             ($.maestro/not-by-profile+ basis
                                                                                        '[test]))
           :kaocha/test-paths   ($.maestro.alias/extra-path+ basis
                                                             ($.maestro/by-profile+ basis
                                                                                     '[test]))})
    basis))
