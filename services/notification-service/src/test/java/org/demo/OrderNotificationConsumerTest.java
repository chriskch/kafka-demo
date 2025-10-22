package org.demo;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class OrderNotificationConsumerTest {

    @Inject
    OrderNotificationConsumer consumer;

    @Test
    void processesIncomingOrders() {
        OrderMessage message = new OrderMessage(
                "order-123",
                "customer@example.com",
                List.of("Espresso Machine"),
                new BigDecimal("499.99"),
                Instant.now());

        consumer.onMessage(message);

        assertEquals(1, consumer.processedOrders().size());
        OrderMessage processed = consumer.processedOrders().get(0);
        assertEquals("order-123", processed.orderId());
        assertEquals("customer@example.com", processed.customerEmail());
        assertEquals(new BigDecimal("499.99"), processed.totalAmount());
        assertNotNull(processed.createdAt());
    }
}
