(let [pulse (js/require "node-pulseaudio-dbus")

      Core1 (.-Core1 pulse)
      core1 (Core1.)]

  (.on core1
    "NewPlaybackStream"
    (fn [path args sig]
      (apply trigger :pulse-new-playback-stream args)))

  (.on core1
    "PlaybackStreamRemoved"
    (fn [path args sig]
      (apply trigger :pulse-playback-stream-removed args))) )
