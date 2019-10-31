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
