(let [-spawn    (.-spawn (js/require "child_process"))
      -openSync (.-openSync (js/require "fs"))]

  (defn spawn
    [command arguments options]
    (let [arguments     (into-array arguments)
          options       (apply js-obj options)
          child-process (-spawn command arguments options)]
      (log :vst "Running:" command arguments options)
      (.on (.-stdout child-process) "data"
        (fn [data] (log :vst-stdout data)))
      (.on (.-stderr child-process) "data"
        (fn [data] (log :vst-stderr data)))
      (.on js/process "exit"
        (fn [_] (.kill child-process)))
      child-process)))


(defn extend-env
  [& args]
  (apply js-obj (apply concat (seq
    (apply assoc (js->clj (.-env js/process)) args))) ))
