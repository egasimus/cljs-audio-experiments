(use-module "pulse")

(use-module "midi")

(let [midi-ports    (refresh-midi-ports)

      starts-with   (fn [str1 str2] (= 0 (.lastIndexOf str1 str2)))

      get-port-name (fn [port-type port-name]
                      (first (filter #(starts-with % port-name)
                                     (keys (midi-ports port-type)))))

      midi-in-name  (get-port-name :in  "Midi Fighter")
      
      midi-out-name (get-port-name :out "Midi Fighter")

      clear-display (fn [] (doseq [cc (range 16)]
                      (midi-out :control 1 cc 0)))

      playback-streams (atom {})]

  ; Setup MIDI

  (println)

  (println "Connecting to MIDI in: " midi-in-name)
  (open-midi-in midi-in-name)

  (println "Connecting to MIDI out:" midi-out-name)
  (open-midi-out midi-out-name)

  (clear-display)

  ; Setup PulseAudio

  (add-watch playback-streams nil
    (fn [_ _ old-streams new-streams]
      (println "\nUPDATED" old-streams "TO" new-streams)
      (doseq [cc (keys new-streams)]
        (midi-out :control 1 cc 1))))

  (defn first-free-slot [streams]
    (println "\nSTREAMS" streams)
    (if (nil? (keys streams))
      0
      (some #(if (nil? (streams %)) % nil) (range 16))))

  (on :pulse-new-playback-stream
    (fn [& args]
      (let [path (first args)]
        (swap! playback-streams
          (fn [streams]
            (assoc streams (first-free-slot streams) path))) ))) )