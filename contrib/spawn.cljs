(let [-spawn    (.-spawn (js/require "child_process"))
      -openSync (.-openSync (js/require "fs"))]

  (defn spawn
    ([command]
      (spawn command []))
    ([command args]
      (spawn command args {}))
    ([command args options]
      (-spawn command args options))) )
