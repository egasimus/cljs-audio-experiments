(use-module "execute")
(use-module "jack")


(on :jack-ports-updated (fn [ins outs] (println ins outs)))


(defprotocol Spawnable
  (spawn [this options]))


(defn spawn-vst [vst]
  (let [vst-path (vst :path)
        vst-name (vst :name)]

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

      vst-process)))


(def *session* {

  :name "Test Session"

  :tracks [
    { :name  "Bass"
      :chain [ { :type :vst
                 :path "/home/epimetheus/vst/Swierk"
                 :name "Swierk" }
               { :type :vst
                 :path "/home/epimetheus/vst/Turnado"
                 :name "Turnado32" } ] }

    { :name  "Pad"
      :chain [ { :type :vst
                 :path "/home/epimetheus/vst/Swierk"
                 :name "Swierk" }
               { :type :vst
                 :path "/home/epimetheus/vst/Turnado"
                 :name "Turnado32" } ] } ]

  :author "Mlad Konstruktor" } )


(defn start []

  (doseq [track (*session* :tracks)]

    (log :tracks
      "Creating track " (track :name))

    (doseq [module (track :chain)]
      (log :tracks
        "Creating module" (.-name module))
      (spawn-vst module)) ) )
