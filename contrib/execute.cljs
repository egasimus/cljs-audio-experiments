(let [-spawn    (.-spawn (js/require "child_process"))
      -openSync (.-openSync (js/require "fs"))]

  (defn execute
    [command arguments options]
    (let [arguments     (into-array arguments)
          options       (apply js-obj options)
          child-process (-spawn command arguments options)]
      (log :spawn "Running:" command arguments options)
      (.on js/process "exit"
        (fn [_] (.kill child-process)))
      child-process)))


(defn extend-env
  [& args]
  (apply js-obj (apply concat (seq
    (apply assoc (js->clj (.-env js/process)) args))) ))
