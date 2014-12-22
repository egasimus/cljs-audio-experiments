(use-module "osc")
(use-module "midi")
(use-module "spawn")


(defn vst [vst-path vst-name]
  (let [command     "jack-dssi-host"
        arguments   [(str "dssi-vst.so:" vst-name)]
        options     ["env"      (extend-env "VST_PATH" vst-path)
                     "detached" true]
        vst-process (spawn command arguments options)]
    (println vst-process)))


(vst "/home/epimetheus/vst/Swierk/" "Swierk.dll")
