# Table of contents
-  [`protosens.git`](#protosens.git)  - Quick Git-related utilities.
    -  [`add`](#protosens.git/add) - <code>git add</code> the given paths.
    -  [`branch`](#protosens.git/branch) - Returns the current branch.
    -  [`branch+`](#protosens.git/branch+) - Returns a vector of existing branches.
    -  [`checkout`](#protosens.git/checkout) - <code>git checkout</code> the given <code>branch</code>.
    -  [`checkout-new`](#protosens.git/checkout-new) - <code>git checkout</code> a new <code>branch</code>.
    -  [`clean?`](#protosens.git/clean?) - Returns <code>true</code> if absolutely nothing changed since the last commit.
    -  [`commit`](#protosens.git/commit) - Commits current changes with the given <code>message</code>.
    -  [`commit-message`](#protosens.git/commit-message) - Returns the message of the given commit (by <code>ref</code>).
    -  [`commit-sha`](#protosens.git/commit-sha) - Returns the full SHA of the last <code>i</code>est commit.
    -  [`count-commit+`](#protosens.git/count-commit+) - Returns the number of commits in the current history.
    -  [`exec`](#protosens.git/exec) - Executes a Git command in the shell.
    -  [`full-sha?`](#protosens.git/full-sha?) - Is <code>x</code> a full SHA?.
    -  [`init`](#protosens.git/init) - Initializes a new Git repository.
    -  [`modified?`](#protosens.git/modified?) - Returns true if a versioned file has been modified.
    -  [`repo?`](#protosens.git/repo?) - Returns <code>true</code> if the working directory contains a Git repository.
    -  [`resolve`](#protosens.git/resolve) - Resolves the given <code>ref</code> (e.g.
    -  [`tag+`](#protosens.git/tag+) - Returns a vector of existing tags.
    -  [`tag-add`](#protosens.git/tag-add) - <code>git tag</code> the last commit with <code>tag</code>.
    -  [`unstaged?`](#protosens.git/unstaged?) - Returns <code>true</code> if some changes are unstaged.
    -  [`version`](#protosens.git/version) - Returns Git's version.

-----
# <a name="protosens.git">protosens.git</a>


Quick Git-related utilities.

   The purpose is not to provide an exhaustive Git API. Rather, a collection of one-liners (or almost)
   for carrying common operations, especially in the context of scripting.

   All Git operations are done by shelling out. Convenience over speed.

   See [`exec`](#protosens.git/exec). Pretty much all functions from this namespace rely on it. It describes available options.

   For a fully-featured Clojure JVM client for Git, see [`clj-jgit`](https://github.com/clj-jgit/clj-jgit).




## <a name="protosens.git/add">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/git/src/main/clj/protosens/git.clj#L79-L95) `add`</a>
``` clojure

(add path+)
(add path+ option+)
```


`git add` the given paths.

## <a name="protosens.git/branch">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/git/src/main/clj/protosens/git.clj#L99-L115) `branch`</a>
``` clojure

(branch)
(branch option+)
```


Returns the current branch.
  
   Or nil if there is no branch currently checked out.

## <a name="protosens.git/branch+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/git/src/main/clj/protosens/git.clj#L119-L140) `branch+`</a>
``` clojure

(branch+)
(branch+ option+)
```


Returns a vector of existing branches.

## <a name="protosens.git/checkout">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/git/src/main/clj/protosens/git.clj#L144-L159) `checkout`</a>
``` clojure

(checkout branch)
(checkout branch option+)
```


`git checkout` the given `branch`.

## <a name="protosens.git/checkout-new">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/git/src/main/clj/protosens/git.clj#L163-L178) `checkout-new`</a>
``` clojure

(checkout-new branch)
(checkout-new branch option+)
```


`git checkout` a new `branch`.

## <a name="protosens.git/clean?">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/git/src/main/clj/protosens/git.clj#L182-L199) `clean?`</a>
``` clojure

(clean?)
(clean? option+)
```


Returns `true` if absolutely nothing changed since the last commit.

   Meaning no untracked files and no modifications (live nor staged).

## <a name="protosens.git/commit">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/git/src/main/clj/protosens/git.clj#L203-L221) `commit`</a>
``` clojure

(commit message)
(commit message option+)
```


Commits current changes with the given `message`.

   Returns `true` in case of success, `false` in case of error (e.g. no changes
   to commit).

## <a name="protosens.git/commit-message">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/git/src/main/clj/protosens/git.clj#L225-L242) `commit-message`</a>
``` clojure

(commit-message ref)
(commit-message ref option+)
```


Returns the message of the given commit (by `ref`).
  
   Or nil if commit not found.

## <a name="protosens.git/commit-sha">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/git/src/main/clj/protosens/git.clj#L246-L269) `commit-sha`</a>
``` clojure

(commit-sha i)
(commit-sha i option+)
```


Returns the full SHA of the last `i`est commit.

   `0` for the very last commit, `1` for the one before, etc.

   Returns `nil` if `i` goes beyond the current history.

## <a name="protosens.git/count-commit+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/git/src/main/clj/protosens/git.clj#L273-L294) `count-commit+`</a>
``` clojure

(count-commit+)
(count-commit+ option+)
```


Returns the number of commits in the current history.

   Or nil if there isn't any.

## <a name="protosens.git/exec">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/git/src/main/clj/protosens/git.clj#L37-L73) `exec`</a>
``` clojure

(exec arg+)
(exec arg+ option+)
```


Executes a Git command in the shell.

   Takes a vector of arguments for the command.

   For instance:

   ```clojure
   (exec ["log" "-10" "--pretty=oneline"])
   ```

   Options may be:

   | Key         | Value                       | Default     |
   |-------------|-----------------------------|-------------| 
   | `:command`  | Git command                 | `"git"`   | 
   | `:env`      | Map of env variables to set | /           |
   | `:dir`      | Working directory           | Current dir |

   Returns a process that can be handled with [protosens.process](https://github.com/protosens/monorepo.cljc/tree/develop/module/process).

   Pretty much all functions of this namespace rely in this one.

## <a name="protosens.git/full-sha?">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/git/src/main/clj/protosens/git.clj#L23-L31) `full-sha?`</a>
``` clojure

(full-sha? x)
```


Is `x` a full SHA?

## <a name="protosens.git/init">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/git/src/main/clj/protosens/git.clj#L298-L314) `init`</a>
``` clojure

(init)
(init option+)
```


Initializes a new Git repository.
  
   Returns `true` in case of success, `false` otherwise.

## <a name="protosens.git/modified?">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/git/src/main/clj/protosens/git.clj#L318-L338) `modified?`</a>
``` clojure

(modified?)
(modified? option+)
```


Returns true if a versioned file has been modified.
  
   Staged or live.

## <a name="protosens.git/repo?">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/git/src/main/clj/protosens/git.clj#L342-L358) `repo?`</a>
``` clojure

(repo?)
(repo? option+)
```


Returns `true` if the working directory contains a Git repository.

## <a name="protosens.git/resolve">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/git/src/main/clj/protosens/git.clj#L362-L381) `resolve`</a>
``` clojure

(resolve ref)
(resolve ref option+)
```


Resolves the given `ref` (e.g. a tag) to a full SHA.
  
   Or returns `nil` if it does not resolve to anything.

## <a name="protosens.git/tag+">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/git/src/main/clj/protosens/git.clj#L385-L401) `tag+`</a>
``` clojure

(tag+)
(tag+ option+)
```


Returns a vector of existing tags.

## <a name="protosens.git/tag-add">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/git/src/main/clj/protosens/git.clj#L405-L420) `tag-add`</a>
``` clojure

(tag-add tag)
(tag-add tag option+)
```


`git tag` the last commit with `tag`.

## <a name="protosens.git/unstaged?">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/git/src/main/clj/protosens/git.clj#L424-L439) `unstaged?`</a>
``` clojure

(unstaged?)
(unstaged? option+)
```


Returns `true` if some changes are unstaged.

## <a name="protosens.git/version">[:page_facing_up:](https://github.com/protosens/monorepo.cljc/blob/develop/module/git/src/main/clj/protosens/git.clj#L443-L457) `version`</a>
``` clojure

(version)
(version option+)
```


Returns Git's version.

-----
