;(metadata 
  ;:title  "Four tracks in Postmelodic."
  ;:author "Mlad Konstruktor <fallenblood@gmail.com>")


(use-module "osc")
(use-module "midi")
;(use-module "spawn")


; utility functions

(defn pos->lpd [pos]
  (cond
    (and (>= pos 0) (<= pos 7)) pos
    (and (>= pos 8) (<= pos 15))  (+ pos 8)))


(defn lpd->pos [data1]
  (cond
    (and (>= data1 0)  (<= data1 7))   data1
    (and (>= data1 16) (<= data1 23))  (- data1 8)))


(defn next-pos [pos]
  (if (and (>= pos 0) (< pos 15))
    (+ 1 pos)
    0))


; looper control

(defn looper
  [sample]

  (osc-connect
    "localhost"
    "77070"
    (fn [client]

      (let [cue   (fn [point frame]
                    (.send client (osc-message "/cue" 0 point frame)))
            seek  (fn [point]
                    (.send client (osc-message "/play" 0 point)))
            position (atom 0)]

        (println "connected to sampler at :7770")

        (on :beat
          (fn [args]
            (doseq [i [0  1  2  3  4  5  6  7
                      16 17 18 19 20 21 22 23]]
              (midi-send 144 i 0))

            (midi-send 144 (pos->lpd @position) 15)
            (swap! position next-pos)))

        (on :midi-in
          (fn [_ [status data1 data2]]
            (when (= data2 127)
              (when-let [pos (lpd->pos data1)]
                (reset! position pos))) ))) )))


; initialize looper with midi

(let [ midi-ports       (refresh-midi-ports)

       starts-with      (fn [str1 str2] (= 0 (.lastIndexOf str1 str2)))

       get-port-name    (fn [port-type port-name]
                          (first (filter #(starts-with % port-name)
                                         (keys (midi-ports port-type))) ))

       midi-in-name     (get-port-name :in  "Launchpad")

       midi-out-name    (get-port-name :out "Launchpad")

       clear-display!   (fn [] (doseq [cc (range 16)]
                          (midi-send 176 0 0)
                          (midi-send 176 0 1))) ]

  ; Setup MIDI

  (println midi-ports)

  (println "Connecting to MIDI in: " midi-in-name)
  (open-midi-in midi-in-name)

  (println "Connecting to MIDI out:" midi-out-name)
  (open-midi-out midi-out-name)

  (clear-display!)
  
  ; Start looper  

  (looper "nevermind"))


; tick tock, mock clock

(let [timers (js/require "timers")]
  (on :beat (fn [args] (.setTimeout timers #(trigger :beat) 214))))

(trigger :beat)
