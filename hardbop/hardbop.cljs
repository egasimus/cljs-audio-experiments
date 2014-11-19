(ns hardbop.core
  (:require [cljs.repl :as repl]))

(def *js-path*      (js/require "path"))

(def *bop-cwd*      (.cwd js/process))

(def *bop-session*  (if-let [session (nth (.-argv js/process) 2)]
                      (.resolve *js-path* session)
                      "untitled"))

(defn pep [text]
  (println (str (repl/prompt) text))
  (repl/eval-print text)
  (println))

(defn -main [& args]

  ;; we're home
  (repl/init)

  ;; setup print functions
  (set! *out* #(.write (.-stdout js/process) %))
  (set! *rtn* #(.write (.-stdout js/process) %))
  (set! *err* #(.write (.-stderr js/process) %))
  (set! *print-fn* #(*out* %))

  ;; where are our manners?
  (println "\nbopbop boq doqdoq")
  (println *bop-session*)

  ;; setup readline interface to repl
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
  
(set! *main-cli-fn* -main)
