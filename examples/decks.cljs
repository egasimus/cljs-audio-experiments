;(metadata 
  ;:title  "Four tracks in Postmelodic."
  ;:author "Mlad Konstruktor <fallenblood@gmail.com>")


(use-module "osc")
(use-module "midi")
(use-module "spawn")


; utility functions

(defn pos->lpd [pos]
  (cond
    (and (>= pos 0) (<= pos 7)) pos
    (and (>= pos 8) (<= pos 15))  (+ pos 8)))

(defn lpd->pos [data1]
  (cond
    (and (>= data1 0)  (<= data1 7))   data1
    (and (>= data1 16) (<= data1 23))  (- data1 8)))

(defn advance-pos [pos]
  (if (and (>= pos 0) (< pos 15))
    (+ 1 pos)
    0))

(defn lpd-clear!
  ([] (doseq [cc (concat (range 8)
                        (range 16 24))]
      (midi-send 176 0 0)
      (midi-send 176 0 1)))
  ([cell])        ; TODO
  ([start end]))  ; TODO

(defn lpd-red! [pos]
  (midi-send 144 pos 3))

(defn lpd-green! [pos]
  (midi-send 144 pos 48))

(defn lpd-yellow! [pos]
  (midi-send 144 pos 51))


; looper control

(defn looper
  [sample]

  ;(spawn "/home/epimetheus/code/kunst/postmelodic/bin/sample_player" (array sample))

  (osc-connect
    "localhost"
    "7770"
    (fn [client]

      (let [cue      (fn [point frame]
                       (.send client (osc-message "/cue" 0 point frame)))

            seek     (fn [point]
                       (.send client (osc-message "/play" 0 point)))

            position (atom 0)

            next-pos (atom nil)]

        (println "connected to sampler at :7770")

        (doseq [i (range 16)] (cue i (.floor js/Math (* (/ 151202 16) i))))

        (add-watch position nil
          (fn [_ _ old-pos new-pos]
            (lpd-clear!)
            (lpd-yellow! (pos->lpd new-pos))
            (seek new-pos)))

        (reset! position 0)

        (on :beat
          (fn [args]
            (if @next-pos (do (reset! position @next-pos)
                              (reset! next-pos nil))
                          (swap! position advance-pos))))

        (on :midi-in
          (fn [_ msg]
            (let [msg (apply unpack-midi-msg msg)
                  hit (= 127 (:data2 msg))
                  jmp (lpd->pos (:data1 msg))]
              (when (and hit jmp) (lpd-red! (:data1 msg))
                                  (reset! next-pos jmp))) ))) )))


(defn btn-fader [start end])


; initialize looper with midi

(let [ midi-ports       (refresh-midi-ports)

       starts-with      (fn [str1 str2] (= 0 (.lastIndexOf str1 str2)))

       get-port-name    (fn [port-type port-name]
                          (first (filter #(starts-with % port-name)
                                         (keys (midi-ports port-type))) ))

       midi-in-name     (get-port-name :in  "Launchpad")

       midi-out-name    (get-port-name :out "Launchpad") ]

  ; Setup MIDI

  (println midi-ports)

  (println "Connecting to MIDI in: " midi-in-name)
  (open-midi-in midi-in-name)

  (println "Connecting to MIDI out:" midi-out-name)
  (open-midi-out midi-out-name)

  (lpd-clear!)

  ; Start pseudo-faders

  (btn-fader "1H" "1C")

  ; Start looper  

  (looper "/home/epimetheus/code/kunst/postmelodic/data/breakbeat-140bpm.wav"))


; tick tock, mock clock

(let [timers (js/require "timers")]
  (on :beat (fn [args] (.setTimeout timers #(emit :beat) 214))))

(emit :beat)
