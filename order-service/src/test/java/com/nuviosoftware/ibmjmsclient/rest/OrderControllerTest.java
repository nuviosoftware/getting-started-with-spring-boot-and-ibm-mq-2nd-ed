package com.nuviosoftware.ibmjmsclient.rest;

import com.nuviosoftware.ibmjmsclient.MQTestContainer;
import com.nuviosoftware.ibmjmsclient.model.OrderRequest;
import jakarta.jms.JMSException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class OrderControllerTest {

    @Container
    public static GenericContainer<?> mqContainer = MQTestContainer.setupMqContainer();

    // Ensure the container is started early (before DynamicPropertySource and Spring context)
    static {
        mqContainer.start();
        // Also set as a system property so Spring picks it up very early in the bootstrap
        String host = mqContainer.getHost();
        Integer port = mqContainer.getMappedPort(1414);
        System.setProperty("ibm.mq.connName", host + "(" + port + ")");
    }

    @Autowired
    private OrderController orderController;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Test
    public void createOrder() throws JMSException {
        // Given
        OrderRequest orderRequest = new OrderRequest("TEST123", "123456789");

        // When
        orderController.createOrder(orderRequest);

        // Then
        String response = (String) jmsTemplate.receiveAndConvert("ORDER.REQUEST");
        assertEquals(orderRequest.message(), response);
    }
}
