(ns hardbop.repl
  (:require [cljs.reader :as reader]
            [cljs.repl   :as repl]))


(js/require "colors")


;; pleasantries


(def HELLO   "!bopbop boq doqdoq¡")

(def GOODBYE "?qodqoq pod bopbop¿")


;; read
 

(defn get-file-contents
  [path]
  (let [fs (js/require "fs")]
    (.toString (.readFileSync fs path))))


(defn read-file-1
  [reader forms]
  (if-let [form (reader/read reader false nil)]
    (read-file-1 reader (conj forms form))
    forms))


(defn read-file
  [path]
  (let [reader (reader/string-push-back-reader (get-file-contents path))
        form   (reader/read reader false nil)]
    (if (not (nil? form))
      (read-file-1 reader [form])
      [])))


;; evaluate


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


(defn eval-file
  [path]
  (repl/evaluate-code (get-file-contents path)))


(defn use-module
  [module-name]
  (if-let [module-path (resolve-module module-name)]
    (do (let [session  @(*bop* :session)
              module   { :name module-name
                         :path module-path
                         :body (read-file module-path)
                         :deps [] }
              new-deps (conj (session :deps) module)]
          (swap! (*bop* :session) assoc :deps new-deps)
          (eval-file module-path)))
    nil))


;; printing, logging


(defn centered
  ([string] (centered string (.-columns (.-stdout js/process))))
  ([string width]
    (let [round   #((.-round js/Math) %)
          padding (round (/ (- width (count string)) 2))
          pad     (apply str (repeat padding " "))]
      (str pad string pad))) )


(defn log [target & values]
  (let [target-name (.substring (str target) 1)]
    (println (apply str (.-cyan (str "[" target-name "] ")) values))) )


(defn run-repl
  []
  (let [readline (js/require "readline")
        rl       (.createInterface readline js/process.stdin js/process.stdout)]
    (.setPrompt rl (str "\n" (repl/prompt)))
    (.prompt rl)

    (.on rl "line"
      (fn [line] (when (seq (filter #(not= " " %) line))
                   (repl/eval-print line)
                   (println))
                 (.setPrompt rl (repl/prompt))
                 (.prompt rl)))

    (.on rl "close"
      (fn [] (println (str "<EXIT>\n" (centered GOODBYE)))
             (.exit js/process 0))) ))


;; event system


(defn on
  [event handler]
  (let [event-map   @(*bop* :events)
        event-hooks (or (event-map event) [])
        new-events  (assoc event-map event (conj event-hooks handler))]
    (reset! (*bop* :events) new-events)))


(defn emit
  ([event & params]
    (let [event-hooks (@(*bop* :events) event)]
      (doseq [hook event-hooks]
        (apply hook params))) ))


;; global state


(def *bop* { :cwd       (.cwd js/process)
             :session   (atom {})
             :events    (atom {})
             :verbosity (atom {}) })


;; session loader


(defn open-session! [session-path]
  (if (string? session-path)
    (let [full-session-path (.resolve (js/require "path") session-path)]
      (if (.existsSync (js/require "fs") full-session-path)
        (do (log :session "Opening session" full-session-path)
            (reset! (:session *bop*) { :name "session-path"
                                       :path full-session-path
                                       :body (read-file full-session-path) })
            (eval-file full-session-path))
        (log :session "File not found:" full-session-path)))
    (log :session session-path "is not a valid session path.")))


;; with all that out of the way,
;; let's try our hand at startup


(set! *main-cli-fn* (fn
  [& args]

  ; we're home
  (repl/init)

  ; setup print functions
  (let [stdout #(.write (.-stdout js/process) %)
        stderr #(.write (.-stderr js/process) %)]
    (set! *err* stderr)
    (set! *out* stdout)
    (set! *rtn* stdout)
    (set! *print-fn* stdout))

  ; oh my, where are our manners?
  (println (str (centered HELLO) "\n"))

  ; load session if passed to command line
  (if-let [session (nth (.-argv js/process) 2)]
    (open-session! session)
    (log :session "Starting new session."))

  ; evaluate session contents

  ; setup readline interface to repl
  (run-repl)))
