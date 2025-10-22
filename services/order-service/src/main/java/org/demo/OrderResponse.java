package org.demo;

/**
 * REST response sent back to callers after an order has been queued.
 */
public record OrderResponse(String orderId, String status) {
}
