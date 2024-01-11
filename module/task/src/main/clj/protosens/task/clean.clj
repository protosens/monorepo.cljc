(ns protosens.task.clean

  (:require [babashka.fs              :as bb.fs]
            [protosens.maestro.plugin :as $.maestro.plugin]))


;;;;;;;;;;


(defn everything

  []

  ($.maestro.plugin/intro "protosens.task.clean/everything")
  ($.maestro.plugin/safe
    (delay
      ($.maestro.plugin/step "Deleting:")
      (doseq [path ["./.shadow-cljs/builds/"
                    "./private/tmp/"]]
        ($.maestro.plugin/step 1
                               path)
        (bb.fs/delete-tree path))
      ($.maestro.plugin/done "All clean"))))
