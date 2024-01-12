(ns protosens.git

  "Quick Git-related utilities.

   The purpose is not to provide an exhaustive Git API. Rather, a collection of one-liners (or almost)
   for carrying common operations, especially in the context of scripting.

   All Git operations are done by shelling out. Convenience over speed.

   See [[exec]]. Pretty much all functions from this namespace rely on it. It describes available options.

   For a fully-featured Clojure JVM client for Git, see [`clj-jgit`](https://github.com/clj-jgit/clj-jgit)."

  (:refer-clojure :exclude [resolve])
  (:require [clojure.string    :as C.string]
            [protosens.process :as $.process]
            [protosens.string  :as $.string]))


;;;;;;;;;; Miscellaneous helpers


(defn full-sha?

  "Is `x` a full SHA?"

  [x]

  (and (string? x)
       (= (count x)
          40)))



(defn shorten-sha

  "Returns a short SHA of the given `full-sha`."

  [full-sha]

  ($.string/n-first full-sha
                    7))


;;;;;;;;;; Master command


(defn exec

  "Executes a Git command in the shell.

   Takes a vector of arguments for the command.

   For instance:

   ```clojure
   (exec [\"log\" \"-10\" \"--pretty=oneline\"])
   ```

   Options may be:

   | Key         | Value                       | Default     |
   |-------------|-----------------------------|-------------| 
   | `:command`  | Git command                 | `\"git\"`   | 
   | `:env`      | Map of env variables to set | /           |
   | `:dir`      | Working directory           | Current dir |

   Returns a process that can be handled with [protosens.process](https://github.com/protosens/monorepo.cljc/tree/develop/module/process).

   Pretty much all functions of this namespace rely in this one."


  ([arg+]

   (exec arg+
         nil))


  ([arg+ option+]

   ($.process/run (cons (or (:command option+)
                                  "git")
                              arg+)
                        option+)))


;;;;;;;;;; Quick commands


(defn add

  "`git add` the given paths."


  ([path+]

   (add path+
        nil))


  ([path+ option+]

   (-> (exec (cons "add"
                    path+)
             option+)
       ($.process/success?))))



(defn branch

  "Returns the current branch.
  
   Or nil if there is no branch currently checked out."


  ([]

   (branch nil))


  ([option+]

   (-> (exec ["branch" "--show-current"]
             option+)
       ($.process/out))))



(defn branch+

  "Returns a vector of existing branches."

  ([]

   (branch+ nil))


  ([option+]

   (or (-> (exec ["branch"]
                 option+)
           ($.process/out)
           (some-> (C.string/split-lines)
                   (->> (map (fn [branch]
                               ($.string/trunc-left branch
                                                    2))))))
       [])))



(defn checkout

  "`git checkout` the given `branch`."


  ([branch]

   (checkout branch
             nil))


  ([branch option+]

   (-> (exec ["checkout" branch]
             option+)
       ($.process/success?))))



(defn checkout-new

  "`git checkout` a new `branch`."


  ([branch]

   (checkout-new branch
                 nil))


  ([branch option+]

   (-> (exec ["checkout" "-b" branch]
             option+)
       ($.process/success?))))



(defn clean?

  "Returns `true` if absolutely nothing changed since the last commit.

   Meaning no untracked files and no modifications (live nor staged)."


  ([]

   (clean? nil))


  ([option+]

   (-> (exec ["status" "--porcelain"]
             option+)
       ($.process/out)
       (nil?))))



(defn commit

  "Commits current changes with the given `message`.

   Returns `true` in case of success, `false` in case of error (e.g. no changes
   to commit)."


  ([message]

   (commit message
           nil))


  ([message option+]

   (-> (exec ["commit" "-m" ($.string/realign message)]
             option+)
       ($.process/success?))))



(defn commit-message

  "Returns the message of the given commit (by `rev`).
  
   Or nil if commit not found."

  ([rev]

   (commit-message rev
                   nil))


  ([rev option+]

   (let [process (exec ["show" "-s" "--format=%B" rev]
                       option+)]
     (when ($.process/success? process)
       ($.process/out process)))))



(defn commit-sha

  "Returns the full SHA of the last `i`est commit.

   `0` for the very last commit, `1` for the one before, etc.

   Returns `nil` if `i` goes beyond the current history."


  ([i]

   (commit-sha i
               nil))


  ([i option+]

   (let [out (-> (exec ["rev-parse" (str "HEAD~"
                                         (or i
                                             0))]
                       option+)
                 ($.process/out))]
     (when (full-sha? out)
       out))))



(defn count-commit+

  "Returns the number of commits in the current history.

   Or nil if there isn't any."


  ([]

   (count-commit+ nil))


  ([option+]

   (if (-> (exec ["log" "-0"]
                 option+)
           ($.process/success?))
     (-> (exec ["rev-list" "--count" "HEAD"]
               option+)
         ($.process/out)
         (Long/parseLong))
     0)))



(defn diff-path+


  ([rev-old rev-new path+]

   (diff-path+ rev-old
               rev-new
               path+
               nil))


  ([rev-old rev-new path+ option+]

   (-> (exec (concat ["diff"
                      "--name-only"]
                     (if rev-new
                       [rev-old
                        rev-new]
                       [rev-old])
                     ["--"]
                     path+)
             option+)
       (:out)
       (slurp)
       (C.string/split (re-pattern ($.string/newline))))))



(defn init

  "Initializes a new Git repository.
  
   Returns `true` in case of success, `false` otherwise."


  ([]

   (init nil))


  ([option+]

   (-> (exec ["init"]
             option+)
       ($.process/success?))))



(defn modified?

  "Returns true if a versioned file has been modified.
  
   Staged or live."

  ;; Anything different in versioned files, staged or not.
  
  ([]

   (modified? nil))


  ([option+]

   (and (boolean (commit-sha nil
                             option+))
        (-> (exec ["diff-index" "--quiet" "HEAD"]
                  option+)
            ($.process/success?)
            (not)))))



(defn repo?

  "Returns `true` if the working directory contains a Git repository."


  ([]
   
   (repo? nil))


  ([option+]

   (or (-> (exec ["rev-parse" "--is-inside-work-tree"]
                 option+)
           ($.process/out)
           (some-> (= "true")))
       false)))



(defn resolve

  "Resolves the given `rev` (e.g. a tag) to a full SHA.
  
   Or returns `nil` if it does not resolve to anything."


  ([rev]

   (resolve rev
            nil))


  ([rev option+]

   (let [out (-> (exec ["rev-parse" rev]
                       option+)
                 ($.process/out))]
     (when (full-sha? out)
       out))))



(defn show-file

  "If found, returns an `InputStream` of the file at `path` from revision `rev` (defaults to `\"HEAD\"`)."

  
  ([rev path]

   (show-file rev
              path
              nil))


  ([rev path option+]

   (let [rev-2   (or rev
                     "HEAD")
         process (exec ["show"
                        (format "%s:%s"
                                rev-2
                                path)]
                       option+)]
     (when ($.process/success? process)
       (:out process)))))



(defn tag+

  "Returns a vector of existing tags."


  ([]

   (tag+ nil))


  ([option+]

   (or (-> (exec ["tag"]
                 option+)
           ($.process/out)
           (some-> (C.string/split-lines)))
       [])))



(defn tag-add

  "`git tag` the last commit with `tag`."


  ([tag]

   (tag-add tag
            nil))


  ([tag option+]

   (-> (exec ["tag" tag]
             option+)
       ($.process/success?))))



(defn unstaged?

  "Returns `true` if some changes are unstaged."

  
  ([]

   (unstaged? nil))


  ([option+]

   (-> (exec ["diff" "--quiet"]
             option+)
       ($.process/success?)
       (not))))



(defn version

  "Returns Git's version."


  ([]

   (version nil))


  ([option+]

   (-> (exec ["--version"]
             option+)
       ($.process/out))))
