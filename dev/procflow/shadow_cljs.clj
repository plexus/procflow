(ns procflow.shadow-cljs
  (:require [shadow.cljs.devtools.server :as server]
            [shadow.cljs.devtools.api :as shadow]
            [integrant.core :as ig]))

(defmethod ig/init-key :procflow/shadow-cljs [_ {:keys [builds watch-opts] :as config}]
  (server/start!)
  (doseq [build builds]
    (shadow/watch build watch-opts))
  config)

(defmethod ig/halt-key! :procflow/shadow-cljs [_ _]
  (server/stop!))
