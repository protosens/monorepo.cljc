# Conventions

Code throughout the whole repository follows the following conventions.


---


## Directory structure

- Each module has a directory in [./module](../module) with `./src` and documentation
- `./src` is divided
    - First by purpose
        - `./main`
        - `./test`
        - etc
    - Then by language
        - `./clj`
        - `./cvx`
        - etc


---


## Naming

- Namespace aliases as close to their actual symbol as possible
- Namespaces from this repository starts always with `protosens.`
    - First segment is aliased as `$.`
    - Rest of the symbol is kept intact
    - E.g. `protosens.maestro` -> `$.maestro`)
- Pluralize with `+` instead of `s`
    - E.g. `items` -> `item+`
