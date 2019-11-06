(ns repl-sessions.malli
  (:require [malli.core :as m]
            [malli.generator :as mg]
            [procflow.base58-prefix :as base58-prefix]))

(let [{:keys [entities validate generate registry]} (:procflow/schema (user/system))]
  (def entities entities)
  (def validate validate)
  (def generate generate)
  (def registry registry))

(generate :procflow/procedure)
(generate :procflow/step)
(generate :procflow/user)

(def ref-schema
  (reify m/IntoSchema
    (-into-schema [_ properties [entity-type] opts]
      (let [validator uuid?]
        (reify m/Schema
          (-name [_] :procflow/ref)
          (-validator [_] validator)
          (-explainer [this path]
            (fn [value in acc]
              (if-not (validator value) (conj acc (m/error path in this value)) acc)))
          (-accept [this visitor opts] (visitor this [] opts))
          (-properties [_] properties)
          (-form [_] [:procflow/ref entity-type]))))))

;; base58-prefix
(require '[malli.core :as m]
         '[malli.generator :as mg]
         '[clj-uuid]
         '[clj-uuid.bitmop]
         '[alphabase.base58 :as base58]
         '[clojure.string :as str]
         '[clojure.test.check.generators :as gen])
(import 'java.util.UUID)

(defn uuid->base58 [u]
  (-> (cond-> u
        (string? u)
        UUID/fromString)
      clj-uuid/to-byte-array
      base58/encode))

(defn base58->uuid [s]
  (let [b (base58/decode s)
        l1 (clj-uuid.bitmop/bytes->long b 0)
        l2 (clj-uuid.bitmop/bytes->long b 8)]
    (UUID. l1 l2)))

(defn base58-prefix-schema []
  (reify m/IntoSchema
    (-into-schema [_ properties [prefix] opts]
      (let [validator (fn [s]
                        (try
                          (boolean
                           (and (string? s)
                                (str/starts-with? s (str prefix "-"))
                                (base58->uuid (subs s (inc (count prefix))))))
                          (catch Exception _
                            false)))]
        (reify m/Schema
          (-name [_] :base58-prefix)
          (-validator [_] validator)
          (-explainer [this path]
            (fn [value in acc]
              (if-not (validator value) (conj acc (m/error path in this value)) acc)))
          (-accept [this visitor opts] (visitor this [] opts))
          (-properties [_] properties)
          (-form [_] [:base58-prefix prefix]))))))

(defmethod mg/-generator :base58-prefix [schema _]
  (gen/fmap (fn [u]
              (str (second (m/-form schema))
                   "-"
                   (uuid->base58 u)))
            gen/uuid))
