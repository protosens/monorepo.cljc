# `module/deps.edn` - [API](doc/API.md)  - [CHANGES](doc/changelog.md)

Handling `deps.edn` files.

```clojure
;; Add to dependencies in `deps.edn`:
;;
protosens/deps.edn
{:deps/root "module/deps.edn"
 :git/sha   "98f817a"
 :git/tag   "stable/2022-10-10"
 :git/url   "https://github.com/protosens/monorepo.cljc"}
```

```clojure
;; Supported platforms:
;;
[:bb :jvm]
```

