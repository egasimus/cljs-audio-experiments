(use-module "pulse")

(use-module "midi")

(let [midi-ports    (refresh-midi-ports)

      starts-with   (fn [str1 str2] (= 0 (.lastIndexOf str1 str2)))

      midi-out-name (first (filter #(starts-with % "Midi Fighter")
                           (keys (:outs midi-ports))) )]

  (println)
  (println "Connecting to MIDI out:" midi-out-name)
  (open-midi-out midi-out-name)
)
