package org.demo;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

/**
 * JSON deserializer for the {@link OrderMessage} payload consumed from Kafka.
 */
public class OrderMessageDeserializer extends ObjectMapperDeserializer<OrderMessage> {

    public OrderMessageDeserializer() {
        super(OrderMessage.class);
    }
}
