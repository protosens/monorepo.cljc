(ns protosens.git

  "Quick Git-related utilities.
  
   Work both in Babashka and on the JVM.

   The purpose is not to provide an exhaustive Git API. Rather, a collection of one-liners (or almost)
   for carrying common operations, especially in the context of scripting.

   All Git operations are done by shelling out. Convenience over speed.

   See [[exec]]. Pretty much all functions from this namespace rely on it. It describes available options.

   For a fully-featured Clojure JVM client for Git, see [`clj-jgit`](https://github.com/clj-jgit/clj-jgit)."

  (:refer-clojure :exclude [resolve])
  (:require [babashka.process :as bb.process]
            [clojure.string   :as string]
            [protosens.txt    :as $.txt]))


;;;;;;;;;; Miscellaneous helpers


(defn full-sha?

  "Is `x` a full SHA?"

  [x]

  (and (string? x)
       (= (count x)
          40)))


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

   Returns a map:

   | Key     | Value                      |
   |---------|----------------------------|
   | `:err`  | Content of STDERR (string) |
   | `:exit` | Exit code                  |
   | `:out`  | Content of STDOUT (string) |

   Pretty much all functions of this namespace rely in this one."


  ([arg+]

   (exec arg+
         nil))


  ([arg+ option+]

   (let [{:keys [err
                 exit
                 out]} (bb.process/sh (cons (or (:command option+)
                                                "git")
                                            arg+)
                                      option+)]
     (cond->
       {:exit exit}
       ;;
       (seq err)
       (assoc :err
              err)
       ;;
       (seq out)
       (assoc :out
              out)))))


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
       (:exit)
       (zero?))))



(defn branch

  "Returns the current branch.
  
   Or nil if there is no branch currently checked out."


  ([]

   (branch nil))


  ([option+]

   (-> (exec ["branch" "--show-current"]
             option+)
       (:out)
       (some-> (string/trimr)))))



(defn branch+

  "Returns a vector of existing branches."

  ([]

   (branch+ nil))


  ([option+]

   (or (-> (exec ["branch"]
                 option+)
           (:out)
           (some-> (string/split-lines)
                   (->> (map (fn [branch]
                               ($.txt/trunc-left branch
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
       (:exit)
       (zero?))))



(defn checkout-new

  "`git checkout` a new `branch`."


  ([branch]

   (checkout-new branch
                 nil))


  ([branch option+]

   (-> (exec ["checkout" "-b" branch]
             option+)
       (:exit)
       (zero?))))



(defn clean?

  "Returns `true` if absolutely nothing changed since the last commit.

   Meaning no untracked files and no modifications (live nor staged)."


  ([]

   (clean? nil))


  ([option+]

   (-> (exec ["status" "--porcelain"]
             option+)
       (:out)
       (empty?))))



(defn commit

  "Commits current changes with the given `message`.

   Returns `true` in case of success, `false` in case of error (e.g. no changes
   to commit)."


  ([message]

   (commit message
           nil))


  ([message option+]

   (-> (exec ["commit" "-m" ($.txt/realign message)]
             option+)
       (:exit)
       (zero?))))



(defn commit-message

  "Returns the message of the given commit (by `ref`).
  
   Or nil if commit not found."

  ([ref]

   (commit-message ref
                   nil))


  ([ref option+]

   (let [result (exec ["show" "-s" "--format=%B" ref]
                      option+)]
     (when (zero? (result :exit))
       (string/trimr (result :out))))))



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
                 (:out)
                 (string/trimr))]
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
           (:exit)
           (zero?))
     (-> (exec ["rev-list" "--count" "HEAD"]
               option+)
         (:out)
         (string/trimr)
         (Long/parseLong))
     0)))



(defn init

  "Initializes a new Git repository.
  
   Returns `true` in case of success, `false` otherwise."


  ([]

   (init nil))


  ([option+]

   (-> (exec ["init"]
             option+)
       (:exit)
       (zero?))))



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
            (:exit)
            (pos?)))))



(defn repo?

  "Returns `true` if the working directory contains a Git repository."


  ([]
   
   (repo? nil))


  ([option+]

   (if-some [out (-> (exec ["rev-parse" "--is-inside-work-tree"]
                           option+)
                     (:out))]
     (-> out
         (string/trimr)
         (= "true"))
     false)))



(defn resolve

  "Resolves the given `ref` (e.g. a tag) to a full SHA.
  
   Or returns `nil` if it does not resolve to anything."


  ([ref]

   (resolve ref
            nil))


  ([ref option+]

   (let [out (-> (exec ["rev-parse" ref]
                       option+)
                 (:out)
                 (string/trimr))]
     (when (full-sha? out)
       out))))



(defn tag+

  "Returns a vector of existing tags."


  ([]

   (tag+ nil))


  ([option+]

   (or (-> (exec ["tag"]
                 option+)
           (:out)
           (some-> (string/split-lines)))
       [])))



(defn tag-add

  "`git tag` the last commit with `tag`."


  ([tag]

   (tag-add tag
            nil))


  ([tag option+]

   (-> (exec ["tag" tag]
             option+)
       (:exit)
       (zero?))))



(defn unstaged?

  "Returns `true` if some changes are unstaged."

  
  ([]

   (unstaged? nil))


  ([option+]

   (-> (exec ["diff" "--quiet"]
             option+)
       (:exit)
       (pos?))))



(defn version

  "Returns Git's version."


  ([]

   (version nil))


  ([option+]

   (-> (exec ["--version"]
             option+)
       (:out)
       (string/trimr))))
