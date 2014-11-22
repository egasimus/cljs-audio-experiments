(let [midi   (js/require "midi")

      Input  (.-input midi)
      input  (Input.)

      Output (.-output midi)
      output (Output.)]

  (.openPort output 1)
  (on :midi-out
    (fn [status data1 data2]
      (.sendMessage output (array status data1 data2))) )

  (.openPort input 1)
  (.on input "message"
    (fn [delta-time message]
      (trigger :midi-in delta-time message))) )
