(use-module "pulse")


(use-module "midi")


(let [midi-ports       (refresh-midi-ports)

      starts-with      (fn [str1 str2] (= 0 (.lastIndexOf str1 str2)))

      get-port-name    (fn [port-type port-name]
                         (first (filter #(starts-with % port-name)
                                        (keys (midi-ports port-type))) ))

      midi-in-name     (get-port-name :in  "Midi Fighter")
      
      midi-out-name    (get-port-name :out "Midi Fighter")

      clear-display    (fn [] (doseq [cc (range 16)]
                         (midi-out :control 0 cc 0)
                         (midi-out :control 1 cc 0)))

      volume-base      65535

      playback-streams (atom {})]


  ; Setup MIDI

  (println)

  (println "Connecting to MIDI in: " midi-in-name)
  (open-midi-in midi-in-name)

  (println "Connecting to MIDI out:" midi-out-name)
  (open-midi-out midi-out-name)

  (clear-display)

  ; Setup PulseAudio

  (defn vol->cc [volume]
    (.floor js/Math (* 127 (/ (first volume) volume-base))) )

  (defn cc->vol [cc]
    (.floor js/Math (* volume-base (/ cc 100))) )

  (add-watch playback-streams nil
    (fn [_ _ old-streams new-streams]
      (clear-display)
      (doseq [cc (keys new-streams)]
        (midi-out :control 1 cc 1)
        (pulse-get-volume (new-streams cc)
          (fn [err vol]
            (midi-out :control 0 cc (vol->cc vol))) ))) )

  (defn first-free-slot [streams]
    (println "\nSTREAMS" streams)
    (if (nil? (keys streams))
      0
      (some #(if (nil? (streams %)) % nil) (range 16))))

  (on :pulse-new-playback-stream
    (fn [& args]
      (let [path (first args)]
        (pulse-get-stream path
          (fn [err stream]
            (swap! playback-streams
              (fn [streams]
                (assoc streams (first-free-slot streams) stream))) ))) ))

  (on :pulse-playback-stream-removed
    (fn [& args]
      (let [path (first args)]
        (swap! playback-streams
          (fn [streams]
            (dissoc streams 
              (first (filter #(= path (.-path (streams %)))
                             (keys streams))) ))) )))

  (on :midi-in (fn [_ msg]
    (let [msg    (apply unpack-midi-msg msg)
          stream (@playback-streams (:data1 msg))]
      (when stream
        (pulse-set-volume stream (cc->vol (:data2 msg))) ))) )

      ;(pulse-set-volume (@playback-streams (:data1 msg))
                        ;(:data2 msg))) ))

)
