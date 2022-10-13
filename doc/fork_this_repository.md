# Fork this repository

Required tools:

- [Clojure CLI](https://clojure.org/guides/install_clojure)
- [Babashka](https://github.com/babashka/babashka)


This monorepo is managed with [Maestro](../module/maestro).

Modules have aliases namespaced as `:module/...` in [`deps.edn`](../deps.edn).


---


## First steps

After cloning the repository, initialize it:

    bb genesis

List available tasks:

    bb tasks

More information about a task:

    bb help <task>

All tasks must always be executed from the root of this repository.


---


## Dev


Start dev mode for a single module:

    bb dev :module/maestro

Or any number of modules and personal aliases from your `~/.clojure/deps.edn`:

    bb dev '[:module/maestro :module/process ...]'


---


## Test


Test a module by providing its alias:

    bb test :module/maestro

---


## Notes

Also see [Conventions](./conventions.md).
