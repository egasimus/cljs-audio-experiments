(use-module "pulse")

(use-module "midi")

(let [midi-ports    (refresh-midi-ports)

      starts-with   (fn [str1 str2] (= 0 (.lastIndexOf str1 str2)))

      get-port-name (fn [port-type port-name]
                      (first (filter #(starts-with % port-name)
                                     (keys (midi-ports port-type)))))

      midi-in-name  (get-port-name :in  "Midi Fighter")
      
      midi-out-name (get-port-name :out "Midi Fighter")]

  (println)

  (println "Connecting to MIDI in: " midi-in-name)
  (open-midi-in midi-in-name)

  (println "Connecting to MIDI out:" midi-out-name)
  (open-midi-out midi-out-name)
)
