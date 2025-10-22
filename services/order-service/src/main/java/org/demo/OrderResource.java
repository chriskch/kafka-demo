package org.demo;

import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * REST endpoint used to enqueue new orders onto Kafka.
 */
@Path("/orders")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OrderResource {

    private static final Logger LOG = Logger.getLogger(OrderResource.class);

    private final OrderProducer producer;

    @Inject
    public OrderResource(OrderProducer producer) {
        this.producer = producer;
    }

    @POST
    public CompletionStage<Response> createOrder(OrderRequest request) {
        validate(request);

        String orderId = UUID.randomUUID().toString();
        OrderMessage message = new OrderMessage(
                orderId,
                request.customerEmail(),
                request.items(),
                request.totalAmount(),
                Instant.now()
        );

        LOG.infov("Received order {0} for {1}, queuing for processing", orderId, request.customerEmail());

        return producer.publish(message)
                .thenApply(ignored -> Response
                        .accepted(new OrderResponse(orderId, "Order queued for processing"))
                        .build())
                .exceptionally(throwable -> {
                    LOG.error("Failed to publish order message", throwable);
                    throw new WebApplicationException("Failed to publish order message", throwable);
                });
    }

    private void validate(OrderRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }

        if (request.customerEmail() == null || request.customerEmail().isBlank()) {
            throw new BadRequestException("customerEmail is required");
        }

        BigDecimal total = request.totalAmount();
        if (Objects.isNull(total) || total.signum() <= 0) {
            throw new BadRequestException("totalAmount must be greater than zero");
        }
    }
}
