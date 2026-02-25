package com.nuviosoftware.ibmjmsclient.service;

import com.ibm.mq.jakarta.jms.MQQueue;
import com.nuviosoftware.ibmjmsclient.model.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Transactional
    public void processOrder(MQQueue orderRequestQueue, OrderRequest order) {

        // Send MQ Confirmation
        jmsTemplate.convertAndSend(orderRequestQueue, order.message(), textMessage -> {
            textMessage.setJMSCorrelationID(order.identifier());
            return textMessage;
        });
    }
}
