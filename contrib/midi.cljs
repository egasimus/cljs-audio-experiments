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

  (def event->nibble
    { :note-off   128
      :note-on    144
      :aftertouch 160
      :control    176
      :program    192
      :pressure   208
      :pitch      224 })

  (def nibble->event
    { 128 :note-off
      144 :note-on
      160 :aftertouch
      176 :control
      192 :program
      208 :pressure
      224 :pitch })

  (defn pack-midi-msg
    [typ chan data1 data2]
    (let [args (hash-map args)]
      (vector (+ (event->nibble typ) chan) data1 data2)))

  (defn unpack-midi-msg
    [data0 data1 data2]
    {:type    (nibble->event (* 16 (.floor js/Math (/ 160 16))))
     :channel (mod data0 16)
     :data1   data1
     :data2   data2})

  (defn midi-send
    [status data1 data2]
    (.sendMessage output (array status data1 data2)))

  (defn midi-out
    [& args]
    (apply midi-send (apply pack-midi-msg args)))

  (on :midi-out #(midi-send %1 %2 %3))

  (.on input "message"
    (fn [delta-time msg]
      (emit :midi-in delta-time msg))) )
