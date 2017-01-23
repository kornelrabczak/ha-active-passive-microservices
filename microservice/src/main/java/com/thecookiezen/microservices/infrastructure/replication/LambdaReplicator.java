package com.thecookiezen.microservices.infrastructure.replication;

import akka.Done;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.kafka.ConsumerSettings;
import akka.kafka.ProducerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Consumer;
import akka.kafka.javadsl.Producer;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import com.thecookiezen.microservices.infrastructure.status.SystemProperty;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.log4j.Logger;
import pl.setblack.airomem.core.kryo.KryoSerializer;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

@Singleton
@Startup
public class LambdaReplicator {

    private static final Logger log = Logger.getLogger(LambdaReplicator.class);

    private ActorSystem system = ActorSystem.create("reactive-kafka");

    private ActorRef publisher;

    private KryoSerializer serializer = new KryoSerializer();

    @Inject
    @SystemProperty("kafkaTopic")
    private String topic;

    @Inject
    @SystemProperty("kafkaServers")
    private String kafkaServers;

    @PostConstruct
    public void init() {
        ActorMaterializer materializer = ActorMaterializer.create(system);

        ProducerSettings<byte[], String> producerSettings = ProducerSettings
                .create(system, new ByteArraySerializer(), new StringSerializer())
                .withBootstrapServers(kafkaServers);

        publisher = Source.actorPublisher(KafkaPublisher.props())
                .map(Object::toString).map(elem -> new ProducerRecord<byte[], String>(topic, elem))
                .to(Producer.plainSink(producerSettings))
                .run(materializer);

        ConsumerSettings consumerSettings = ConsumerSettings
                .create(system, new ByteArrayDeserializer(), new StringDeserializer())
                .withBootstrapServers(kafkaServers)
                .withGroupId("group1")
                .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Consumer.plainSource(consumerSettings, Subscriptions.assignmentWithOffset(new TopicPartition(topic, 0), 0))
                .mapAsync(1, record -> {
                    log.info(record);
                    return CompletableFuture.completedFuture(Done.getInstance());
                })
                .runWith(Sink.ignore(), materializer);

    }

    public void replicate(String msg) {
        publisher.tell(msg, ActorRef.noSender());
    }
}
