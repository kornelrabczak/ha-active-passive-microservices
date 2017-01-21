
## starting zookeeper

bin/zookeeper-server-start.sh config/zookeeper.properties

## starting kafka

bin/kafka-server-start.sh config/server.properties

## creating topic

bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic topic1

## consume kafka topic

bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic topic1 --from-beginning

## starting single node

java -DzookeeperConnection=127.0.0.1:2181 -DlatchPath=/example/leader -DkafkaTopic=topic1 -DkafkaServers=localhost:9092 -DnodeId=node-1 -Dport=8080 -jar payara-micro/target/payara-micro-1.0-SNAPSHOT.jar