(ns protosens.test.maestro.idiom.stable

  "Tests `$.maestro.idiom.stable`."

  (:require [babashka.fs                    :as bb.fs]
            [clojure.test                   :as T]
            [protosens.git                  :as $.git]
            [protosens.maestro.idiom.stable :as $.maestro.idiom.stable]))


;;;;;;;;;;


(T/deftest -git

  ;; Tests Git-related operations.

  (let [dir (str (bb.fs/create-temp-dir))]

    ($.git/init {:dir dir})

    (T/is (empty? ($.maestro.idiom.stable/all {:dir dir}))
          "No tags yet")

    (spit (str dir
               "/foo.txt")
          "Foo")
    (T/is (true? ($.git/add ["."]
                            {:dir dir}))
          "Add a file")
    (T/is (true? ($.git/commit "Add `foo.txt`"
                               {:dir dir}))
          "Commit new file")

    (T/is ($.maestro.idiom.stable/tag? ($.maestro.idiom.stable/tag-add {:dir dir}))
          "Add first stable tag")

    (T/is (= 1
             (count ($.maestro.idiom.stable/all {:dir dir})))
          "First stable tag added indeed")

    (T/is ($.maestro.idiom.stable/tag? ($.maestro.idiom.stable/tag-add {:dir dir}))
          "Add second stable tag to ensure another one is created")

    (T/is (= 2
             (count ($.maestro.idiom.stable/all {:dir dir})))
          "Second stable tag added indeed")))



(T/deftest latest

  (T/is (true? ($.maestro.idiom.stable/tag? ($.maestro.idiom.stable/latest)))
        "Without providing a list of tags")

  (T/is (= "2022-10-11"
           ($.maestro.idiom.stable/latest ["2020-10-11"
                                           "2022-10-10"
                                           "2022-10-11"
                                           "2022-10-08"]))
        "Providing a list of tags")

  (T/is (nil? ($.maestro.idiom.stable/latest []))
        "Nothing to sort through"))



(T/deftest tag?

  (T/is (true? ($.maestro.idiom.stable/tag? "stable/xxx")))

  (T/is (false? ($.maestro.idiom.stable/tag? "foo/xxx"))))



(T/deftest tag->date

  (T/is (= "2022-10-11"
           ($.maestro.idiom.stable/tag->date "stable/2022-10-11"))
        "Valid stable tag")

  (T/is (nil? ($.maestro.idiom.stable/tag->date "foo/2022-10-11"))
        "Not a stable tag"))



(T/deftest today

  (T/is (true? ($.maestro.idiom.stable/tag? ($.maestro.idiom.stable/today)))))
