# `Monorepo.cljc`

Nascent public monorepo hosting libraries and tools for the Clojure/script
ecosystem.

Created, used, and maintained by Protosens SRL.


---


## Public work

Modules exposed publicly are listed in [`./module`](./module) and can be
consumed as [Git
dependencies](https://clojure.org/guides/deps_and_cli#_using_git_libraries) by
[Clojure CLI](https://clojure.org/guides/deps_and_cli).

This repository uses [calver](https://calver.org) and follows best effort
towards avoiding known breaking changes in publicly exposed modules. All
consumed modules must be required with the same `stable/YYYY-0M-0D` tag.


---


## Structure

This repository is organized and managed with [Maestro](./module/maestro), a
novel paradigm

- Whole repository is described and documented in [`deps.edn`](./deps.edn)
- Structured around small, well-scoped "modules" represented aliases
- Modules are trivial to combine, extreme flexibility
- Any module can be easily exposed as a Git dependency
- All dependencies are pinned and synced between modules
- All API documentation is generated as Markdown and kept in the repository
- Everything is automated and aims to minimize human errors


---


## License

Copyright © 2022 Protosens SRL

Licensed under the Mozilla Public License, Version 2.0 (see LICENSE)
