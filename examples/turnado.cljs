(use-module "osc")
(use-module "midi")
(use-module "spawn")


(defn vst [vst-path vst-name]
  (let [command     "jack-dssi-host"
        arguments   [(str "dssi-vst.so:" vst-name)]
        options     ["env"      (extend-env "VST_PATH" vst-path)
                     "detached" true]
        vst-process (spawn command arguments options)]

    (.on (.-stdout vst-process) "data" (fn [data]
      (let [data (.trim (str data))]
        (if (> (.indexOf data "OSC URL is") -1)
          (log :vst-meta "Here's our OSC URL!")))))

    (println vst-process)))


(vst "/home/epimetheus/vst/Swierk/" "Swierk.dll")
