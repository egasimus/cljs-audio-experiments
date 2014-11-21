(let [midi  (js/require "midi")
      Input (.-input midi)
      input (Input.)]
  (.openPort input 1)
  (.on input "message"
    (fn [delta-time message]
      (println delta-time message))))
