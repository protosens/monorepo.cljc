(ns protosens.test.maestro.plugin.readme.listing

  (:import (java.io StringWriter))
  (:require [clojure.string                          :as C.string]
            [clojure.test                            :as T]
            [protosens.maestro.plugin.readme.listing :as $.maestro.plugin.readme.listing]))


;;;;;;;;;;


(T/deftest docstring

  (T/is (= "No description"
           ($.maestro.plugin.readme.listing/docstring {})
           ($.maestro.plugin.readme.listing/docstring {:maestro/doc nil})
           ($.maestro.plugin.readme.listing/docstring {:maestro/doc ""})))

  (T/is (= "This is a test"
           (-> {:maestro/doc "This is a test.
                              Indeed."}
               ($.maestro.plugin.readme.listing/docstring)))))



(T/deftest root

  (T/is (= "a"
           (-> {:maestro/root "module/a"}
               ($.maestro.plugin.readme.listing/root)))))



(T/deftest table

  (let [writer (StringWriter.)]
    (T/is (= (C.string/join \newline
                            ["| Module | Description |"
                             "|---|---|"
                             "| [`:a`](./a) | Module A |"
                             "| [`:b`](./b) | Module B |"
                             ""])
             (binding [*out* writer]
               (-> [[:a {:maestro/doc  "Module A.
                                        Indeed."
                         :maestro/root "module/a"}]
                    [:b {:maestro/doc  "Module B."
                         :maestro/root "module/b"}]]
                   ($.maestro.plugin.readme.listing/table))
               (str writer))))))
