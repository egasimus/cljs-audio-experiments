(let [osc    (js/require "node-osc")

      Server (.-Server osc)
      server (Server. 3333 "0.0.0.0")

      Client (.-Client osc)]

  (.on server "message"
    (fn [msg rinfo]
      (trigger :osc-in msg rinfo)))

  (on :osc-connect
    (fn [args]
      (let [addr     (nth args 0)
            port     (nth args 1)
            callback (nth args 2)
            client   (Client. addr port)]
        (callback client)))))
