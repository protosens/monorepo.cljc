(ns protosens.git

  (:require [babashka.process :as bb.process]
            [clojure.string   :as string]
            [protosens.txt    :as $.txt]))


(declare last-sha)


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


  ([]

   (add nil))


  ([path+]

   (add path+
        nil))


  ([path+ option+]

   (-> (exec (if (seq path+)
               (cons "add"
                     path+)
               ["add" "."])
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



(defn change?

  ;; Anything different in versioned files, staged or not.
  
  ([]

   (change? nil))


  ([option+]

   (and (boolean (last-sha nil
                           option+))
        (-> (exec ["diff-index" "--quiet" "HEAD"]
                  option+)
            (:exit)
            (pos?)))))



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



(defn last-sha


  ([]

   (last-sha 0))

  
  ([i]

   (last-sha i
             nil))


  ([i option+]

   (let [result (-> (exec ["rev-parse" (str "HEAD~"
                                            (or i
                                                0))]
                          option+)
                    (:out)
                    (string/trimr))]
     (when (full-sha? result)
       result))))



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
