(ns protosens.test.util.deps.edn.diff

  (:require [babashka.fs   :as bb.fs]
            [protosens.git :as $.git]))


;;;;;;;;;;


(defn with-touched-path+

  [f]

  (let [dir      "module/maestro/src/"
        dir-main (str dir
                      "main/clj/")
        dir-test (str dir
                      "test/clj/")
        foo      (str dir-main
                      "foo")
        bar      (str dir-test
                      "bar")]
    (try
      ;;
      (doseq [file [foo
                    bar]]
        (spit file
              "Test"))
      ($.git/add [foo
                  bar])
      (f [dir-main
          dir-test]
         [foo
          bar])
      ;;
      (finally
        (doseq [file [foo
                      bar]]
          (bb.fs/delete-if-exists file)
          ($.git/add [file]))))))
