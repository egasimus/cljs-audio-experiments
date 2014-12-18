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
        (.on (.-stdout child-process) "data"
          (fn [data] (println data)))
        (.on (.-stderr child-process) "data"
          (fn [data] (println data)))
        (.on js/process "exit"
          (fn [_] (.kill child-process))) ))) )
