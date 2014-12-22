(use-module "osc")
(use-module "midi")
(use-module "spawn")

(defn vst [vst-path vst-name]
  (let [vst-process (spawn "jack-dssi-host"
                           (array  (str "dssi-vst.so:" vst-name))
                           (js-obj "env" (js-obj "VST_PATH" vst-path)))]
    (println vst-process)))
