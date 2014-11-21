;(metadata 
  ;:title  "Four SooperLooper tracks."
  ;:author "Mlad Konstruktor <fallenblood@gmail.com>")


(use-module "osc")


(use-module "midi")


(def timers (js/require "timers"))


(on :midi-in (fn [args] (println args)))


(on :osc-in  (fn [args] (println args)))


(trigger :midi-out 176 0 0)
(trigger :midi-out 176 0 1)


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
  []
  (let [position (atom 0)]

    (on :beat
      (fn [args]
        (swap! position looper-1)))

    (on :midi-in
      (fn [args]
        (let [msg  (nth (nth args 0) 1)
              step (nth msg 1)
              vel  (nth msg 2)]
          (when (= vel 127) (reset! position (looper-1 step))) ))) ))


(on :beat (fn [args] (.setTimeout timers #(trigger :beat) 100)))


(looper)
