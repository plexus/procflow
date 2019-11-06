(ns user)

(defmacro jit
  "Just in time loading of dependencies."
  [sym]
  `(requiring-resolve '~sym))

(defn set-prep! []
  ((jit integrant.repl/set-prep!)
   (fn []
     (doto ((jit procflow.system/config) :dev)
       ((jit integrant.core/load-namespaces))))))

(defn go []
  (set-prep!)
  (require 'clojure.spec.alpha)
  (set! clojure.spec.alpha/*explain-out* @(jit expound.alpha/printer))
  ((jit integrant.repl/go)))

(defn reset []
  (set-prep!)
  ((jit integrant.repl/reset)))

(defn system []
  @(jit integrant.repl.state/system))

(defn config []
  @(jit integrant.repl.state/config))

(defn db []
  ((jit crux.api/db) (:procflow.storage/crux (system))))

(defn shadow-cljs-repl
  ([]
   (shadow-cljs-repl :ui))
  ([build-id]
   (loop []
     (when (nil? @@(jit shadow.cljs.devtools.server.runtime/instance-ref))
       (Thread/sleep 250)
       (recur)))
   ((jit shadow.cljs.devtools.api/nrepl-select) build-id)))
