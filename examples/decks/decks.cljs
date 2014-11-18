(ns examples.decks
  (:require [hardbop] 
            [hardbop.media.sooperlooper :as sl]))

(session

  :info { :title  "Example I"
          :author "Mlad Konstruktor <fallenblood@gmail.com>" }

  (track "Deck A"
    (clip "Dat Beat"  "/home/epimetheus/hear/samples/favorites/drums/140bpm-vec3-breakbeat-011.wav")
    (clip "Same Beat" "/home/epimetheus/hear/samples/favorites/drums/140bpm-vec3-breakbeat-011.wav"))

  (track "Deck B"
    (clip "Nasty Bass"  "/home/epimetheus/hear/samples/favorites/bass/140bpm-g-vdub2-melody-100.wav")
    (clip "Nice Bass" "/home/epimetheus/hear/samples/favorites/bass/128bpm-em-vdub2-melody-47.wav")))