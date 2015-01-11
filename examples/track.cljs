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


(on :jack-ports-updated (fn [ins outs] (println ins outs)))


(def *session* {

  :name "Test Session"

  :tracks [
    { :name  "Bass"
      :chain [ (VST. "/home/epimetheus/vst/Swierk"  "Swierk")
               (VST. "/home/epimetheus/vst/Turnado" "Turnado32") ] }

    { :name  "Pad"
      :chain [ (VST. "/home/epimetheus/vst/Swierk"  "Swierk")
               (VST. "/home/epimetheus/vst/Turnado" "Turnado32") ] } ]

  :author "Mlad Konstruktor" } )


(defn start []

  (doseq [track (*session* :tracks)]

    (log :tracks
      "Creating track " (track :name))

    (doseq [module (track :chain)]
      (log :tracks
        "Creating module" (.-name module))
      (spawn module {})) ) )
