package org.demo;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Consumes order events from Kafka and simulates sending a notification.
 */
@ApplicationScoped
public class OrderNotificationConsumer {

    private static final Logger LOG = Logger.getLogger(OrderNotificationConsumer.class);

    private final List<OrderMessage> processedOrders = new CopyOnWriteArrayList<>();

    @Incoming("orders-in")
    public void onMessage(OrderMessage order) {
        processedOrders.add(order);
        LOG.infov("Notification sent for order {0} to {1} (total {2})", order.orderId(), order.customerEmail(), order.totalAmount());
    }

    /**
     * Provides test visibility into processed orders without coupling to Kafka.
     */
    List<OrderMessage> processedOrders() {
        return List.copyOf(processedOrders);
    }
}
