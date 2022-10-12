# Table of contents
-  [`protosens.maestro.idiom.changelog`](#protosens.maestro.idiom.changelog)  - Templating changelogs.
    -  [`main`](#protosens.maestro.idiom.changelog/main) - Templates all changelogs.
    -  [`module+`](#protosens.maestro.idiom.changelog/module+) - Templates module changelogs.
    -  [`top`](#protosens.maestro.idiom.changelog/top) - Templates the top changelog.
-  [`protosens.maestro.idiom.listing`](#protosens.maestro.idiom.listing)  - Geneting a Markdown files listing existing modules.
    -  [`main`](#protosens.maestro.idiom.listing/main) - Generates a Markdown file under <code>path-list</code> listing exposed and private modules.
    -  [`module+`](#protosens.maestro.idiom.listing/module+) - Returns modules exposed modules and private ones.
    -  [`table`](#protosens.maestro.idiom.listing/table) - Prints a Markdown table for the prepared modules.
-  [`protosens.maestro.idiom.readme`](#protosens.maestro.idiom.readme)  - Generating READMEs for modules.
    -  [`body`](#protosens.maestro.idiom.readme/body) - Prints a body of text.
    -  [`default`](#protosens.maestro.idiom.readme/default) - Default README printer.
    -  [`doc`](#protosens.maestro.idiom.readme/doc) - Prints <code>:maestro/doc</code>.
    -  [`git-dependency`](#protosens.maestro.idiom.readme/git-dependency) - Prints how to consume the alias as a Git dependency in <code>deps.edn.</code>.
    -  [`header`](#protosens.maestro.idiom.readme/header) - Prints the first line of the README.
    -  [`main`](#protosens.maestro.idiom.readme/main) - Generates READMEs for all modules.
    -  [`platform+`](#protosens.maestro.idiom.readme/platform+) - Prints <code>:maestro/platform+</code>.
-  [`protosens.maestro.idiom.stable`](#protosens.maestro.idiom.stable)  - Tagging stable releases following [calver](https://calver.org).
    -  [`all`](#protosens.maestro.idiom.stable/all) - Returns a list of stable tags in the repository.
    -  [`latest`](#protosens.maestro.idiom.stable/latest) - Returns the latest stable tag.
    -  [`tag->date`](#protosens.maestro.idiom.stable/tag->date) - Returns the date portion of the given stable tag.
    -  [`tag-add`](#protosens.maestro.idiom.stable/tag-add) - Tags the last commit as a stable release.
    -  [`tag?`](#protosens.maestro.idiom.stable/tag?) - Is the given <code>tag</code> a stable tag?.
    -  [`today`](#protosens.maestro.idiom.stable/today) - Returns a stable tag for a release done today.

-----

-----
# <a name="protosens.maestro.idiom.changelog">protosens.maestro.idiom.changelog</a>


Templating changelogs.

   Done using the [Selmer library](https://github.com/yogthos/Selmer)

   Biased towards a dual setup: a top changelog informs about which modules were impacted while
   each module maintains its own changelog containing details (if required).
  
   A notable usecase for templating changelogs are releases. One can have a placeholder like
   `{{ next-release }}` in those files and when releasing, template the actual tag of the next
   release everywhere.
  
   See [`main`](#protosens.maestro.idiom.changelog/main).




## <a name="protosens.maestro.idiom.changelog/main">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.idiom/src/main/clj/protosens/maestro/idiom/changelog.clj#L118-L143) `main`</a>
``` clojure

(main)
(main proto-basis)
```


Templates all changelogs.
  
   Successively calls [`top`](#protosens.maestro.idiom.changelog/top) and [`module+`](#protosens.maestro.idiom.changelog/module+).

   `proto-basis` may contain the following options:

   | Key                                    | Value                                               | Mandatory? | Default                |
   |----------------------------------------|-----------------------------------------------------|------------|------------------------|
   | `:maestro.idiom.changelog/ctx`         | Function taking a basis and return a Selmer context | Yes        | `nil`                  |
   | `:maestro.idiom.changelog.path/module` | Path to module changelog in each `:maestro/root`    | No         | `"doc/changelog.md"` |
   | `:maestro.idiom.changelog.path/top`    | Path to top changelog                               | No         | `"doc/changelog.md"` |

## <a name="protosens.maestro.idiom.changelog/module+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.idiom/src/main/clj/protosens/maestro/idiom/changelog.clj#L49-L85) `module+`</a>
``` clojure

(module+)
(module+ proto-basis)
```


Templates module changelogs.
  
   Each module that needs to document changes publicly should maintain its own changelog
   containing details only relevant to that module.

   See [`main`](#protosens.maestro.idiom.changelog/main) about options.

## <a name="protosens.maestro.idiom.changelog/top">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.idiom/src/main/clj/protosens/maestro/idiom/changelog.clj#L89-L112) `top`</a>
``` clojure

(top)
(top proto-basis)
```


Templates the top changelog.
  
   A repository should have a general changelog informing about what modules where impacted between
   releases.
  
   Modules should maintain their own changelogs containing details (see [`module+`](#protosens.maestro.idiom.changelog/module+)).

   See [`main`](#protosens.maestro.idiom.changelog/main) about options.

-----
# <a name="protosens.maestro.idiom.listing">protosens.maestro.idiom.listing</a>


Geneting a Markdown files listing existing modules.
  
   See [`main`](#protosens.maestro.idiom.listing/main).




## <a name="protosens.maestro.idiom.listing/main">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.idiom/src/main/clj/protosens/maestro/idiom/listing.clj#L113-L153) `main`</a>
``` clojure

(main path-list)
(main proto-basis path-list)
```


Generates a Markdown file under `path-list` listing exposed and private modules.
  
   This function is opinionated. For a more custom behavior, see [`module+`](#protosens.maestro.idiom.listing/module+) and possibly [`table`](#protosens.maestro.idiom.listing/table).

   `path-list` is typically the path to the `README.md` file in a directory hosting all those modules.

## <a name="protosens.maestro.idiom.listing/module+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.idiom/src/main/clj/protosens/maestro/idiom/listing.clj#L17-L72) `module+`</a>
``` clojure

(module+ path-list)
(module+ proto-basis path-list)
```


Returns modules exposed modules and private ones.
  
   More precisely, adds to the basis `:maestro.idiom.listing/public` pointing to modules having a
   `:maestro.module.expose/name` and `:maestro.idiom.listing/private` pointing to the rest.

   Reminder: modules are aliases with a `:maestro/root`.

   Alias data is also prepared:

   - `:maestro/doc` is truncated to its first line
   - `:maestro/root` is relativized to the parent directory of `path-list`

   `path-list` illustrates the file where those modules will be listed.

## <a name="protosens.maestro.idiom.listing/table">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.idiom/src/main/clj/protosens/maestro/idiom/listing.clj#L76-L107) `table`</a>
``` clojure

(table prepared-module+)
(table prepared-module+ option+)
```


Prints a Markdown table for the prepared modules.

   Either private or public modules from [`module+`](#protosens.maestro.idiom.listing/module+).

   Options may contain:

   | Key                           | Value                              | Default |
   |-------------------------------|------------------------------------|---------|
   | `:maestro.idiom.listing/name` | `(fn [alias-keyword] listed-name)` | `name`  |

-----
# <a name="protosens.maestro.idiom.readme">protosens.maestro.idiom.readme</a>


Generating READMEs for modules.
  
   This is what the Protosens monorepo uses for generating module READMEs that contain
   much needed information: how to use them as Git dependencies, which platforms they
   support, etc.

   These functions are opinionated and not meant for any situation. However, the general
   idea of general of printing these READMEs is somewhat flexible should anyone need to
   print anything else.

   See [`main`](#protosens.maestro.idiom.readme/main).




## <a name="protosens.maestro.idiom.readme/body">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.idiom/src/main/clj/protosens/maestro/idiom/readme.clj#L26-L44) `body`</a>
``` clojure

(body alias-data)
```


Prints a body of text.

   Some READMEs require only the generic information printed by other functions.
   Others require examples and explanations carefully written by a human.
  
   This function prints the file under `./doc/body.md` relative to the `maestro/root`
   of the alias if it exists.

## <a name="protosens.maestro.idiom.readme/default">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.idiom/src/main/clj/protosens/maestro/idiom/readme.clj#L142-L164) `default`</a>
``` clojure

(default alias-data)
```


Default README printer.
  
   Used by [`main`](#protosens.maestro.idiom.readme/main) unless overwritten.

   Successively calls:

   - [`header`](#protosens.maestro.idiom.readme/header)
   - [`doc`](#protosens.maestro.idiom.readme/doc)
   - [`git-dependency`](#protosens.maestro.idiom.readme/git-dependency)
   - [`platform+`](#protosens.maestro.idiom.readme/platform+)
   - [`body`](#protosens.maestro.idiom.readme/body)

## <a name="protosens.maestro.idiom.readme/doc">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.idiom/src/main/clj/protosens/maestro/idiom/readme.clj#L48-L56) `doc`</a>
``` clojure

(doc alias-data)
```


Prints `:maestro/doc`.
  
   After realigning it.

## <a name="protosens.maestro.idiom.readme/git-dependency">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.idiom/src/main/clj/protosens/maestro/idiom/readme.clj#L60-L85) `git-dependency`</a>
``` clojure

(git-dependency alias-data)
```


Prints how to consume the alias as a Git dependency in `deps.edn.`.
  
   This leverages a preparation step donc in [`main`](#protosens.maestro.idiom.readme/main). It merges into the input
   a delay under `:maestro.module/d*expose`. This delay resolves to a map
   containing what is necessary for specifying a full Git dependency:

   - `:maestro.module.expose/sha`
   - `:maestro.module.expose/tag`
   - `:maestro.module.expose/url`

## <a name="protosens.maestro.idiom.readme/header">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.idiom/src/main/clj/protosens/maestro/idiom/readme.clj#L89-L118) `header`</a>
``` clojure

(header alias-data)
```


Prints the first line of the README.
  
   A main title mentioning the `:maestro/root` with a link to the module API
   (see the `maestro.plugin.quickdoc` plugin) and a link to the changelog.

   Changelog is presumed to be under `./doc/changelog.md` by default. The basis
   or indiviual alias data can contain `:maestro.idiom.changelog.path/module`
   specifying an alternative path.

## <a name="protosens.maestro.idiom.readme/main">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.idiom/src/main/clj/protosens/maestro/idiom/readme.clj#L168-L220) `main`</a>
``` clojure

(main)
(main proto-basis)
```


Generates READMEs for all modules.
  
   More precisely, all aliases that have a `:maestro/root` (where their README will be
   printed).

   READMEs are printed using [`default`](#protosens.maestro.idiom.readme/default) by default. An alternative printer function can
   be provided under `:maestro.idiom.readme/print`. It is called for each alias after
   binding `*out*` to the relevant file writer, taking only one argument: the basis merged
   with the alias date of the currently handled alias.

## <a name="protosens.maestro.idiom.readme/platform+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.idiom/src/main/clj/protosens/maestro/idiom/readme.clj#L122-L136) `platform+`</a>
``` clojure

(platform+ alias-data)
```


Prints `:maestro/platform+`.
  
   Informing users which platforms this alias supports.

-----
# <a name="protosens.maestro.idiom.stable">protosens.maestro.idiom.stable</a>


Tagging stable releases following [calver](https://calver.org).

   See [`today`](#protosens.maestro.idiom.stable/today) about tag format.

   These are the utilities used by the Protosens monorepo but there is no
   obligation following all that.

   Some functions accept the following options:

   | Key    | Value                                     | Default           |
   |--------|-------------------------------------------|-------------------|
   | `:dir` | Directory used for Git-related operations | Current directory |




## <a name="protosens.maestro.idiom.stable/all">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.idiom/src/main/clj/protosens/maestro/idiom/stable.clj#L38-L53) `all`</a>
``` clojure

(all)
(all option+)
```


Returns a list of stable tags in the repository.

## <a name="protosens.maestro.idiom.stable/latest">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.idiom/src/main/clj/protosens/maestro/idiom/stable.clj#L57-L74) `latest`</a>
``` clojure

(latest)
(latest tag+)
```


Returns the latest stable tag

## <a name="protosens.maestro.idiom.stable/tag->date">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.idiom/src/main/clj/protosens/maestro/idiom/stable.clj#L110-L119) `tag->date`</a>
``` clojure

(tag->date tag)
```


Returns the date portion of the given stable tag.

## <a name="protosens.maestro.idiom.stable/tag-add">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.idiom/src/main/clj/protosens/maestro/idiom/stable.clj#L89-L106) `tag-add`</a>
``` clojure

(tag-add)
(tag-add option+)
```


Tags the last commit as a stable release.
  
   See [`today`](#protosens.maestro.idiom.stable/today).

## <a name="protosens.maestro.idiom.stable/tag?">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.idiom/src/main/clj/protosens/maestro/idiom/stable.clj#L78-L85) `tag?`</a>
``` clojure

(tag? tag)
```


Is the given `tag` a stable tag?

## <a name="protosens.maestro.idiom.stable/today">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/maestro.idiom/src/main/clj/protosens/maestro/idiom/stable.clj#L123-L155) `today`</a>
``` clojure

(today)
(today option+)
```


Returns a stable tag for a release done today.
  
   Format is `stable/YYYY-0M-0D`.

   If the tag already exists, appends an iterating `_%02d` portion.

   Hence, this format is suitable for a daily stable release at most, providing
   a bit of room for emergencies.
