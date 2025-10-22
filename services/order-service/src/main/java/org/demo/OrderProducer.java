package org.demo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import java.util.concurrent.CompletionStage;

/**
 * Publishes order messages to Kafka using the configured reactive messaging channel.
 */
@ApplicationScoped
public class OrderProducer {

    private static final Logger LOG = Logger.getLogger(OrderProducer.class);

    @Inject
    @Channel("orders-out")
    Emitter<OrderMessage> emitter;

    public CompletionStage<Void> publish(OrderMessage order) {
        LOG.infov("Publishing order {0} for {1}", order.orderId(), order.customerEmail());
        return emitter.send(order);
    }
}
