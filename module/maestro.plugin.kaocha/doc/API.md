# Table of contents
-  [`protosens.maestro.plugin.kaocha`](#protosens.maestro.plugin.kaocha)  - Maestro plugin for the [Kaocha](https://github.com/lambdaisland/kaocha) test runner.
    -  [`prepare`](#protosens.maestro.plugin.kaocha/prepare) - Produces and EDN file supplementing the Kaocha EDN configuraton file.

-----
# <a name="protosens.maestro.plugin.kaocha">protosens.maestro.plugin.kaocha</a>


Maestro plugin for the [Kaocha](https://github.com/lambdaisland/kaocha) test runner.
  
   Reliably computes source and test paths for aliases you are working with.
   No need to maintain several test suites manually.




## <a name="protosens.maestro.plugin.kaocha/prepare">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/main/module/maestro.plugin.kaocha/src/main/clj/protosens/maestro/plugin/kaocha.clj#L16-L51) `prepare`</a>
``` clojure

(prepare basis)
```


Produces and EDN file supplementing the Kaocha EDN configuraton file.

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
                          :kaocha/ns-patterns [".+"]}
                         #include "<PATH>"]]}
   ```

-----
