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
  ((jit integrant.repl/go)))

(defn reset []
  (set-prep!)
  ((jit integrant.repl/reset)))

(defn system []
  @(jit integrant.repl.state/system))

(defn config []
  @(jit integrant.repl.state/config))

(defn db []
  ((jit crux.api/db) (:procflow.system/crux (system))))
