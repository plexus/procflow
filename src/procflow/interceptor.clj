(ns procflow.interceptor)

(defonce registry (atom {}))

(defn register! [name interceptor]
  (swap! registry assoc name interceptor))

(defn lookup [name]
  (get @registry name))

(defmacro defhandler ^{:style/indent [2]} [name & ftail]
  `(register! ~name (fn ~@ftail)))

(defmacro definterceptor ^{:style/indent [1]} [name & body]
  `(register! ~name ~(assoc (apply array-map body) :name name)))
