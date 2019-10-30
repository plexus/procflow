(ns procflow.db
  (:require [crux.api :as crux]))

(defn touch
  "Recursively expand an entity map by looking up component references and
  replacing them with entity maps."
  [component-keys db entity]
  (let [touch (partial touch component-keys db)]
    (into
     {}
     (map (fn [[k v]]
            [k
             (if (some #{k} component-keys)
               (if (vector? v)
                 (mapv (comp touch (partial crux/entity db)) v)
                 (touch (crux/entity db v)))
               v)]))
     entity)))

(defn entity
  "Like [[crux.api/entity]], but supports lookup refs."
  [db eid]
  (if (vector? eid)
    (crux/entity db (ffirst (crux/q db {:find ['id] :where [(into ['id] eid)]})))
    (crux/entity db eid)))

(defn q
  "Like [[crux.api/q]], but supports variable bindings with :in, and lookup refs."
  [db query & args]
  (let [vars  (zipmap (:in query) args)
        lrefs (filter (comp vector? val) vars)
        where (:where query)
        [vars where] (reduce
                      (fn [[vars where] [var [k v]]]
                        (let [sym (gensym "?lref")]
                          [(assoc vars var sym)
                           (conj where [sym k v])]))
                      [vars where]
                      lrefs)
        query (-> query
                  (dissoc :in)
                  (assoc :where (vec (for [c where]
                                       (vec (for [x c]
                                              (vars x x)))))))]
    (crux/q db query)))
