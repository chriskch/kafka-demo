package org.demo;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.providers.connectors.InMemoryConnector;
import io.smallrye.reactive.messaging.providers.connectors.InMemorySink;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class OrderResourceTest {

    private static InMemoryConnector connector;

    @BeforeAll
    static void init() {
        connector = InMemoryConnector.switchOutgoingChannelsToInMemory("orders-out");
    }

    @AfterAll
    static void tearDown() {
        InMemoryConnector.clear();
    }

    @Test
    void postOrderPublishesMessageToKafkaChannel() {
        InMemorySink<OrderMessage> sink = connector.sink("orders-out");
        sink.clear();

        given()
                .contentType("application/json")
                .body(Map.of(
                        "customerEmail", "customer@example.com",
                        "items", List.of("Coffee Beans", "Chemex"),
                        "totalAmount", new BigDecimal("42.50")
                ))
                .when()
                .post("/orders")
                .then()
                .statusCode(202)
                .body("orderId", notNullValue())
                .body("status", equalTo("Order queued for processing"));

        List<Message<OrderMessage>> received = sink.received();
        assertEquals(1, received.size(), "Expected a single order message");

        OrderMessage payload = received.get(0).getPayload();
        assertNotNull(payload);
        assertNotNull(payload.orderId());
        assertEquals("customer@example.com", payload.customerEmail());
        assertEquals(0, payload.totalAmount().compareTo(new BigDecimal("42.50")));
        assertEquals(List.of("Coffee Beans", "Chemex"), payload.items());
        assertNotNull(payload.createdAt());
    }
}
