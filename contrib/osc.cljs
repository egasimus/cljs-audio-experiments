(let [osc    (js/require "node-osc")
      Server (.-Server osc)
      server (Server. 3333 "0.0.0.0")]
  (.on server "message"
    (fn [msg rinfo]
      (trigger :osc-msg { :msg  msg
                          :info rinfo })) ))

(println *bop*)
