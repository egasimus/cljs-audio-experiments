(let [jack (js/require "jack-connector")]

  (.openClientSync jack "Hardbop")

  (.registerOutPortSync jack "foo")

  (.activateSync jack)

  (defn jack-connect
    [source sink]
    (.connectPortSync jack source sink)))
