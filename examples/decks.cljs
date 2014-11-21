;(metadata 
  ;:title  "Four SooperLooper tracks."
  ;:author "Mlad Konstruktor <fallenblood@gmail.com>")


(use-module "osc")


(use-module "midi")


(on :midi-in (fn [args] (println args)))


(on :osc-in  (fn [args] (println args)))


(trigger :midi-out 176 0 127)


;(track "Deck A"
  ;(clip "empty clip")
  ;(clip "empty clip")
  ;(clip "empty clip")
  ;(clip "empty clip"))


;(track "Deck B"
  ;(clip "empty clip")
  ;(clip "empty clip")
  ;(clip "empty clip")
  ;(clip "empty clip"))


;(ns examples.decks
  ;(:require [cljs.repl :as repl]))

;(defn track [& args])

;(defn clip [& args])

;(def info
  ;{ :title  "Example I"
    ;:author "Mlad Konstruktor <fallenblood@gmail.com>" })

;(def tracks
  ;[ (track "Deck A"
      ;(clip "Dat Beat"   "/home/epimetheus/hear/samples/favorites/drums/140bpm-vec3-breakbeat-011.wav")
      ;(clip "Same Beat"  "/home/epimetheus/hear/samples/favorites/drums/140bpm-vec3-breakbeat-011.wav"))

    ;(track "Deck B"
      ;(clip "Nasty Bass" "/home/epimetheus/hear/samples/favorites/bass/140bpm-g-vdub2-melody-100.wav")
      ;(clip "Nice Bass"  "/home/epimetheus/hear/samples/favorites/bass/128bpm-em-vdub2-melody-47.wav")) ])

;(println tracks)
