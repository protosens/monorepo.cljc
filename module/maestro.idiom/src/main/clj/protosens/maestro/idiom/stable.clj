(ns protosens.maestro.idiom.stable

  "Tagging stable releases following [calver](https://calver.org)

   See [[today]] about tag format.

   These are the utilities used by the Protosens monorepo but there is no
   obligation following all that.

   Some functions accept the following options:

   | Key    | Value                                     | Default           |
   |--------|-------------------------------------------|-------------------|
   | `:dir` | Directory used for Git-related operations | Current directory |"

  (:import (java.time LocalDateTime)
           (java.time.format DateTimeFormatter))
  (:require [clojure.string :as string]
            [protosens.git  :as $.git]))


(declare today)


;;;;;;;;;; Private


(def ^:private ^DateTimeFormatter -dtf

  ;; Date time formatter used in [[today]].

  (DateTimeFormatter/ofPattern "yyyy-MM-dd"))


;;;;;;;;;; Public


(defn all

  "Returns a list of stable tags in the repository."


  ([]

   (all nil))


  ([option+]

   (filter (fn [tag]
             (string/starts-with? tag
                                  "stable/"))
           ($.git/tag+ option+))))



(defn latest

  "Returns the latest stable tag"

  ([]

   (latest nil))


  ([tag+]

   (some->> (not-empty (or tag+
                           (all)))
            (reduce (fn [acc tag]
                      (if (pos? (compare tag
                                         acc))
                        tag
                        acc))))))



(defn tag?

  "Is the given `tag` a stable tag?"

  [tag]

  (string/starts-with? tag
                       "stable/"))



(defn tag-add

  "Tags the last commit as a stable release.
  
   See [[today]]."


  ([]

   (tag-add nil))


  ([option+]

   (let [t (today option+)]
     ($.git/tag-add t
                    option+)
     t)))



(defn tag->date

  "Returns the date portion of the given stable tag."

  [tag]

  (when (tag? tag)
    (second (string/split tag
                          #"/"
                          2))))



(defn today

  "Returns a stable tag for a release done today.
  
   Format is `stable/YYYY-0M-0D`.

   If the tag already exists, appends an iterating `_%02d` portion.

   Hence, this format is suitable for a daily stable release at most, providing
   a bit of room for emergencies."


  ([]

   (today nil))


  ([option+]

   (let [tag (str "stable/"
                  (.format -dtf
                           (LocalDateTime/now)))]
     (if ($.git/resolve tag
                        option+)
       (loop [i 2]
         (let [tag-2 (format "%s_%02d"
                             tag
                             i)]
           (if ($.git/resolve tag-2
                              option+)
             (recur (inc i))
             tag-2)))
       tag))))
