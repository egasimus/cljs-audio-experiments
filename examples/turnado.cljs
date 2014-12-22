(use-module "osc")
(use-module "midi")
(use-module "spawn")


(defn spawn-dssi-vst [& args]
  (let [child-process (apply spawn "jack-dssi-host" args)]
    (.on (.-stdout child-process) "data"
      (fn [data] (log :vst-stdout data)))
    (.on (.-stderr child-process) "data"
      (fn [data] (log :vst-stderr data)))
    child-process))


(defn spawn-dssi-vst-gui [vst-name osc-url]
  (let [child-process (spawn "/usr/lib32/dssi/dssi-vst/dssi-vst_gui"
                             [osc-url "dssi-vst.so" vst-name vst-name])]
    (.on (.-stdout child-process) "data"
      (fn [data] (log :vst-gui-stdout data)))
    (.on (.-stderr child-process) "data"
      (fn [data] (log :vst-gui-stderr data)))
    child-process))


(defn vst [vst-path vst-name]
  (let [arguments   [(str "dssi-vst.so:" vst-name)]
        options     ["env" (extend-env "VST_PATH" vst-path)]
        vst-process (atom (spawn-dssi-vst arguments options))
        gui-process (atom nil)
        osc-url     (atom nil)]

    (add-watch osc-url nil
      (fn [_ _ _ new-url]
        (log :vst-gui "Starting GUI for" vst-name "on" new-url)
        (reset! gui-process (spawn-dssi-vst-gui vst-name new-url))))

    (.on (.-stdout @vst-process) "data" (fn [data]
      (let [data (.trim (str data))]
        (if (> (.indexOf data "OSC URL is") -1)
          (let [osc-url- (.trim (nth (.split data "\n") 1))]
            (log :vst-meta
              "Intercepted OSC URL for" (str vst-name ":") osc-url-)
            (reset! osc-url osc-url-))) ))) ))


(vst "/home/epimetheus/vst/Swierk/"         "Swierk.dll")
(vst "/home/epimetheus/wine32/drive_c/VST/" "Turnado.dll")
