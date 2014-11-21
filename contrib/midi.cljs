(let [midi   (js/require "midi")

      Input  (.-input midi)
      input  (Input.)

      Output (.-output midi)
      output (Output.)]

  (.openPort output 1)
  (println (.getPortName output 1))
  (on :midi-out
    (fn [args]
      (println args)
      (.sendMessage output (into-array args))))

  (.openPort input 1)
  (println (.getPortName input 1))
  (.on input "message"
    (fn [delta-time message]
      (trigger :midi-in [delta-time message])) ))
