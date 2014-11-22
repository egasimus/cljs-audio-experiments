;(metadata 
  ;:title  "Four SooperLooper tracks."
  ;:author "Mlad Konstruktor <fallenblood@gmail.com>")


(use-module "osc")
(use-module "midi")
(use-module "spawn")


(trigger :midi-out 176 0 0)
(trigger :midi-out 176 0 1)


(let [timers (js/require "timers")]
  (on :beat (fn [args] (.setTimeout timers #(trigger :beat) 100))))


(on :midi-in (fn [args]   (println "midi in" args)))
(on :osc-in  (fn [& args] (println "osc in"  args)))


(defn looper-1
  [pos]
  (let [next-pos (cond (< pos 0)  0
                       (= pos 7)  16
                       (> pos 22) 0
                       :else      (+ 1 pos))]
    (doseq [i [0  1  2  3  4  5  6  7
              16 17 18 19 20 21 22 23]]
      (trigger :midi-out 144 i 0))
    (trigger :midi-out 144 pos 15)
    next-pos))


(defn looper
  [sample]
  (let [;sooperlooper (spawn "sooperlooper"
                       ;["-D" "no"
                       ; "-p" "3334"])
        position     (atom 0)]

    (on :beat
      (fn [args]
        (swap! position looper-1)))

    (on :midi-in
      (fn [args]
        (let [msg  (nth (nth args 0) 1)
              step (nth msg 1)
              vel  (nth msg 2)]
          (when (= vel 127) (reset! position (looper-1 step))) )))

    (on :osc-in
      (fn [& args]
        (println "and again" args)))

    (trigger :osc-connect "localhost" "9999"
      (fn [client]
        (.send client "/ping" "localhost:9999" "/pont")))

    (trigger :osc-connect "localhost" "9951"
      (fn [client]
        (.send client "/sl/0/load_loop" sample "" "")
        (.send client "/sl/0/hit" "trigger")
        (.send client "/ping" "localhost:9999" "/pont2"))) ))


(looper "/home/epimetheus/hear/samples/kunststruktur/drums/140bpm-vec3-breakbeat-011.wav")
