(ns procflow.ui-macros
  (:require [procflow.system :as system]))

(defmacro datascript-schema []
  (let [entities (:entities (:procflow/schema (system/config :prod)))]
    (into {}
          (for [[ent {:keys [rels]}] entities
                [attr dest] rels
                :let [coll? (vector? dest)
                      dest (cond-> dest coll? first)]]
            [attr (cond-> {:db/valueType :db.type/ref}
                    coll? (assoc :db/cardinality :db.cardinality/many))]))))

(comment
  (datascript-schema)

  )
