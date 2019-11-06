(ns procflow.schema
  (:require [integrant.core :as ig]
            [malli.core :as m]
            [malli.generator :as mg]))

(defn ents->malli [entities]
  (into {}
        (for [[ent {:keys [attrs rels]}] entities]
          [ent (-> [:map
                    [:procflow/id 'uuid?]]
                   (into attrs)
                   (into (for [[attr dest] rels]
                           [attr (if (vector? dest)
                                   [:vector 'uuid?]
                                   'uuid?)])))])))

(defmethod ig/init-key :procflow/schema [_ config]
  (let [entities (:entities config)
        registry (merge m/default-registry (ents->malli entities))]
    (assoc config
           :registry registry
           :validate (fn [schema value]
                       (m/validate schema value {:registry registry}))
           :generate (fn [schema]
                       (mg/generate schema {:registry registry})))))

(comment
  (ents->malli (:entities (:procflow/schema (user/config))))

  (:procflow/schema (user/system)))
