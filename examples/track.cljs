(use-module "execute")
(use-module "jack")


(defprotocol Spawnable
  (spawn [this options]))


(deftype VST [vst-path vst-name])

(extend-protocol Spawnable VST
  (spawn [this options]
    (let [vst-path (.-vst-path this)
          vst-name (.-vst-name this)]

      (println options)

      (log :module "Spawning module" vst-path vst-name)

      (let [arguments   ["-c" vst-name
                         (str vst-path "/" vst-name)]
            options     ["env" (extend-env "VST_PATH"  vst-path
                                           "DSSI_PATH" "/home/epimetheus/code/kunst")]
            vst-process (atom (execute "vsthost" arguments options))]

        ;(.on (.-stdout @vst-process) "data"
          ;(fn [data] (log :vst-stdout data)))

        ;(.on (.-stderr @vst-process) "data"
          ;(fn [data] (log :vst-stderr data)))

        vst-process))))


(deftype TrackChain [modules])

(extend-protocol Spawnable TrackChain
  (spawn [this options]
    (doseq [m (.-modules this)]
      (log :track "Spawning module")
      (spawn m { :parent this }))))

(defn track-chain [& modules]
  (TrackChain. modules))


(deftype TrackSession [tracks])

(extend-protocol Spawnable TrackSession
  (spawn [this options]
    (doseq [t (.-tracks this)]
      (log :tracks "Spawning" (first t))
      (spawn (second t) { :parent this
                          :name   (first t) }) )))

(defn track-session [& tracks]
  (TrackSession. (apply hash-map tracks)))


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
  (spawn *session* {}))
