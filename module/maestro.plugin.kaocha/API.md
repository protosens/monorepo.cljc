# Table of contents
-  [`protosens.maestro.plugin.kaocha`](#protosens.maestro.plugin.kaocha)  - Maestro plugin for the Kaocha test runner reliably computing source and test paths for aliases you are working with.
    -  [`prepare`](#protosens.maestro.plugin.kaocha/prepare) - Given a <code>basis</code> that went through [[protosens.maestro/search]], produces an EDN file at containing <code>:kaocha/source-paths</code> and <code>:kaocha/test-paths</code>.

-----
# <a name="protosens.maestro.plugin.kaocha">protosens.maestro.plugin.kaocha</a>


Maestro plugin for the Kaocha test runner reliably computing source and test paths for aliases you are
   working with. No need to maintain several test suites manually.




## <a name="protosens.maestro.plugin.kaocha/prepare">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.plugin.kaocha/src/main/clj/protosens/maestro/plugin/kaocha.clj#L13-L46) `prepare`</a>
``` clojure

(prepare basis)
```


Given a `basis` that went through [[protosens.maestro/search]], produces an EDN file
   at containing `:kaocha/source-paths` and `:kaocha/test-paths`.

   The path for that EDN file must be provided under `:maestro.plugin.kaocha/path`. It should be
   ignored in `.gitignore` as it is not meant to be checked out.

   Your Kaocha configuration file can then refer to it:

   ```clojure
   #kaocha/v1
   {:tests [#meta-merge [{:id                 :unit
                          :kaocha/ns-patterns [".+"]}
                         #include "<PATH>"]]}
   ```

   Test paths are deduced from aliases that were required by activing the `test` profile`. Source
   paths are all the remaining paths.
