# Table of contents
-  [`protosens.maestro.plugin.deps-deploy`](#protosens.maestro.plugin.deps-deploy)  - Maestro plugin for installing and deploying artifacts via <code>slipset/deps-deploy</code>.
    -  [`clojars`](#protosens.maestro.plugin.deps-deploy/clojars) - Babashka task for deploying an artifact to Clojars.
    -  [`deploy`](#protosens.maestro.plugin.deps-deploy/deploy) - Core function for using <code>deps-deploy</code> via the <code>clojure</code> tool.
    -  [`local`](#protosens.maestro.plugin.deps-deploy/local) - Installs the given alias to the local Maven repository.

-----
# <a name="protosens.maestro.plugin.deps-deploy">protosens.maestro.plugin.deps-deploy</a>


Maestro plugin for installing and deploying artifacts via `slipset/deps-deploy`.

   Works even better in combination with [[protosens.maestro.plugin.build]].

   Babashka tasks:

   - [`clojars`](#protosens.maestro.plugin.deps-deploy/clojars)
   - [`local`](#protosens.maestro.plugin.deps-deploy/local)




## <a name="protosens.maestro.plugin.deps-deploy/clojars">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.plugin.deps-deploy/src/main/clj/protosens/maestro/plugin/deps_deploy.clj#L78-L111) `clojars`</a>
``` clojure

(clojars alias-deps-deploy)
(clojars alias-deps-deploy username path-token alias-deploy)
```


Babashka task for deploying an artifact to Clojars.

   See [`deploy`](#protosens.maestro.plugin.deps-deploy/deploy) about `:maestro.plugin.deps-deploy/exec-args`.

   `username`, `path-token`, and `alias` are taken from command line arguments
   if not provided explicitly.

   `path-token` is the path to the file containing the Clojars deploy token to use.

## <a name="protosens.maestro.plugin.deps-deploy/deploy">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.plugin.deps-deploy/src/main/clj/protosens/maestro/plugin/deps_deploy.clj#L32-L72) `deploy`</a>
``` clojure

(deploy alias-deps-deploy installer alias-deploy env)
```


Core function for using `deps-deploy` via the `clojure` tool.
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
   | `:pom-file` | "pom.xml" file assumed to be in `:maestro/root` |

## <a name="protosens.maestro.plugin.deps-deploy/local">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.plugin.deps-deploy/src/main/clj/protosens/maestro/plugin/deps_deploy.clj#L115-L139) `local`</a>
``` clojure

(local alias-deps-deploy)
(local alias-deps-deploy alias-deploy)
```


Installs the given alias to the local Maven repository.

   Alias to install will be taken from the first command line argument if not provided
   explicitly.

   See [`deploy`](#protosens.maestro.plugin.deps-deploy/deploy) about `:maestro.plugin.deps-deploy/exec-args`.
