package org.demo;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Message published to Kafka describing an order that needs downstream processing.
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
