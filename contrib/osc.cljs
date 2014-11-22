(let [osc    (js/require "node-osc")

      Server (.-Server osc)
      server (Server. 3333 "0.0.0.0")

      Client (.-Client osc)]

  (.on server "message"
    (fn [msg rinfo]
      (trigger :osc-in msg rinfo)))

  (on :osc-send
    (fn [client msg args]
      (println client msg args)
      (apply .send client msg args)))

  (on :osc-connect
    (fn [addr port callbackrgs]
      (callback (Client. addr port)))))
