# Table of contents
-  [`protosens.maestro.idiom.changelog`](#protosens.maestro.idiom.changelog)  - Templating changelogs.
    -  [`main`](#protosens.maestro.idiom.changelog/main) - Templates all changelogs.
    -  [`module+`](#protosens.maestro.idiom.changelog/module+) - Templates module changelogs.
    -  [`top`](#protosens.maestro.idiom.changelog/top) - Templates the top changelog.
-  [`protosens.maestro.idiom.stable`](#protosens.maestro.idiom.stable)  - Tagging stable releases following [calver](https://calver.org) See [[today]] about tag format.
    -  [`all`](#protosens.maestro.idiom.stable/all) - Returns a list of stable tags in the repository.
    -  [`latest`](#protosens.maestro.idiom.stable/latest) - Returns the latest stable tag.
    -  [`tag->date`](#protosens.maestro.idiom.stable/tag->date) - Returns the date portion of the given stable tag.
    -  [`tag-add`](#protosens.maestro.idiom.stable/tag-add) - Tags the last commit as a stable release.
    -  [`tag?`](#protosens.maestro.idiom.stable/tag?) - Is the given <code>tag</code> a stable tag?.
    -  [`today`](#protosens.maestro.idiom.stable/today) - Returns a stable tag for a release done today.

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
# <a name="protosens.maestro.idiom.stable">protosens.maestro.idiom.stable</a>


Tagging stable releases following [calver](https://calver.org)

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
