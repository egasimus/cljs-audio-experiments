(let [pulse (js/require "node-pulseaudio-dbus")

      Core1 (.-Core1 pulse)
      core1 (Core1.)]

  (defn pulse-get-playback-streams
    [callback]
    (.getPlaybackStreams core1 callback))

  (defn pulse-get-stream
    [path callback]
    (.getStream core1 path callback))

  (defn pulse-get-volume
    [obj callback]
    (.getVolume obj (fn [err volume]
      (let [data (nth (nth volume 1) 0)]
        (callback err (into [] data))) )))

  (defn pulse-set-volume
    [obj value]
    (set! (.-volume obj) value))

  (.on core1
    "NewPlaybackStream"
    (fn [path args sig]
      (apply trigger :pulse-new-playback-stream args)))

  (.on core1
    "PlaybackStreamRemoved"
    (fn [path args sig]
      (apply trigger :pulse-playback-stream-removed args))) )
