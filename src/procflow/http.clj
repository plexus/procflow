(ns procflow.http
  (:require [crux.api :as crux]
            [integrant.core :as ig]
            [muuntaja.core]
            [muuntaja.interceptor :as muuntaja]
            [pohjavirta.request :as request]
            [pohjavirta.response :as response]
            [pohjavirta.server :as pohjavirta]
            [procflow.interceptor :as interceptor]
            [procflow.http.websocket :as websocket]
            [reitit.http :as http]
            [reitit.ring :as ring]
            [reitit.core :as reitit]
            [reitit.interceptor.sieppari :as sieppari]
            [clojure.tools.logging :as log])
  (:import (io.undertow.server HttpHandler)
           (io.undertow.websockets WebSocketConnectionCallback WebSocketProtocolHandshakeHandler)
           (io.undertow.websockets.core WebSockets
                                        WebSocketChannel
                                        AbstractReceiveListener
                                        BufferedTextMessage
                                        StreamSourceFrameChannel
                                        CloseMessage)
           (io.undertow.websockets.spi WebSocketHttpExchange)))

(require 'procflow.routes.api :reload)

(defn inject-db-interceptor [crux-node]
  {:name  ::inject-db
   :enter (fn [ctx]
            (assoc ctx
              :procflow.system/crux crux-node
              :procflow.system/db (crux/db crux-node)))})

(defn format-negotiation-interceptor []
  (muuntaja/format-interceptor (assoc muuntaja.core/default-options :return :bytes)))

(defn router [{:keys [routes]}]
  (http/router
   routes
   {:reitit.interceptor/registry @interceptor/registry}))

(defn ring-handler [router crux-node]
  (http/ring-handler router
                     (ring/create-default-handler)
                     {:executor     sieppari/executor
                      :interceptors [(inject-db-interceptor crux-node)
                                     (format-negotiation-interceptor)]}))


(defmethod ig/init-key ::router [_ config]
  (router config))

(defmethod ig/init-key ::server [_ {:keys [router crux port]}]
  (doto (-> (websocket/undertow-handler router (ring-handler router crux))
            (pohjavirta/create {:port port}))
    (pohjavirta/start)))

(defmethod ig/halt-key! ::server [_ http]
  (pohjavirta/stop http))

(def conns (atom []))

(defn on-receive [{:keys [data channel]}]
  (log/info "Got data" data (websocket/uid channel))
  (websocket/send! channel (str (websocket/uid channel))))

(defn on-connect [{:as opts}]
  (log/info "Connect" opts)
  (swap! conns conj (:channel opts)))

(defn on-error [{:as opts}]
  (log/info "Error" opts))

(defn on-close [{:as opts}]
  (log/info "Close" opts))
