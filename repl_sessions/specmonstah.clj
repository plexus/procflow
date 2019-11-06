(ns repl-sessions.specmonstah)

;;; convert Malli schema to specmontah schema
;;; {:user [:map [:id uuid?] [:profile [:procflow/ref :profile]]]}

(defn ref? [schema]
  (if (vector? schema)
    (or (= :procflow/ref (first schema))
        (and (= :vector (first schema))
             (= :procflow/ref (first (second schema)))))
    false))

(defn coll-schema? [schema]
  (if (vector? schema)
    (= :vector (first schema))
    false))

(defn flatten-schema [entities]
  (for [[ent s] entities
        [attr val] (next s)]
    [ent attr val]))

(defn reference [schema]
  (if (coll-schema? schema)
    (second (second schema))
    (second schema)))

(defn malli->specmonstah [entities]
  (reduce
   (fn [acc [ent attr schema]]
     (cond-> acc
       :->           (assoc-in [ent :prefix] (keyword (name ent)))
       :->           (assoc-in [ent :schema] ent)
       (ref? schema) (assoc-in [ent :relations attr] [(reference schema) :procflow/id])
       (ref? schema) (assoc-in [ent :constraints attr] (if (coll-schema? schema)
                                                         #{:coll :uniq}
                                                         #{}))))
   {}
   (flatten-schema entities)))

(malli->specmonstah {:user [:map
                            [:id uuid?]
                            [:profile [:procflow/ref :profile]]]})
;; => {:user
;;     {:prefix :user,
;;      :schema :user,
;;      :relations {:profile [:profile :procflow/id]},
;;      :constraints {:profile #{}}}}
