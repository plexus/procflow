(ns crux
  (:require [crux.api :as crux]
            [procflow.data :as data]
            [procflow.db :as db]
            [clojure.set :as set]))

#_(user/go)

(defn node []
  (:procflow.system/crux (user/system)))

(defn db []
  (crux/db (node)))

(defn map-crux-id [m]
  (set/rename-keys m {:procflow/id :crux.db/id}))

#_
(def tx-res
  (crux/submit-tx crux-node (vec (for [m (data/gen-data {:procedure [[3]]})]
                                   [:crux.tx/put (map-crux-id m)]))))

(def touch (partial db/touch #{:procflow.procedure/steps
                               :procflow.procedure/owner}))

(let [component-keys #{:procflow.procedure/steps
                       :procflow.procedure/owner}
      db     (db)
      entity (partial crux/entity db)
      touch  (partial touch db)]
  (map (comp touch entity first)
       (crux/q db
               '{:find  [p1]
                 :where [[p1 :procflow.procedure/title t]]})))

(let [db (db)]
  (->> #uuid "ce20cc92-a55a-4bf7-9282-4df9f4cbb9e2"
       (crux/entity db)
       (db/touch #{:procflow.procedure/steps} db)))
;; => {:procflow.procedure/steps
;;     [{:procflow.step/type :text,
;;       :crux.db/id #uuid "f45d3c5d-a911-475f-8271-bd8078d131ec"}],
;;     :crux.db/id #uuid "ce20cc92-a55a-4bf7-9282-4df9f4cbb9e2"}

(crux/q (crux/db crux-node)
        '{:find [p1 t]
          :where [[p1 :procflow.step/type t]
                  ]})
;; => #{[#uuid "7065a98b-3d47-49cc-bca2-ed1ff5cbccfe" :text]
;;      [#uuid "ab8b8f10-a826-41f2-b5bd-9f972b148977" :text]
;;      [#uuid "4febb87c-ed2c-4ed9-8613-5a05db5483a3" :text]}
