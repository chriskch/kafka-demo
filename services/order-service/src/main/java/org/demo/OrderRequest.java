package org.demo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Incoming payload representing a customer order captured via the REST API.
 */
public record OrderRequest(String customerEmail, List<String> items, BigDecimal totalAmount) {

    public OrderRequest {
        items = items == null ? List.of() : List.copyOf(items);
    }
}
