(ns procflow.system
  (:require [clojure.java.io :as io]
            [integrant.core :as ig]

            [procflow.http :as http]

            [integrant.core :as integrant]
            [aero.core :as aero]
            [crux.api :as crux]
            [pohjavirta.server :as pohjavirta]))

(defmethod aero/reader 'ig/ref
  [_ tag value]
  (integrant/ref value))

(defn config [profile]
  (aero/read-config (io/resource "procflow/system.edn") {:profile profile}))

(defmethod ig/init-key ::crux [_ config]
  (-> config
      crux/start-node
      (with-meta config)))

(defmethod ig/halt-key! ::crux [_ crux]
  (.close crux))

(defmethod ig/init-key ::http [_ config]
  (doto (-> config
            http/handler
            (pohjavirta/create config))
    (pohjavirta/start)))

(defmethod ig/halt-key! ::http [_ http]
  (pohjavirta/stop http))
