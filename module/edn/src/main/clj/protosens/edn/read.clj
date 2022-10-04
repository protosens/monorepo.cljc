(ns protosens.edn.read

  "Reading EDN data.

   Accepted options for reading are:

   | Key               | Value                                               | Default |
   |-------------------|-----------------------------------------------------|---------|
   | `:default-reader` | Used when `:tag->reader` falls short                | `nil`   |
   | `:end`            | Value returned when the end of the input is reached | Throws  |
   | `:tag->reader     | Map of tagged reader functions                      | `{}`    |

   The default reader (if provided) is only used when there is no reader function for a given tag
   in `:tag->reader`. It takes 2 arguments: the tag and its associated value."

  (:import (java.io PushbackReader))
  (:require [clojure.edn     :as edn]
            [clojure.java.io :as java.io]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;; Private


(defn- -option+

  ;; Prepares options, notably by explicitly setting a value for EOF.

  [option+]

  (let [end (:end option+)]
    (cond->
      {:default (:default-reader option+)
       :readers (:tag->reader option+)}
      end
      (assoc :eof
             end))))


;;;;;;;;;; Public


(defn file

  "Reads the first object from the file at the given `path`."

  
  ([path]

   (file path
         nil))


  ([path option+]

   (with-open [reader (-> path
                          (java.io/reader)
                          (PushbackReader.))]
     (edn/read (-option+ option+)
               reader))))



(defn string

  "Reads the first object in the given string."


  ([s]

   (string s
           nil))


  ([s option+]

   (edn/read-string (-option+ option+)
                    s)))
