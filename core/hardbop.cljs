(ns hardbop.repl
  (:require [cljs.reader :as reader]
            [cljs.repl   :as repl]))


; pleasantries


(def HELLO   "!bopbop boq doqdoq¡")

(def GOODBYE "?qodqoq pod bopbop¿")


; read
 

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


; evaluate


(defn make-map
  [coll]
  (zipmap (take-nth 2 coll)
          (take-nth 2 (rest coll))))


(defn resolve-module
  [module]
  (let [path (js/require "path")
        fs   (js/require "fs")

        module-path (.resolve path (.join path
          "contrib" (str module ".cljs")))]

    (if (.existsSync fs module-path)
      module-path
      nil)))


(defn use-module
  [module]
  (if-let [module-path (resolve-module module)]
    (do (println "loading module" module "from" module-path)
        (println (read-cljs module-path)))
    nil))

(defn eval-module-1
  [form]
  (let [head (first form)
        tail (rest  form)]
    (condp = head
      'metadata (let [info (make-map tail)]
                  (println "\nauthor ::" (info :author)
                           "\n title ::" (info :title)))
      'use      (do (println " using ::" (apply str tail))
                    (doseq [module tail] (use-module module)))
      nil)))


(defn eval-module
  [module]
  (doseq [form module]
    (eval-module-1 form)))


(defn eval-file
  [path]
  (repl/evaluate-code (read-file path)))


; print


(defn centered
  ([string] (centered string (.-columns (.-stdout js/process))))
  ([string width]
    (let [round   #((.-round js/Math) %)
          padding (round (/ (- width (count string)) 2))
          pad     (apply str (repeat padding " "))]
      (str pad string pad))))


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
      (fn [] (println (str "<EXIT>\n" (centered GOODBYE)))
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
  (let [stdout #(.write (.-stdout js/process) %)
        stderr #(.write (.-stderr js/process) %)]
    (set! *err* stderr)
    (set! *out* stdout)
    (set! *rtn* stdout)
    (set! *print-fn* stdout))

  ;; oh my, where are our manners?
  (println (str (centered HELLO) "\n"))
  (println "*bop*" *bop*)

  ;; evaluate session contents
  (eval-module (*bop* :session))

  ;; setup readline interface to repl
  (println)
  (run-repl)))
