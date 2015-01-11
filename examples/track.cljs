(use-module "execute")
(use-module "jack")


(deftype VST [vst-path vst-name])


(deftype TrackChain [modules])
(defn track-chain [& modules]
  (TrackChain. modules))


(deftype TrackSession [tracks])
(defn track-session [& tracks]
  (TrackSession. (apply hash-map tracks)))


(defprotocol Spawnable (spawn [this]))
(extend-protocol Spawnable
  VST
  (spawn [this]
    (let [vst-path (.-vst-path this)
          vst-name (.-vst-name this)]
      (log :module "Spawning module" vst-path vst-name)
      (let [arguments   [(str vst-path "/" vst-name)]
            options     ["env" (extend-env "VST_PATH" vst-path)]
            vst-process (atom (execute "vsthost" arguments options))]
        (.on (.-stdout @vst-process) "data"
          (fn [data] (log :vst-stdout data)))
        (.on (.-stderr @vst-process) "data"
          (fn [data] (log :vst-stderr data)))
        vst-process)))

  TrackChain
  (spawn [this]
    (doseq [m (.-modules this)]
      (log :track "Spawning module")
      (spawn m)))

  TrackSession
  (spawn [this]
    (doseq [t (.-tracks this)]
      (log :tracks "Spawning" (first t))
      (spawn (second t))))
)

(on :jack-ports-updated (fn [ins outs] (println ins outs)))

(def *session* (track-session

  "Bass" (track-chain
    (VST. "/home/epimetheus/vst/Swierk"  "Swierk")
    (VST. "/home/epimetheus/vst/Turnado" "Turnado32"))

  "Pad"  (track-chain
    (VST. "/home/epimetheus/vst/Swierk"  "Swierk")
    (VST. "/home/epimetheus/vst/Turnado" "Turnado32")))

)


(defn start []
  (spawn *session*))
