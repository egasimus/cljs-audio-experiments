(let osc (js/require "node-osc")

    (def *osc-server* (.Server osc 3333 "0.0.0.0")))
