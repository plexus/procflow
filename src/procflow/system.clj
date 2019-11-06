(ns procflow.system
  (:require [aero.core :as aero]
            [clojure.java.io :as io]
            [integrant.core :as integrant]))

(defmethod aero/reader 'ig/ref
  [_ tag value]
  (integrant/ref value))

(defn config [profile]
  (aero/read-config (io/resource "procflow/system.edn") {:profile profile}))
