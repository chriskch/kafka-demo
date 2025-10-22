package org.demo;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Representation of an order event arriving from Kafka.
 */
public record OrderMessage(String orderId,
                           String customerEmail,
                           List<String> items,
                           BigDecimal totalAmount,
                           Instant createdAt) {

    public OrderMessage {
        items = items == null ? List.of() : List.copyOf(items);
    }
}
