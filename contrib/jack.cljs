(let [jack (js/require "jack-connector")]

  (.openClientSync jack "Hardbop")

  (.registerOutPortSync jack "foo")

  (.activateSync jack)

  (let [ports-in  (atom [])
        ports-out (atom [])]

    (set! *bop* (assoc *bop* :jack { :ins  ports-in
                                     :outs ports-out }))

    (defn jack-refresh-ports []
      (reset! ports-in  (apply vector (.getOutPortsSync jack)))
      (reset! ports-out (apply vector (.getInPortsSync  jack))))

    ((fn timer []
      (jack-refresh-ports)
      (js/setTimeout timer 1000))))

  (defn jack-connect
    [source sink]
    (.connectPortSync jack source sink)))
