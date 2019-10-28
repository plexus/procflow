(ns procflow.data
  (:require [clojure.spec-alpha2 :as s]
            [clojure.test.check.generators :as gen]
            [reifyhealth.specmonstah.core :as sm]
            [procflow.specmonstah-gen-spec :as sg]
            ))

(s/def :procflow/id uuid?)

(s/def :procflow/procedure (s/keys :req [:procflow/id
                                         :procflow.procedure/owner
                                         :procflow.procedure/title
                                         :procflow.procedure/steps]))

(s/def :procflow.procedure/owner :procflow/id)
(s/def :procflow.procedure/title string?)
(s/def :procflow.procedure/steps (s/coll-of :procflow/id))

(s/def :procflow/step (s/keys :req [:procflow/id
                                    :procflow.step/type]))

(s/def :procflow.step/type #{:text})

(s/def :procflow/user (s/keys :req [:procflow/id
                                    :procflow.user/identity]))
(s/def :procflow.user/identity (s/and string?
                                      #(< 2 (count %))))

(gen/sample (s/gen :procflow/procedure))
(gen/sample (s/gen :procflow/user))

(def schema
  {:user {:prefix :u
          :spec   :procflow/user
          #_#_:constraints {:procflow.user/identity #{:uniq}}}
   :procedure {:prefix    :p
               :spec      :procflow/procedure
               :relations {:procflow.procedure/owner [:user :procflow/id]
                           :procflow.procedure/steps [:step :procflow/id]}
               :constraints {:procflow.procedure/steps #{:coll :uniq}} }
   :step {:prefix :s
          :spec :procflow/step
          }
   })
(-> (sm/add-ents {:schema schema} {:procedure [[10]]})
    (sm/visit-ents :prn (fn [db {:keys [ent-name ent-type] :as ent}]
                          (prn [ent-name ent-type (dissoc ent :ent-name :ent-type)]))))

;;(s/resolve-spec (s/form ::a))
;;(s/spec int?)
(defn gen-data [opts]
  (-> (sg/ent-db-spec-gen {:schema schema} opts)
      (sm/attr-map :spec-gen)
      ;; :data
      ;; :attrs
      ;; vals
      ;; (keep :spec-gen)
      vals
      ))

(gen-data {:procedure [[3]]
           :step [[10]]})
