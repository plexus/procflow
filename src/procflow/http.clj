(ns procflow.http
  (:require [reitit.ring :as ring]
            [reitit.http :as http]
            [reitit.interceptor.sieppari :as sieppari]
            [muuntaja.core]
            [muuntaja.interceptor :as muuntaja]
            [procflow.interceptor :as interceptor :refer [definterceptor]]
            [crux.api :as crux]))

(require 'procflow.routes.api :reload)

(defn inject-db-interceptor [crux-node]
  {:name  ::inject-db
   :enter (fn [ctx]
            (assoc ctx
              :procflow.system/crux crux-node
              :procflow.system/db (crux/db crux-node)))})

(defn format-negotiation-interceptor []
  (muuntaja/format-interceptor (assoc muuntaja.core/default-options :return :bytes)))

(defn router [opts]
  (http/router
   (:routes opts)
   {:reitit.interceptor/registry @interceptor/registry}))

(defn handler [opts]
  (http/ring-handler (router opts)
                     (ring/create-default-handler)
                     {:executor     sieppari/executor
                      :interceptors [(inject-db-interceptor (:crux opts))
                                     (format-negotiation-interceptor)]}))
