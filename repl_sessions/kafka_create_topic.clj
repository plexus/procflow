(ns kafka-create-topic
  (:import (org.apache.kafka.clients.admin AdminClient AdminClientConfig NewTopic)
           org.slf4j.LoggerFactory))

(def admin (AdminClient/create {AdminClientConfig/BOOTSTRAP_SERVERS_CONFIG "localhost:9092"}))

(def res
  (let [partitions 1
        replicas 1]
    (.createTopics admin [(NewTopic. "topic-1" partitions replicas)])))

(.values res)
