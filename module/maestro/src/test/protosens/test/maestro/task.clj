(ns protosens.test.maestro.task

  (:require [clojure.test           :as T]
            [protosens.maestro.task :as $.maestro.task]))


;;;;;;;;;;


(T/deftest alias+

  (T/is (= ":b:e:c:a"
           (with-out-str
             (binding [*command-line-args* "[:b bar]"]
               ($.maestro.task/alias+ '{:aliases          {:a {:maestro/require [:b
                                                                                 {foo :c}]}
                                                           :b {:maestro/require [{bar :d}]}
                                                           :c {:maestro/require [{foo :e
                                                                                  bar :f}]}
                                                           :d {}
                                                           :e {}
                                                           :f {}
                                                           :g {}
                                                           :h {}}
                                        :maestro/alias+   [:a]
                                        :maestro/profile+ [foo]}))))))


(T/deftest pprint-cp

  (T/is (= "a\nb\nc/d\n"
           (with-out-str
             ($.maestro.task/pprint-cp "a:b:c/d")))))
