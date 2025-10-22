package org.demo;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class OrderResourceTest {

    @InjectMock
    OrderProducer producer;

    @BeforeEach
    void stubProducer() {
        Mockito.when(producer.publish(Mockito.any()))
                .thenReturn(CompletableFuture.completedFuture(null));
    }

    @Test
    void postOrderPublishesMessageToKafkaChannel() {
        ArgumentCaptor<OrderMessage> captor = ArgumentCaptor.forClass(OrderMessage.class);

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

        Mockito.verify(producer).publish(captor.capture());
        OrderMessage message = captor.getValue();

        assertNotNull(message);
        assertNotNull(message.orderId());
        assertEquals("customer@example.com", message.customerEmail());
        assertEquals(0, message.totalAmount().compareTo(new BigDecimal("42.50")));
        assertEquals(List.of("Coffee Beans", "Chemex"), message.items());
        assertNotNull(message.createdAt());
    }
}
