(ns protosens.git

  (:refer-clojure :exclude [resolve])
  (:require [babashka.process :as bb.process]
            [clojure.string   :as string]
            [protosens.txt    :as $.txt]))


;;;;;;;;;; Miscellaneous helpers


(defn full-sha?

  [x]

  (and (string? x)
       (= (count x)
          40)))


;;;;;;;;;; Master command


(defn exec


  ([arg+]

   (exec arg+
         nil))


  ([arg+ option+]

   (let [{:keys [err
                 exit
                 out]} (bb.process/sh (cons (or (:command option+)
                                                "git")
                                            arg+)
                                      option+)]
     (cond->
       {:exit exit}
       ;;
       (seq err)
       (assoc :err
              err)
       ;;
       (seq out)
       (assoc :out
              out)))))


;;;;;;;;;; Quick commands


(defn add


  ([path+]

   (add path+
        nil))


  ([path+ option+]

   (-> (exec (cons "add"
                    path+)
             option+)
       (:exit)
       (zero?))))



(defn branch


  ([]

   (branch nil))


  ([option+]

   (-> (exec ["branch" "--show-current"]
             option+)
       (:out)
       (string/trimr))))



(defn branch+


  ([]

   (branch+ nil))


  ([option+]

   (-> (exec ["branch"]
             option+)
       (:out)
       (some-> (string/split-lines)
               (->> (map (fn [branch]
                           ($.txt/trunc-left branch
                                             2))))))))



(defn checkout


  ([branch]

   (checkout branch
             nil))


  ([branch option+]

   (-> (exec ["checkout" branch]
             option+)
       (:exit)
       (zero?))))



(defn checkout-new


  ([branch]

   (checkout-new branch
                 nil))


  ([branch option+]

   (-> (exec ["checkout" "-b" branch]
             option+)
       (:exit)
       (zero?))))



(defn clean?



  ([]

   (clean? nil))


  ([option+]

   (-> (exec ["status" "--porcelain"]
             option+)
       (:out)
       (empty?))))



(defn commit


  ([message]

   (commit message
           nil))


  ([message option+]

   (-> (exec ["commit" "-m" ($.txt/realign message)]
             option+)
       (:exit)
       (zero?))))



(defn commit-message


  ([sha]

   (commit-message sha
                   nil))


  ([sha option+]

   (let [result (exec ["show" "-s" "--format=%B" sha]
                      option+)]
     (when (zero? (result :exit))
       (string/trimr (result :out))))))



(defn commit-sha


  ([i]

   (commit-sha i
               nil))


  ([i option+]

   (let [out (-> (exec ["rev-parse" (str "HEAD~"
                                         (or i
                                             0))]
                       option+)
                 (:out)
                 (string/trimr))]
     (when (full-sha? out)
       out))))



(defn count-commit+


  ([]

   (count-commit+ nil))


  ([option+]

   (if (-> (exec ["log" "-0"]
                 option+)
           (:exit)
           (zero?))
     (-> (exec ["rev-list" "--count" "HEAD"]
               option+)
         (:out)
         (string/trimr)
         (Long/parseLong))
     0)))



(defn init


  ([]

   (init nil))


  ([option+]

   (-> (exec ["init"]
             option+)
       (:exit)
       (zero?))))



(defn modified?

  ;; Anything different in versioned files, staged or not.
  
  ([]

   (modified? nil))


  ([option+]

   (and (boolean (commit-sha nil
                             option+))
        (-> (exec ["diff-index" "--quiet" "HEAD"]
                  option+)
            (:exit)
            (pos?)))))



(defn repo?


  ([]
   
   (repo? nil))


  ([option+]

   (if-some [out (-> (exec ["rev-parse" "--is-inside-work-tree"]
                           option+)
                     (:out))]
     (-> out
         (string/trimr)
         (= "true"))
     false)))



(defn resolve


  ([ref]

   (resolve ref
            nil))


  ([ref option+]

   (let [out (-> (exec ["rev-parse" ref]
                       option+)
                 (:out)
                 (string/trimr))]
     (when (full-sha? out)
       out))))



(defn tag+


  ([]

   (tag+ nil))


  ([option+]

   (-> (exec ["tag"]
             option+)
       (:out)
       (some-> (string/split-lines)))))



(defn tag-add


  ([tag]

   (tag-add tag
            nil))


  ([tag option+]

   (-> (exec ["tag" tag]
             option+)
       (:exit)
       (zero?))))



(defn unstaged?

  
  ([]

   (unstaged? nil))


  ([option+]

   (-> (exec ["diff" "--quiet"]
             option+)
       (:exit)
       (pos?))))



(defn version


  ([]

   (version nil))


  ([option+]

   (-> (exec ["--version"]
             option+)
       (:out)
       (string/trimr))))
