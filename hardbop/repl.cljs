(ns hardbop.repl
  (:require [cljs.repl :as repl]))


(def HELLO   "!bopbop boq doqdoq¡")

(def GOODBYE "?qodqoq pod bopbop¿")


(def *bop-cwd*      (.cwd js/process))

(def *bop-session*  (if-let [session (nth (.-argv js/process) 2)]
                      (.resolve (js/require "path") session)
                      "untitled"))

(defn evaluate [path]
  (let [fs (js/require "fs")
        text (.toString (.readFileSync fs *bop-session*))]
    (repl/eval-print text)))


(defn run-repl []
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
      (println (str "\n\n" GOODBYE))
      (.exit js/process 0)))))


(defn -main [& args]

  ;; we're home
  (repl/init)

  ;; setup print functions
  (set! *out* #(.write (.-stdout js/process) %))
  (set! *rtn* #(.write (.-stdout js/process) %))
  (set! *err* #(.write (.-stderr js/process) %))
  (set! *print-fn* #(*out* %))
 
  ;; evaluate the session contents 
  (evaluate *bop-session*)

  ;; oh my, where are our manners?
  (println (str "\n\n" HELLO "\n"))
  (println "*bop-cwd*     " *bop-cwd*)
  (println "*bop-session* " *bop-session*)
  (println)

  ;; setup readline interface to repl
  (run-repl))
  
(set! *main-cli-fn* -main)
