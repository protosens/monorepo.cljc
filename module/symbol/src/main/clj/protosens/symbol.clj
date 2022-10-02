(ns protosens.symbol

  (:refer-clojure :exclude [replace])
  (:require [clojure.string :as string]))


(declare stringify)


;;;;;;;;;;


(defn ends-with?

  [sym x]

  (string/ends-with? (str sym)
                     (str x)))



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



(defn- -replace

  [f sym match replacement]

  (symbol (f (str sym)
             (stringify match)
             (if (fn? replacement)
               (comp str
                     replacement)
               (stringify replacement)))))



(defn replace

  [sym match replacement]

  (-replace string/replace
            sym
            match
            replacement))



(defn replace-first

  [sym match replacement]

  (-replace string/replace-first
            sym
            match
            replacement))



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
