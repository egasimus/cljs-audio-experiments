;(metadata 
  ;:title  "Four SooperLooper tracks."
  ;:author "Mlad Konstruktor <fallenblood@gmail.com>")


(use-module "osc")
(use-module "midi")
;(use-module "spawn")


(defn reset-launchpad
  []
  (trigger :midi-out 176 0 0)
  (trigger :midi-out 176 0 1))


(defn looper
  [sample]
  (let [position-to-launchpad (fn [pos]
                                (cond (and (>= pos 0) (<= pos 7))  pos
                                      (and (>= pos 8) (<= pos 15)) (+ pos 8)))


        launchpad-to-position (fn [data1]
                                (cond (and (>= data1 0)  (<= data1 7))  data1
                                      (and (>= data1 16) (<= data1 23)) (- data1 8)))


        advance-position      (fn [pos]
                                (if (and (>= pos 0) (< pos 15))
                                  (+ 1 pos)
                                  0))

        seek                  (fn [client pos]
                                (println "seek" pos)
                                (.send client (osc-bundle [
                                  ["/sl/0/down" "scratch"]
                                  ["/sl/0/set"  "scratch_pos" (/ (- 15 pos) 16)]
                                  ["/sl/0/hit"  "set_sync_pos"]
                                  ["/sl/0/up"   "scratch"]
                                  ["/sl/0/down" "trigger"]])))

        position              (atom 0)]

    (trigger :osc-connect "localhost" "9951"
      (fn [client]

        (println "foo")

        (.send client (osc-bundle [
          ["/sl/0/load_loop" sample "" ""]
          ;["/sl/0/register_auto_update" "loop_pos" 100 "localhost:3333" "/update"]
          ;["/sl/0/register_auto_update" "scratch_pos" 100 "localhost:3333" "/update"]
          ;["/sl/0/register_auto_update" "state" 100 "localhost:3333" "/update"]
          ["/ping"           "localhost:3333" "/pong"]]))

        (defn up      [evt] (.send client "/sl/0/up"      evt))
        (defn down    [evt] (.send client "/sl/0/down"    evt))
        (defn forceup [evt] (.send client "/sl/0/forceup" evt))
        (defn hit     [evt] (.send client "/sl/0/hit"     evt))

        (on :beat
          (fn [args]
            ;(when (= @position 0) (seek client 0))
            ;(seek client @position)
            
            (doseq [i [0  1  2  3  4  5  6  7
                      16 17 18 19 20 21 22 23]]
              (trigger :midi-out 144 i 0))

            (trigger :midi-out 144 (position-to-launchpad @position) 15)

            (swap! position advance-position)))

        (on :midi-in
          (fn [_ [status data1 data2]]
            (when (= data2 127)
              (when-let [pos (launchpad-to-position data1)]
                (reset! position pos)
                (seek client pos)))))

        ;(.send client "/sl/0/hit" "trigger")
        ;(println (/ pos 16))
        ;(.send client (osc-bundle [
          ;#_["/sl/0/hit" "pause"]
          ;#_["/sl/0/down" "set_sync_pos"]
          ;#_["/sl/0/set" "scratch_pos" (/ pos 16)]
          ;#_["/sl/0/up" "set_sync_pos"]
          ;#_["/sl/0/hit" "set_sync_pos"]
          ;#_["/sl/0/hit" "trigger"]]) ))) ))
        ;(.send client "/sl/0/set" "scratch_pos" (/ data1 16))) ))

        (on :osc-in
          (fn [& args]
            (println "sl says" args))) ))) )


(looper "/home/epimetheus/hear/samples/kunststruktur/drums/140bpm-vec3-breakbeat-011.wav")


; mock clock

(let [timers (js/require "timers")]
  (on :beat (fn [args] (.setTimeout timers #(trigger :beat) 214))))

(trigger :beat)
