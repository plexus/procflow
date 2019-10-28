(ns crux
  (:require [crux.api :as crux]
            [procflow.data :as data]
            [clojure.set :as set]))

(def crux-node
  (crux/start-node {:crux.node/topology :crux.kafka/topology
                    :crux.node/kv-store 'crux.kv.rocksdb/kv
                    :crux.kv/db-dir "data/db-dir-1"
                    :crux.kafka/bootstrap-servers "localhost:9092"}))

(defn map-crux-id [m] (set/rename-keys m {:procflow/id :crux.db/id}))



(def tx-res
  (crux/submit-tx crux-node (vec (for [m (data/gen-data {:procedure [[3]]})]
                                   [:crux.tx/put (map-crux-id m)]))))

(crux/q (crux/db crux-node)
        '{:find [p1 t]
          :where [[p1 :procflow.procedure/title t]
                  ]})
;; => #{[#uuid "39770fa5-9420-4cee-9301-11034597e7fc" "8531AD5b1PbLHl1k1G93"]
;;      [#uuid "156bafeb-f56e-4b2f-845a-1f80d38df04b" "1ib01Pbl6E10DlTZyfG9"]
;;      [#uuid "b90ea39a-d6f8-43d0-804c-ec10e43a690a" "dF1J"]}


(crux/q (crux/db crux-node)
        '{:find [p1 t]
          :where [[p1 :procflow.step/type t]
                  ]})
;; => #{[#uuid "7065a98b-3d47-49cc-bca2-ed1ff5cbccfe" :text]
;;      [#uuid "ab8b8f10-a826-41f2-b5bd-9f972b148977" :text]
;;      [#uuid "4febb87c-ed2c-4ed9-8613-5a05db5483a3" :text]}
