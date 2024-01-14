(ns protosens.test.maestro.plugin.gitlib

  (:require [babashka.fs                            :as       bb.fs]
            [clojure.test                           :as       T]
            [protosens.edn.read                     :as       $.edn.read]
            [protosens.maestro                      :as-alias $.maestro]
            [protosens.maestro.plugin.gitlib        :as       $.maestro.plugin.gitlib]
            [protosens.path                         :as       $.path]
            [protosens.test.util.maestro            :as       $.test.util.maestro]))


;;;;;;;;;;


(def -state-prepared

  ;; Sometimes messing around with paths to ensure they are handled
  ;; in their normalized form.

  {::$.maestro.plugin.gitlib/alias
   :a
   ,
   ::$.maestro.plugin.gitlib/definition
   {:extra-deps                 {'dep/a {1 2}}
    :extra-paths                ["././a////src"]
    :maestro/require            [:b]
    :maestro/root               ".////a///"
    :maestro.plugin.gitlib/name 'exposed/a}
   ,
   ::$.maestro.plugin.gitlib/dep+
   {'dep/a     {1 2}
    'dep/b     {3 4}
    'exposed/b {:deps/root "b"
                :git/sha   "SHA"
                :git/url   "URL"}}
   ,
   ::$.maestro.plugin.gitlib/deps.edn
   {:deps  {'dep/a    {1 2}
            'dep/b    {3 4}
            'exposed/b {:deps/root "b"
                        :git/sha   "SHA"
                        :git/url   "URL"}}
    :paths ["src"]}
   ,
   ::$.maestro.plugin.gitlib/path+
   ["src"]
   ,
   ::$.maestro.plugin.gitlib/require
   [:b]
   ,
   ::$.maestro.plugin.gitlib/root
   ($.path/from-string "a")
   ,
   ::$.maestro.plugin.gitlib/sha
   "SHA"
   ::$.maestro.plugin.gitlib/url
   "URL"
   ,
   ::$.maestro/deps.edn
   {:aliases (sorted-map
               :a {:extra-deps                 {'dep/a {1 2}}
                   :extra-paths                ["././a////src"]
                   :maestro/require            [:b]
                   :maestro/root               ".////a///"
                   :maestro.plugin.gitlib/name 'exposed/a}
               ,
               :b {:extra-deps      {'dep/b {3 4}}
                   :extra-paths     ["b/src"]
                   :maestro/root    "b"
                   :maestro.plugin.gitlib/name 'exposed/b}
               ,
               :c {:extra-deps      {'dep/c {5 6}}
                   :extra-paths     ["c/src"]
                   :maestro/root    "c"
                   :maestro.plugin.gitlib/name 'expose/c})}})
    


(defn -t-augmented


  ([f k-input+ k-output+]

   (-t-augmented f
                 k-input+
                 k-output+
                 nil))


  ([f k-input+ k-output+ message]

   (let [input  (select-keys -state-prepared
                             k-input+)
         output (select-keys -state-prepared
                             (concat k-input+
                                     k-output+))]
     (T/is (= output
              (f input))
           message))))


;;;;;;;;;;


(T/deftest flat-deps-edn

  (-t-augmented
    $.maestro.plugin.gitlib/flat-deps-edn
    ,
    [::$.maestro.plugin.gitlib/dep+
     ::$.maestro.plugin.gitlib/path+]
    ,
    [::$.maestro.plugin.gitlib/deps.edn]))



(T/deftest dep+

  (-t-augmented
    $.maestro.plugin.gitlib/dep+
    ,
    [::$.maestro.plugin.gitlib/alias
     ::$.maestro.plugin.gitlib/definition
     ::$.maestro.plugin.gitlib/require
     ::$.maestro.plugin.gitlib/sha
     ::$.maestro.plugin.gitlib/url
     ::$.maestro/deps.edn]
    ,
    [::$.maestro.plugin.gitlib/dep+]))



(T/deftest required

  (-t-augmented
    $.maestro.plugin.gitlib/required
    ,
    [::$.maestro.plugin.gitlib/alias
     ::$.maestro/deps.edn]
    ,
    [::$.maestro.plugin.gitlib/require]))



(T/deftest path+

  (-t-augmented
    $.maestro.plugin.gitlib/path+
    ,
    [::$.maestro.plugin.gitlib/alias
     ::$.maestro.plugin.gitlib/definition
     ::$.maestro.plugin.gitlib/root]
    ,
    [::$.maestro.plugin.gitlib/path+]))



(T/deftest root

  (-t-augmented
    $.maestro.plugin.gitlib/root
    ,
    [::$.maestro.plugin.gitlib/alias
     ::$.maestro.plugin.gitlib/definition]
    ,
    [::$.maestro.plugin.gitlib/root]))


;;;


(T/deftest prepare

  (-t-augmented
    $.maestro.plugin.gitlib/prepare
    ,
    [::$.maestro.plugin.gitlib/alias
     ::$.maestro.plugin.gitlib/sha
     ::$.maestro.plugin.gitlib/url
     ::$.maestro/deps.edn]
    ,
    [::$.maestro.plugin.gitlib/definition
     ::$.maestro.plugin.gitlib/dep+
     ::$.maestro.plugin.gitlib/deps.edn
     ::$.maestro.plugin.gitlib/path+
     ::$.maestro.plugin.gitlib/require
     ::$.maestro.plugin.gitlib/root]
    ,
    "Success")

  ($.test.util.maestro/t-fail*
    (-> -state-prepared
        (update-in [::$.maestro/deps.edn
                    :aliases
                    :b]
                   dissoc
                   :maestro.plugin.gitlib/name)
        $.maestro.plugin.gitlib/prepare)
    "Required module not exposed"))



(T/deftest write-prepared

  (let [deps-edn {:foo :bar}
        root     (bb.fs/create-temp-dir)]
    (try
      (let [state   {::$.maestro.plugin.gitlib/deps.edn deps-edn
                     ::$.maestro.plugin.gitlib/root     root}
            state-2 ($.maestro.plugin.gitlib/write-prepared state)
            output  (state-2 ::$.maestro.plugin.gitlib/output)]
        ;;
        (T/is (= output
                 (str root
                      "/deps.edn"))
              "Right path")
        ;;
        (T/is (= deps-edn
                 (-> output
                     (slurp)
                     ($.edn.read/string)))
              "Persisted"))
      ;;
      (finally
        (bb.fs/delete-tree root)))))


;;;;;;;;;;


(T/deftest prepare+

  (let [state           (select-keys -state-prepared
                                     [::$.maestro.plugin.gitlib/alias
                                      ::$.maestro.plugin.gitlib/sha
                                      ::$.maestro.plugin.gitlib/url
                                      ::$.maestro/deps.edn])
        state-prepared+ ($.maestro.plugin.gitlib/prepare+ state)
        k-prepared+     ::$.maestro.plugin.gitlib/prepared+
        prepared+       (state-prepared+ k-prepared+)]

    (T/is (= state
             (dissoc state-prepared+
                     k-prepared+))
          "Augmented")

    (T/is (= -state-prepared
             (first prepared+))
          "Individual preparation valid")
    
    (T/is (= 3
             (count prepared+))
          "All exposed aliases prepared")))



(T/deftest write-prepared+

  (with-redefs [$.maestro.plugin.gitlib/write-prepared inc]
    (T/is (= {::$.maestro.plugin.gitlib/prepared+
              [2
               3]}
             (-> {::$.maestro.plugin.gitlib/prepared+
                  [1
                   2]}
                 ($.maestro.plugin.gitlib/write-prepared+))))))

