package com.nuviosoftware.paymentservice.jms;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @JmsListener(destination = "ORDER.REQUEST")
    @SendTo("")
    public String receive(Message message) throws JMSException {
        log.info("### 2 ### Processing request. Reply will go to: {}", message.getJMSReplyTo());
        return "payment_ok";
    }

}
