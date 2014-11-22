;(metadata 
  ;:title  "Four SooperLooper tracks."
  ;:author "Mlad Konstruktor <fallenblood@gmail.com>")


(use-module "osc")
(use-module "midi")
(use-module "spawn")


(trigger :midi-out 176 0 0)
(trigger :midi-out 176 0 1)


(let [timers (js/require "timers")]
  (on :beat (fn [args] (.setTimeout timers #(trigger :beat) 214))))


;(on :midi-in (fn [delta-time msg] (println "midi in" delta-time msg)))
;(on :osc-in  (fn [& args]         (println "osc in"  args)))


(defn looper-1
  [pos client]

  (when (= pos 0)                     ; keep loop in sync
    (.send client "/sl/0/hit" "trigger"))
  
  (doseq [i [0  1  2  3  4  5  6  7   ; clear screen
            16 17 18 19 20 21 22 23]]
    (trigger :midi-out 144 i 0))

  (trigger :midi-out 144 pos 15)      ; draw pixel

  (cond (< pos 0)  0                  ; return next position
        (= pos 7)  16
        (> pos 22) 0
        :else      (+ 1 pos)))             


(defn looper
  [sample]
  (let [;sooperlooper (spawn "sooperlooper"
                       ;["-D" "no"
                       ; "-p" "3334"])
        position     (atom 0)]

    (trigger :osc-connect "localhost" "9951"
      (fn [client]
        (.send client (osc-bundle [
          ["/sl/0/load_loop" sample "" ""]
          ["/sl/0/hit"       "trigger"]
          ["/ping"           "localhost:3333" "/pong"]]))

        (on :beat
          (fn [args]
            (swap! position looper-1 client)))

        (on :midi-in
          (fn [delta-time [status data1 data2]]
            (when (= data2 127)
              (reset! position (looper-1 data1 client))
              (.send client "/sl/0/set" "scratch_pos" (/ data1 16))) ))

        (on :osc-in
          (fn [& args]
            (println "and again" args))) ))) )


(looper "/home/epimetheus/hear/samples/kunststruktur/drums/140bpm-vec3-breakbeat-011.wav")
