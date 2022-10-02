(ns protosens.symbol

  "Collection of helpers for handling symbols.

   Often similar to [`clojure.string`](https://clojuredocs.org/clojure.string).
   Actually, those functions typically work with strings instead of input symbols but
   providing symbols better conveys the intent."

  (:refer-clojure :exclude [replace])
  (:require [clojure.string :as string]))


(declare stringify)


;;;;;;;;;;


(defn ends-with?

  "Returns `true` if the given `sym` ends with `x`."

  [sym x]

  (string/ends-with? (str sym)
                     (str x)))



(defn includes?

  "Returns `true` if the given `sym` include `x`."

  [sym x]

  (string/includes? (str sym)
                    (str x)))



(defn join

  "Joins the given collection of symbols.
  
   Default separator is `.`."


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

  "Qualifies or requalifies `sym` in terms of `namespace`."

  [namespace sym]

  (symbol (str namespace)
          (name sym)))



(defn- -replace

  ;; Core implementation for `replace...` functions

  [f sym match replacement]

  (symbol (f (str sym)
             (stringify match)
             (if (fn? replacement)
               (comp str
                     replacement)
               (stringify replacement)))))



(defn replace

  "Replaces `match`es in the given `sym`.
  
   Like [`clojure.string/replace`](https://clojuredocs.org/clojure.string/replace)
   but inputs can be symbols."

  [sym match replacement]

  (-replace string/replace
            sym
            match
            replacement))



(defn replace-first

  "Replaces the first occurence of `match` in the given `sym`.

   Other than that, exactly like [[replace]]."

  [sym match replacement]

  (-replace string/replace-first
            sym
            match
            replacement))



(defn split

  "Splits the given `sym`.
  
   Default separator is `.`."


  ([sym]

   (split nil
          sym))


  ([regex-separator sym]

   (map symbol
        (string/split (str sym)
                      (or regex-separator
                          #"\.")))))



(defn starts-with?

  "Returns `true` if the given `sym` starts with `x`."

  [sym x]

  (string/starts-with? (str sym)
                       (str x)))



(defn stringify

  "Transforms `x` into a string only if it is a symbol."

  [x]

  (cond->
    x
    (symbol? x)
    (str)))
