(ns protosens.test.maestro.plugin.changelog

  (:require [babashka.fs                        :as bb.fs]
            [clojure.test                       :as T]
            [protosens.maestro.plugin.changelog :as $.maestro.plugin.changelog]))


;;;;;;;;;;


(T/deftest -module-path+

  (let [dir  (bb.fs/temp-dir)
        root (str dir
                  "/module/c")
        path (str root
                  "/doc/changelog.md")]
    (-> path
        (bb.fs/parent)
        (bb.fs/create-dirs))
    (spit path
          "")
    (T/is (= [[:c
               path]]
             (-> {:aliases (sorted-map
                             :a nil
                             :b {}
                             :c {:maestro/root root}
                             :d {:maestro/root "module/d"})}
                 ($.maestro.plugin.changelog/-module-path+))))))
