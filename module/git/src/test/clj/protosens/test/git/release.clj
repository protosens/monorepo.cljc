(ns protosens.test.git.release

  (:require [babashka.fs           :as bb.fs]
            [clojure.test          :as T]
            [protosens.git         :as $.git]
            [protosens.git.release :as $.git.release]))


;;;;;;;;;; Helpers


(defn- -with-repo

  [f]

  (let [dir (str (bb.fs/create-temp-dir))]
    (try
      ;;
      (let [option+ {:dir dir}]
        ($.git/init option+)
        (spit (str dir
                   "/foo.txt")
              "test")
        ($.git/add ["."]
                   option+)
        ($.git/commit "First commit"
                      option+)
        (f option+))
      ;;
      (finally
        (bb.fs/delete-tree dir)))))


;;;;;;;;;; Tests


(T/deftest latest

  (-with-repo
    (fn [option+]
      

      (T/is (nil? ($.git.release/latest option+))
            "Not tagged yet")


      ($.git.release/tag-add "foo"
                             option+)
      (spit (str (option+ :dir)
                 "/bar.txt")
            "test")
      ($.git/add ["."]
                 option+)
      ($.git/commit "Second commit"
                    option+)
      ($.git.release/tag-add "bar"
                             option+)


      (T/is (= [($.git/commit-sha 0
                                  option+)
                "release/bar"]
               ($.git.release/latest option+))
            "Tag retrieved"))))



(T/deftest tag-add

  (-with-repo
    (fn [option+]
      ($.git.release/tag-add "foo"
                             option+)
      (T/is (= "release/foo"
               (first ($.git/tag+ option+)))))))
