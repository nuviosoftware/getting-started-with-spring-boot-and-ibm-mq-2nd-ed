package com.nuviosoftware.paymentservice.jms;

import com.ibm.mq.jakarta.jms.MQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import java.util.Random;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @JmsListener(destination = "ORDER.REQUEST")
    public void receive(Message message) throws JMSException {
        // receive message
        TextMessage textMessage = (TextMessage) message;
        final String textMessageBody = textMessage.getText();
        log.info("### 2 ### Payment Service received message: {} with correlationId: {}", textMessageBody, textMessage.getJMSCorrelationID());

        // some random logic to complete the order (80% of times it returns true)
        Random random = new Random();
        String orderCompleted = (random.nextInt(101) >= 20) ? "payment_ok" : "payment_failed";

        // send response
        log.info("### 3 ### Payment Service sending response");
        MQQueue orderRequestQueue = new MQQueue("ORDER.RESPONSE");
        jmsTemplate.convertAndSend(orderRequestQueue, orderCompleted, responseMessage -> {
            responseMessage.setJMSCorrelationID(textMessage.getJMSCorrelationID());
            return responseMessage;
        });
    }
}
