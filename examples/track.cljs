(use-module "execute")
(use-module "jack")


(on :jack-ports-updated (fn [ins outs] (println ins outs)))


(let [path (js/require "path")]

  (defn spawn-vst [vst]
    (let [vst-path (.resolve path (vst :path))
          vst-dir  (.dirname path vst-path)
          vst-file (str (.basename path vst-path))
          vst-name (or (vst :name) (.basename vst-path))]

      (log :module "Spawning VST plugin" vst-name (str "(" vst-file ")"))

      (let [arguments   ["-c" vst-name
                         vst-path]
            options     ["env" (extend-env "VST_PATH"  vst-dir
                                           "DSSI_PATH" "/home/epimetheus/code/kunst")]
            vst-process (atom (execute "vsthost" arguments options))]

        ;(.on (.-stdout @vst-process) "data"
          ;(fn [data] (log :vst-stdout data)))

        ;(.on (.-stderr @vst-process) "data"
          ;(fn [data] (log :vst-stderr data)))

        vst-process))))


(def *session* {

  :name "Test Session"

  :tracks [
    { :name  "Bass"
      :chain [ { :type :vst
                 :path "/home/epimetheus/vst/Swierk/Swierk.dll"
                 :name "Swierk" }
               { :type :vst
                 :path "/home/epimetheus/vst/Turnado/Turnado32.dll"
                 :name "Turnado" } ] }

    { :name  "Pad"
      :chain [ { :type :vst
                 :path "/home/epimetheus/vst/Swierk/Swierk.dll"
                 :name "Swierk" }
               { :type :vst
                 :path "/home/epimetheus/vst/Turnado/Turnado.dll"
                 :name "Turnado" } ] } ]

  :author "Mlad Konstruktor" } )


(defn start []

  (doseq [track (*session* :tracks)]

    (log :tracks
      "Creating track" (track :name))

    (doseq [module (track :chain)]
      (log :tracks
        "Creating module" (module :name))
      (spawn-vst module)) ) )
