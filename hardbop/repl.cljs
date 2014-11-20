(ns hardbop.repl
  (:require [cljs.reader :as reader]
            [cljs.repl   :as repl]))


; pleasantries


(def HELLO   "!bopbop boq doqdoq¡")

(def GOODBYE "?qodqoq pod bopbop¿")


; reader
 

(defn read-file
  [path]
  (let [fs (js/require "fs")]
    (.toString (.readFileSync fs path))))


(defn read-cljs-1
  [reader forms]
  (if-let [form (reader/read reader false nil)]
    (read-cljs-1 reader (conj forms form))
    forms))


(defn read-cljs
  [path]
  (let [reader (reader/string-push-back-reader (read-file path))
        form   (reader/read reader false nil)]
    (if (not (nil? form))
      (read-cljs-1 reader [form])
      [])))


; repl


(defn evaluate-cljs
  [path]
  (repl/evaluate-code (read-file path)))


(defn run-repl
  []
  (let [readline (js/require "readline")
        rl       (.createInterface readline js/process.stdin js/process.stdout)]
    (.setPrompt rl (repl/prompt))
    (.prompt rl)

    (.on rl "line"
      (fn [line] (when (seq (filter #(not= " " %) line))
                   (repl/eval-print line)
                   (println))
                 (.setPrompt rl (repl/prompt))gt
                 (.prompt rl)))

    (.on rl "close"
      (fn [] (println (str "\n\n" GOODBYE))
             (.exit js/process 0)))))


; global state


(let [session-path (if-let [session (nth (.-argv js/process) 2)]
                             (.resolve (js/require "path") session)
                             "untitled")]
  (def *bop* { :cwd          (.cwd js/process)
               :session-path session-path
               :session      (read-cljs session-path) }))


; startup


(set! *main-cli-fn* (fn
  [& args]

  ;; we're home
  (repl/init)

  ;; setup print functions
  (set! *out* #(.write (.-stdout js/process) %))
  (set! *rtn* #(.write (.-stdout js/process) %))
  (set! *err* #(.write (.-stderr js/process) %))
  (set! *print-fn* #(*out* %))

  ;; oh my, where are our manners?
  (println (str "\n" HELLO "\n"))
  (println "*bop*" *bop* "\n")

  ;; setup readline interface to repl
  (run-repl)))
