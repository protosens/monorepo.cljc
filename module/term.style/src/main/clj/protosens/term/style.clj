(ns protosens.term.style

  (:refer-clojure :exclude [reverse]))


(set! *warn-on-reflection*
      true)


;;;;;;;;;; Background colors


(def bg-black

  "\033[40m")



(def bg-blue

  "\033[44m")



(def bg-cyan

  "\033[46m")



(def bg-green

  "\033[42m")



(def bg-magenta

  "\033[45m")




(def bg-red

  "\033[41m")




(def bg-white

  "\033[47m")




(def bg-yellow

  "\033[43m")


;;;


(defn bg-rgb


  ([[r g b]]

   (bg-rgb r
           g
           b))


  ([r g b]

   (str "\033[48;2;" r ";" g ";" b "m")))


;;;;;;;;;; Foreground colors


(def fg-black

  "\033[30m")



(def fg-blue

  "\033[34m")



(def fg-cyan

  "\033[36m")



(def fg-green

  "\033[32m")



(def fg-magenta

  "\033[35m")



(def fg-red

  "\033[31m")



(def fg-white

  "\033[37m")



(def fg-yellow

  "\033[33m")


;;;


(defn fg-rgb


  ([[r g b]]

   (fg-rgb r
           g
           b))


  ([r g b]

   (str "\033[38;2;" r ";" g ";" b "m")))


;;;;;;;;;; Modifiers


(def bold

  "\033[1m")



(def reset

  "\033[0m")



(def reverse

  "\033[7m")



(def underline

  "\033[4m")
