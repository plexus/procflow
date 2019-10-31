(ns procflow.http.websocket
  (:require [reitit.core :as reitit]
            [pohjavirta.response :as response]
            [pohjavirta.request :as request])
  (:import (io.undertow.server HttpHandler)
           (io.undertow.websockets WebSocketConnectionCallback WebSocketProtocolHandshakeHandler)
           (io.undertow.websockets.core WebSockets
                                        WebSocketChannel
                                        AbstractReceiveListener
                                        BufferedTextMessage
                                        StreamSourceFrameChannel
                                        CloseMessage)
           (io.undertow.websockets.spi WebSocketHttpExchange)
           (java.util UUID)))

(defprotocol WSChannel
  (send! [this message])
  (close! [this])
  (uid [this]))

(extend-protocol WSChannel
  WebSocketChannel
  (send! [this message]
    (WebSockets/sendText message this nil))
  (close! [this]
    (WebSockets/sendClose CloseMessage/NORMAL_CLOSURE this nil))
  (uid [this]
    (.getAttribute this (str `uid))))

(defn handle-websocket-request [exchange callbacks]
  (let [callback (fn [k m]
                   (when-let [cb (get callbacks k)]
                     (if (qualified-symbol? cb)
                       ((requiring-resolve cb) m)
                       (cb m))))
        listener (proxy [AbstractReceiveListener] []
                   (onFullTextMessage [^WebSocketChannel channel ^BufferedTextMessage message]
                     (let [data (.getData message)]
                       (callback :on-receive {:data data :channel channel})))
                   (onClose [^WebSocketChannel ws-channel ^StreamSourceFrameChannel ssf-channel]
                     (callback :on-close {:channel ws-channel}))
                   (onCloseMessage [^CloseMessage cm ^WebSocketChannel channel]
                     (callback :on-close {:channel channel
                                          :reason (.getReason cm)}))
                   (onError [^WebSocketChannel channel ^Throwable error]
                     (callback :on-error {:channel channel
                                          :error error})))
        ws-hs-handler (WebSocketProtocolHandshakeHandler.
                       (reify WebSocketConnectionCallback
                         (^void onConnect [this ^WebSocketHttpExchange exchange ^WebSocketChannel channel]
                          (.setAttribute channel (str `uid) (UUID/randomUUID))
                          (.set (.getReceiveSetter channel) listener)
                          (.resumeReceives channel)
                          (callback :on-connect {:channel channel}))))]
    (.handleRequest ws-hs-handler exchange)))

(defn undertow-handler [router ring-handler]
  (reify HttpHandler
    (handleRequest [_ exchange]
      (let [uri   (.getRequestPath exchange)
            match (reitit/match-by-path router uri)]
        (if-let [callbacks (:websocket (:data match))]
          (handle-websocket-request exchange callbacks)
          (let [request  (request/create exchange)
                response (ring-handler request)]
            (response/send-response response exchange)))))))
