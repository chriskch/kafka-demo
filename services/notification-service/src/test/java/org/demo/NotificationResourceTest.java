package org.demo;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class NotificationResourceTest {

    @Inject
    OrderNotificationConsumer consumer;

    @Test
    void returnsProcessedNotifications() {
        consumer.onMessage(new OrderMessage(
                "order-demo",
                "demo@example.com",
                List.of("Cold Brew Kit"),
                new BigDecimal("29.95"),
                Instant.now()
        ));

        given()
                .when().get("/notifications")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].orderId", is("order-demo"))
                .body("[0].customerEmail", is("demo@example.com"))
                .body("[0].items[0]", containsString("Cold Brew"));
    }
}
