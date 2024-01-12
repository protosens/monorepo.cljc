(ns protosens.test.git

  "Tests for `$.git`."

  (:require [babashka.fs   :as bb.fs]
            [clojure.test  :as T]
            [protosens.git :as $.git]))


;;;;;;;;;;


(T/deftest main

  (let [dir      (str (bb.fs/create-temp-dir))
        file-foo (str dir
                      "/foo.txt")
        file-bar (str dir
                      "/bar.txt")
        option+  {:dir dir}]

    (T/is (false? ($.git/repo? option+))
          "No repo yet")

    (T/is (true? ($.git/init option+))
          "Initialization new repo")

    (T/is (true? ($.git/repo? option+))
          "Repo now detected")

    (T/is (= "master"
             ($.git/branch option+))
          "Default branch")

    (T/is (zero? ($.git/count-commit+ option+))
          "No commit yet")

    (T/is (= []
             ($.git/tag+ option+))
          "No existing tag")

    (T/is (= []
             ($.git/branch+ option+))
          "No branch listed since no commit yet")

    (T/is (nil? ($.git/commit-sha 0
                                  option+))
          "Cannot find last SHA since no commit yet")

    (T/is (nil? ($.git/commit-message "dfqsdfsqdfqsdf"
                                      option+))
          "No commit message for a bad ref")

    (T/is (nil? ($.git/resolve "fdsmqfdlsmfk"
                               option+))
          "Cannot resolve any ref, there is nothing anyways")

    (T/is (false? ($.git/modified? option+))
          "There cannot be any change since nothing happened yet.")

    (T/is (true? ($.git/clean? option+))
          "Repo is completely clean, nothing has been added yet")

    (T/is (false? ($.git/unstaged? option+))
          "Nothing unstaged since no file has been added")

    (T/is (true? ($.git/checkout-new "develop"
                                     option+))
          "Creating new branch")

    (T/is (= "develop"
             ($.git/branch option+))
          "New branch checked out")

    (spit file-foo
          "Foo")

    (T/is (not ($.git/modified? option+))
          "No change detected since new file is not part of the index yet")

    (T/is (not ($.git/clean? option+))
          "Repo is not clean anymore, a file has been added.")

    (T/is (not ($.git/unstaged? option+))
          "New change not unstaged since not part of the index yet")

    (T/is (true? ($.git/add ["."]
                            option+))
          "Add all (new file)")

    (T/is (false? ($.git/modified? option+))
          "Still no actual change after adding the file since it is new ")

    (T/is (false? ($.git/clean? option+))
          "Repo cannot be clean since there is a file to commit")

    (T/is (false? ($.git/unstaged? option+))
          "Nothing can be deemed unstaged at this point")

    (T/is (true? ($.git/commit "First commit"
                               option+))
          "First committed changed")

    (T/is (= 1
             ($.git/count-commit+ option+)))

    (let [sha-1 ($.git/commit-sha 0
                                  option+)]

      (T/is ($.git/full-sha? sha-1)
            "SHA of first commit")

      (T/is (= "First commit"
               ($.git/commit-message sha-1
                                     option+))
            "Commit message correctly persisted")

      (T/is (= sha-1
               ($.git/resolve sha-1
                              option+))
            "Full SHA resolves to itself")

      (T/is (= ["develop"]
               ($.git/branch+ option+))
            "Now there is a commit, branch is listed")

      (T/is (false? ($.git/modified? option+))
            "There cannot be any change after a fresh commit")

      (T/is (true? ($.git/clean? option+))
            "Repo is completely clean after a fresh commit")
      
      (T/is (false? ($.git/unstaged? option+))
            "Nothing can be deemed unstaged after a fresh commit")

      (T/is (= "Foo"
               (slurp ($.git/show-file nil
                                       "foo.txt"
                                       option+))
               (slurp ($.git/show-file sha-1
                                       "foo.txt"
                                       option+)))
            "Can retrieve a committed file")

      (T/is (true? ($.git/tag-add "some-tag"
                                  option+))
            "Add tag to latest commit")

      (T/is (= sha-1
               ($.git/resolve "some-tag"
                              option+))
            "Tag resolve to expected commit")

      (T/is (= ["some-tag"]
               ($.git/tag+ option+))
            "Tag is listed")

      (spit file-foo
            "Foo 2")

      (T/is (true? ($.git/modified? option+))
            "File has been modified")

      (T/is (false? ($.git/clean? option+))
            "Repo is not clean after modifying a file")

      (T/is (true? ($.git/unstaged? option+))
            "Modified file is currently unstaged")

      (T/is (= ["foo.txt"]
               ($.git/diff-path+ "HEAD"
                                 nil
                                 ["foo.txt"]
                                 option+))
            "Diff paths against working tree")

      (T/is (true? ($.git/add ["."]
                              option+))
            "Staging modified file")

      (T/is (true? ($.git/modified? option+))
            "Change still detected even after staging the modified file")

      (T/is (false? ($.git/clean? option+))
            "Repo dirty even after staging the modified file")

      (T/is (false? ($.git/unstaged? option+))
            "File has been staged")

      (T/is (true? ($.git/commit "Second commit"
                                 option+))
            "Commit changed file")

      (T/is (= 2
               ($.git/count-commit+ option+))
            "Second commit persisted")

      (let [sha-2 ($.git/commit-sha 0
                                    option+)]

        (T/is ($.git/full-sha? sha-2)
              "SHA of second commit")

        (T/is (not= sha-1
                    sha-2)
              "SHAs of commits are different")

        (T/is (= "Second commit"
                 ($.git/commit-message sha-2
                                       option+))
              "Second message commit persisted")

        (T/is (true? ($.git/clean? option+))
              "Repo is clean again after second commit")

        (T/is (= ["foo.txt"]
                 ($.git/diff-path+ sha-1
                                   sha-2
                                   ["foo.txt"]
                                   option+))
              "Diff paths for 2 revisions")

        (spit file-bar
              "Bar")

        (T/is (false? ($.git/modified? option+))
              "No change detected after adding a second file")

        (T/is (false? ($.git/clean? option+))
              "Repo dirty after adding another file")

        (T/is (false? ($.git/unstaged? option+))
              "New file not considered unstaged")

        (T/is (true? ($.git/checkout-new "feature"
                                         option+))
              "Create new feature branch")

        (T/is (= "feature"
                 ($.git/branch option+))
              "New feature branch checked out")

        (T/is (= ["develop"
                  "feature"]
                 (sort ($.git/branch+ option+)))
              "All branches with commits listed")

        (T/is (true? ($.git/checkout sha-1
                                     option+))
              "Checking out the first commit")

        (T/is (nil? ($.git/branch option+))
              "No branch since checked out to a raw commit")))))



(T/deftest shorten-sha

  (T/is (= "1234567"
           ($.git/shorten-sha "12345678901234567890"))))



(T/deftest version

  (let [version ($.git/version)]
    (T/is (and (string? version)
               (seq version)))))
