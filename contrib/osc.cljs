(let [osc    (js/require "node-osc")
      moment (js/require "moment")

      Server (.-Server osc)
      server (Server. 3333 "0.0.0.0")

      Client (.-Client osc)]

  (defn osc-message [address & args]
    (js-obj "oscType" "message"
            "address" address
            "args"    (into-array args)))

  (defn osc-bundle [msgs]
    (js-obj "oscType"  "bundle"
            "timetag"  0
            "elements" (let [msgs1 (map #(apply osc-message %) msgs)
                             msgs2 (into-array msgs1)]
                        msgs2)))

  (.on server "message"
    (fn [msg rinfo]
      (trigger :osc-in msg rinfo)))

  (on :osc-send
    (fn [client msg args]
      (apply (.-send client) msg args)))

  (on :osc-connect
    (fn [addr port callback]
      (callback (Client. addr port)))))
