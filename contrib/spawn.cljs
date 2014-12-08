(let [-spawn    (.-spawn (js/require "child_process"))
      -openSync (.-openSync (js/require "fs"))]

  (defn spawn
    ([command]
      (spawn command []))
    ([command args]
      (spawn command args {}))
    ([command args options]
      (println command args options)
      (let [child-process (-spawn command args options)]
        (.on js/process "exit"
          (fn [_] (.kill child-process))) ))) )
