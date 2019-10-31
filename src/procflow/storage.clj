(ns procflow.storage
  (:require [integrant.core :as ig]
            [crux.api :as crux]))

(defmethod ig/init-key ::crux [_ config]
  (-> config
      crux/start-node
      (with-meta config)))

(defmethod ig/halt-key! ::crux [_ crux]
  (.close crux))
