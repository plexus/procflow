(ns user)

(defmacro jit
  "Just in time loading of dependencies."
  [sym]
  `(do
     (require '~(symbol (namespace sym)))
     (find-var '~sym)))

(defn set-prep! []
  ((jit integrant.repl/set-prep!) #((jit procflow.system/config) :dev)))

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
