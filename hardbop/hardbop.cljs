(ns hardbop.core
  (:require [cljs.repl :as repl]))

(defn pep [text]
  (println (str (repl/prompt) text))
  (repl/eval-print text)
  (println))

(defn -main [& args]
  (repl/init)

  ;; setup the print function
  (set! *out* #(.write (.-stdout js/process) %))
  (set! *rtn* #(.write (.-stdout js/process) %))
  (set! *err* #(.write (.-stderr js/process) %))
  (set! *print-fn* #(*out* %))

  ;; greet the user
  (println "\nbopbop boq doqdoq")

  (let [readline (js/require "readline")
        rl (.createInterface readline js/process.stdin js/process.stdout)]
    (.setPrompt rl (repl/prompt))
    (.prompt rl)
    (.on rl "line" (fn [line]
                     (when (seq (filter #(not= " " %) line))
                       (repl/eval-print line)
                       (println))
                     (.setPrompt rl (repl/prompt))
                     (.prompt rl)))

    (.on rl "close" (fn []
      (println "\nqodqoq pod bopbop")
      (.exit js/process 0)))))

  ;; this is gonna be printed on successful exit
  
(set! *main-cli-fn* -main)
