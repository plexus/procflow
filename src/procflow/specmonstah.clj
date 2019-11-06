(ns procflow.specmonstah
  (:require [malli.core :as m]
            [procflow.specmonstah-malli :as sm]))

(defn ents->specmonstah [entities]
  (into {}
        (for [[ent {:keys [rels]}] entities]
          [ent (cond-> {:prefix (keyword (name ent))
                        :schema ent}
                 (seq rels)
                 (assoc :relations
                        (into {} (for [[attr dest] rels
                                       :let [dest (cond-> dest (vector? dest) first)]]
                                   [attr [dest :procflow/id]])))
                 (seq rels)
                 (assoc :constraints
                        (into {} (for [[attr dest] rels
                                       :let [coll? (vector? dest)]]
                                   [attr (if coll? #{:uniq :coll} #{})]))))])))

(comment
  (set! *print-namespace-maps* false)
  (def schema (ents->specmonstah (:entities (:procflow/schema (user/system)))))
  (def malli-opts {:registry (:registry (:procflow/schema (user/system)))})

  (let [query {:procflow/procedure [[3 {:refs {:procflow.procedure/steps ["x"]}}]]
               :procflow/step [[10]]}]
    (sm/ent-db-spec-gen-attr {:schema schema} query malli-opts))

  )

;; (def schema (malli->specmonstah (:entities schema)))


#_(defn gen-data [opts]
    (-> (sg/ent-db-spec-gen {:schema schema} opts)
        (sm/attr-map :spec-gen)
        vals))
