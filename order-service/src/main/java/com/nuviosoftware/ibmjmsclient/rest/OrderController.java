package com.nuviosoftware.ibmjmsclient.rest;

import com.ibm.mq.jakarta.jms.MQQueue;
import com.nuviosoftware.ibmjmsclient.model.OrderRequest;
import jakarta.jms.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import jakarta.jms.JMSException;
import jakarta.jms.TextMessage;
import java.nio.charset.StandardCharsets;

@RequestMapping("orders")
@RestController
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    // Synchronous example
    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody OrderRequest order) throws JMSException {
        logger.info("### 1 ### Order Service sending order message '{}' to the queue", order.message());

        Message response = jmsTemplate.sendAndReceive("ORDER.REQUEST", session -> {
            TextMessage textMessage = session.createTextMessage(order.message());
            textMessage.setJMSCorrelationID(order.identifier());
            // Note: IBM MQ creates a dynamic name for this, like 'AMQ.65A2...'
            textMessage.setJMSReplyTo(session.createTemporaryQueue());
            return textMessage;
        });
        // Extract the body and ID for logging and returning
        if (response instanceof TextMessage textMessage) {
            String responseBody = textMessage.getText();
            String correlationId = textMessage.getJMSCorrelationID();

            logger.info("### 4 ### Received response: {} with correlation id: {}",
                    responseBody, correlationId);

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        }

        return new ResponseEntity<>("No response received", HttpStatus.REQUEST_TIMEOUT);
    }


    @Deprecated // this was just to show how to find a message by correlation Id
    @GetMapping
    public ResponseEntity<OrderRequest> findOrderByCorrelationId(@RequestParam String correlationId) throws JMSException {
        logger.info("Looking for message '{}'", correlationId);
        String convertedId = bytesToHex(correlationId.getBytes());
        final String selectorExpression = String.format("JMSCorrelationID='ID:%s'", convertedId);
        final TextMessage responseMessage = (TextMessage) jmsTemplate.receiveSelected("ORDER.REQUEST", selectorExpression);
        OrderRequest response = new OrderRequest(responseMessage.getText(), correlationId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // You could use Apache Commons Codec library instead
    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes();
    public static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }
}
