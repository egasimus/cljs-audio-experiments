(use-module "execute")

(defprotocol Spawnable
  (spawn [this]))

(deftype VST [vst-path vst-name] Spawnable
  (spawn [this]
    (log :module "Spawning module" vst-path vst-name)
    (let [arguments   [(str vst-path "/" vst-name)]
          options     ["env" (extend-env "VST_PATH" vst-path)]
          vst-process (atom (execute "vsthost" arguments options))]
      (.on (.-stdout child-process) "data"
        (fn [data] (log :vst-stdout data)))
      (.on (.-stderr child-process) "data"
        (fn [data] (log :vst-stderr data)))
      vst-process)))

(deftype TrackChain [modules] Spawnable
  (spawn [this]
    (doseq [m modules]
      (log :track "Spawning module")
      (spawn m))))

(defn track-chain [& modules]
  (TrackChain. modules))

(deftype TrackSession [tracks] Spawnable
  (spawn [this]
    (doseq [t tracks]
      (log :tracks "Spawning" (first t))
      (spawn (second t)))))

(defn track-session [& tracks]
  (TrackSession. (apply hash-map tracks)))

(def *session* (track-session
   "Bass" (track-chain (VST. "/home/epimetheus/vst/Swierk"  "Swierk")
                       (VST. "/home/epimetheus/vst/Turnado" "Turnado32"))
   "Pad"  (track-chain (VST. "/home/epimetheus/vst/Swierk"  "Swierk")
                       (VST. "/home/epimetheus/vst/Turnado" "Turnado32"))))

(defn start []
  (spawn *session*))
