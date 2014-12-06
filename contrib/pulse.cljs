(let [pulse (js/require "node-pulseaudio-dbus")

      Core1 (.-Core1 pulse)
      core1 (Core1.)]

  (.on core1
    "NewPlaybackStream"
    (fn [path args sig]
      (println path args sig))))
