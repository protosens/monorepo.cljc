(ns protosens.symbol

  (:refer-clojure :exclude [replace])
  (:require [clojure.string :as string]))


(declare stringify)


;;;;;;;;;;


(defn includes?

  [sym x]

  (string/includes? (str sym)
                    (str x)))



(defn join


  ([segment+]

   (join nil
         segment+))


  ([separator segment+]

   (symbol (string/join (or (some-> separator
                                    (str))
                            ".")
                        (map str
                             segment+)))))



(defn qualify

  [namespace sym]

  (symbol (str namespace)
          (name sym)))



(defn replace

  [sym match replacement]

  (symbol (string/replace (str sym)
                          (stringify match)
                          (stringify replacement))))



(defn replace-first

  [sym match replacement]

  (symbol (string/replace-first (str sym)
                                (stringify match)
                                (stringify replacement))))



(defn split


  ([sym]

   (split nil
          sym))


  ([regex-separator sym]

   (map symbol
        (string/split (str sym)
                      (or regex-separator
                          #"\.")))))



(defn starts-with?

  [sym x]

  (string/starts-with? (str sym)
                       (str x)))



(defn stringify

  [x]

  (cond->
    x
    (symbol? x)
    (str)))
