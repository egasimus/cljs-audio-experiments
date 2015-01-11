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

      (let [arguments ["-c" vst-name
                       vst-path]
            options   ["env" (extend-env "VST_PATH"  vst-dir
                                         "DSSI_PATH" "/home/epimetheus/code/kunst")]
            vst-info  (atom { :process (execute "vsthost" arguments options) })]

        ;(.on (.-stdout @vst-process) "data"
          ;(fn [data] (log :vst-stdout data)))

        (add-watch vst-info nil
          (fn [_ _ old-info new-info]
            (println old-info new-info)))

        (.on (.-stderr (@vst-info :process)) "data"
          (fn [data]
            (log :vst-stderr data)
            (let [data (str data)
                  jcn  "JACK client name: "
                  jip  "JACK input port: "
                  jop  "JACK output port: "]

              (when (= 0 (.indexOf (str data) jcn))
                (swap! vst-info assoc :jack-name
                  (.substr data (.-length jcn))))

              (when (= 0 (.indexOf (str data) jip))
                (swap! vst-info assoc :jack-inputs
                  (conj (or (@vst-info :jack-inputs) []) (.substr data (.-length jip)))))

              (when (= 0 (.indexOf (str data) jop))
                (swap! vst-info assoc :jack-outputs
                  (conj (or (@vst-info :jack-outputs) []) (.substr data (.-length jop)))))

            )))

        vst-info))))


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
                 :path "/home/epimetheus/vst/Turnado/Turnado32.dll"
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
