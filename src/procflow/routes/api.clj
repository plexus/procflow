(ns procflow.routes.api
  (:require [crux.api :as c]
            [procflow.db :as db]
            [procflow.interceptor :refer [definterceptor]])
  (:import java.util.UUID))

(def touch (partial db/touch #{:procflow.procedure/steps
                               :procflow.procedure/owner}))

(definterceptor ::procedure
  :enter
  (fn [{:procflow.system/keys [db]
        {:keys [path-params]} :request
        :as ctx}]
    (assoc ctx :response
           {:status 200
            :body (->> path-params
                       :id
                       UUID/fromString
                       (c/entity db)
                       (touch db))})))
