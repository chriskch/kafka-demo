package org.demo;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

/**
 * Exposes notification history so the demo UI can display processed orders.
 */
@Path("/notifications")
public class NotificationResource {

    private final OrderNotificationConsumer consumer;

    @Inject
    public NotificationResource(OrderNotificationConsumer consumer) {
        this.consumer = consumer;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<OrderMessage> listProcessedOrders() {
        return consumer.processedOrders();
    }
}
