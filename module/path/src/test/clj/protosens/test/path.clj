(ns protosens.test.path

  (:require [clojure.test   :as T]
            [protosens.path :as $.path]))


;;;;;;;;;; Preparations


(def -dir
     (System/getProperty "user.dir"))


;;;;;;;;;; Tests


(T/deftest absolute

  (let [path ($.path/from-string "/foo")]
    (T/is (= path
             ($.path/absolute path))
          "Already absolute"))

  (T/is (= ($.path/from-string+ [-dir
                                 "foo"])
           ($.path/absolute ($.path/from-string "foo")))
        "From relative"))



(T/deftest canonical

  (T/is (= ($.path/from-string "/foo/baz")
           (-> "////lol//./../foo//././bar/../bar/../baz/"
               ($.path/from-string)
               ($.path/canonical)))
        "Absolute")

  (T/is (= (-> "foo/baz"
               ($.path/from-string)
               ($.path/absolute))
           (-> ".////foo/././../foo//bar/././..///baz///"
               ($.path/from-string)
               ($.path/canonical)))
        "Relative"))



(T/deftest canonical+

  (T/is (= #{}
           ($.path/canonical+ nil))
        "Empty")

  (T/is (= #{($.path/from-string "/a/b")
             ($.path/from-string "/c/d")
             ($.path/from-string+ [-dir
                                   "e"])}
           (-> (map $.path/from-string
                    ["///a/././b//"
                     "//////c///////d///"
                     "./././e/.././e//"])
               ($.path/canonical+)))))



(T/deftest from-string

  (T/is ($.path/is? ($.path/from-string "foo")))

  (T/is (= "foo"
           (str ($.path/from-string "foo")))))



(T/deftest from-string+

  (T/is (= ($.path/from-string "a/b")
           ($.path/from-string+ ["a"
                                 "b"]))))



(T/deftest is?

  (T/is (true? ($.path/is? (.getFileName ($.path/from-string "foo/bar"))))))



(T/deftest normalized

  (let [path ($.path/from-string "foo/bar")]
    (T/is (= path
             ($.path/normalized path))
          "Already normal"))

  (T/is (= ($.path/from-string "b/c/f")
           (-> "././a/..///./b/c/./d/e/../../f///"
               ($.path/from-string)
               ($.path/normalized)))))



(T/deftest normalized+

  (T/is (= #{}
           ($.path/normalized+ nil))
        "Empty")

  (T/is (= #{($.path/from-string "a/b")
             ($.path/from-string "/c/d")}
           (-> (map $.path/from-string
                    ["././a/../a/../a////b/"
                     "////c/././d/"])
               ($.path/normalized+)))))



(T/deftest starts-with?

  (T/is (true? ($.path/starts-with? ($.path/from-string "/a/b")
                                    ($.path/from-string "/a")))
        "Absolute")

  (T/is (true? ($.path/starts-with? ($.path/from-string "/a/b////c")
                                    ($.path/from-string "/a///b/")))
        "Absolute, no need to normalize")

  (T/is (true? ($.path/starts-with? ($.path/from-string "a/b")
                                    ($.path/from-string "a")))
        "Relative")

  (T/is (true? ($.path/starts-with? ($.path/from-string "a/b////c")
                                    ($.path/from-string "a///b/")))
        "Relative, no need to normalize")

  (T/is (false? ($.path/starts-with? ($.path/from-string "/a/b")
                                     ($.path/from-string "/c")))
        "Absolute, negative")

  (T/is (false? ($.path/starts-with? ($.path/from-string "a/b")
                                     ($.path/from-string "c")))
        "Relative, negative"))


;;;;;;;;;;


(T/deftest coerce

  (let [path ($.path/from-string "foo")]

    (T/is (= path
             ($.path/coerce path))
          "Already a Path")

    (T/is (= path
             ($.path/coerce "foo"))
          "From a string")))

