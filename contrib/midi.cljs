(let [midi   (js/require "midi")

      Input  (.-input midi)
      input  (Input.)

      Output (.-output midi)
      output (Output.)]

  (def midi-ins (atom nil))

  (def midi-outs (atom nil))

  (defn refresh-midi-ports
    []
    (let [
      get-ports (fn [io]
        (apply hash-map (apply concat
          (map-indexed #(vector (.getPortName io %1) %1)
                       (range (.getPortCount io))) )))
      inputs  (get-ports input)
      outputs (get-ports output)]

      (reset! midi-ins  inputs)
      (reset! midi-outs outputs)

      { :in  inputs
        :out outputs }))

  (defn open-midi-in
    [port]
    (.openPort input (@midi-ins port)))

  (defn open-midi-out
    [port]
    (.openPort output (@midi-outs port)))

  (def midi-msg-types
    {:note-off   128
     :note-on    144
     :aftertouch 160
     :control    176
     :program    192
     :pressure   208
     :pitch      224})

  (defn make-midi-msg
    [typ chan data1 data2]
    (let [args (hash-map args)]
      (vector (+ (midi-msg-types typ) chan) data1 data2)))

  (defn midi-send
    [status data1 data2]
    (.sendMessage output (array status data1 data2)))

  (defn midi-out
    [& args]
    (apply midi-send (apply make-midi-msg args)))

  (on :midi-out #(midi-send %1 %2 %3))

  (.on input "message"
    (fn [delta-time message]
      (trigger :midi-in delta-time message))) )
