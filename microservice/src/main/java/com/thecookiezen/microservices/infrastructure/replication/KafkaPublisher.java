package com.thecookiezen.microservices.infrastructure.replication;

import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import akka.stream.actor.AbstractActorPublisher;

public class KafkaPublisher extends AbstractActorPublisher<String> {

    public static Props props() { return Props.create(KafkaPublisher.class); }

    public KafkaPublisher() {
        receive(ReceiveBuilder.
                matchEquals("START", val -> {
                    onNext("data: {\"Hello\": \"World!\"}\n\n");
                }).
                match(String.class, this::onNext).
                build());
    }
}
