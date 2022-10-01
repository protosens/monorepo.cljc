(ns protosens.git

  (:require [babashka.process :as bb.process]
            [clojure.string   :as string]
            [protosens.txt    :as $.txt]))


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
       (string/split-lines)
       (->> (map (fn [branch]
                   ($.txt/trunc-left branch
                                     2)))))))



(defn change?

  ;; Anything different in versioned files, staged or not.
  
  ([]

   (change? nil))


  ([option+]

   (-> (exec ["diff-index" "--quiet" "HEAD"]
             option+)
       (:exit)
       (pos?))))



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



(defn last-sha


  ([]

   (last-sha 0))

  
  ([i]

   (last-sha i
             nil))


  ([i option+]

   (-> (exec ["rev-parse" (str "HEAD^"
                               (or i
                                   0))]
             option+)
       (:out)
       (string/trimr))))



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
