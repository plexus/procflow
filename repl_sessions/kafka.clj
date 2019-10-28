(ns kafka
  (:import (org.apache.kafka.common Cluster)
           (org.apache.kafka.clients.consumer Consumer
                                              ConsumerConfig
                                              KafkaConsumer
                                              ConsumerRecords)
           (org.apache.kafka.clients.producer KafkaProducer
                                              Producer
                                              ProducerConfig
                                              Partitioner
                                              ProducerRecord
                                              RecordMetadata)
           (org.apache.kafka.common.serialization Serializer
                                                  LongSerializer
                                                  StringSerializer
                                                  LongDeserializer
                                                  StringDeserializer)))

(def producer-config
  {ProducerConfig/BOOTSTRAP_SERVERS_CONFIG      "localhost:9092"
   ProducerConfig/CLIENT_ID_CONFIG              "client1"
   ProducerConfig/KEY_SERIALIZER_CLASS_CONFIG   (.getName LongSerializer)
   ProducerConfig/VALUE_SERIALIZER_CLASS_CONFIG (.getName StringSerializer)
   ;;ProducerConfig/PARTITIONER_CLASS_CONFIG      (.getName CustomPartitioner)
   })

producer-config
;; => {"bootstrap.servers" "localhost:9092",
;;     "client.id" "client1",
;;     "key.serializer" "org.apache.kafka.common.serialization.LongSerializer",
;;     "value.serializer" "org.apache.kafka.common.serialization.StringSerializer"}

(def producer
  (KafkaProducer. producer-config))

(deftype MySerializer []
  Serializer
  (configure [this configs key?])
  (^bytes serialize [this ^String topic object])
  (close [this]))

(deftype MyPartioner []
  Partitioner
  (configure [this configs])
  (^int partition [this ^String topic
                   ^Object key ^bytes keyBytes
                   ^Object value ^bytes valueBytes ^Cluster cluster])
  (close [this]))

(def consumer-config
  {ConsumerConfig/BOOTSTRAP_SERVERS_CONFIG        "localhost:9092"
   ConsumerConfig/GROUP_ID_CONFIG                 "consumer-group-2"
   ;; ConsumerConfig/KEY_DESERIALIZER_CLASS_CONFIG   (.getName LongDeserializer)
   ;; ConsumerConfig/VALUE_DESERIALIZER_CLASS_CONFIG (.getName StringDeserializer)
   ConsumerConfig/MAX_POLL_RECORDS_CONFIG         (int 5)
   ConsumerConfig/ENABLE_AUTO_COMMIT_CONFIG       "false"
   ConsumerConfig/AUTO_OFFSET_RESET_CONFIG        "earliest" ;; "latest"
   })



consumer-config
;; => {"bootstrap.servers" "localhost:9092",
;;     "group.id" "consumer-group-1",
;;     "key.deserializer" "org.apache.kafka.common.serialization.LongDeserializer",
;;     "value.deserializer"
;;     "org.apache.kafka.common.serialization.StringDeserializer",
;;     "max.poll.records" 1,
;;     "enable.auto.commit" "false",
;;     "auto.offset.reset" "earliest"}

(def consumer
  (KafkaConsumer. consumer-config
                  (LongDeserializer.)
                  (StringDeserializer.)))

(.subscribe consumer ["topic-1"])

(def res
  (let [record (ProducerRecord. "topic-1" "This is record 3")]
    (.send producer record)))

(def records
  (.poll consumer 1000))

(.isEmpty records)
(seq records)
;; => #object[org.apache.kafka.clients.consumer.ConsumerRecords 0x5f8f94d8 "org.apache.kafka.clients.consumer.ConsumerRecords@5f8f94d8"]

(defn record->map [r]
  {:key (.key r)
   :value (.value r)
   :offset (.offset r)
   :partition (.partition r)
   :timestamp (.timestamp r)
   :topic (.topic r)
   :headers (into {} (map (juxt #(.key %) #(.value %))) (.headers r))}

  ;; (.timestampType r)
  ;; (.serializedKeySize r)
  ;; (.serializedValueSize r)
  )

(map record->map (.poll consumer 1000))
