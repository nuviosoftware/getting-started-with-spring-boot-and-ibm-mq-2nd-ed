package com.nuviosoftware.ibmjmsclient.rest;

import com.ibm.mq.jakarta.jms.MQQueue;
import com.nuviosoftware.ibmjmsclient.model.OrderRequest;
import com.nuviosoftware.ibmjmsclient.service.OrderService;
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

    @Autowired
    private OrderService orderService;

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);


    @PostMapping
    public ResponseEntity<OrderRequest> createOrder(@RequestBody OrderRequest order) throws JMSException {
        logger.info("### 1 ### Order Service sending order message '{}' to the queue", order.message());

        MQQueue orderRequestQueue = new MQQueue("ORDER.REQUEST");

        orderService.processOrder(orderRequestQueue, order);

        return new ResponseEntity<>(order, HttpStatus.ACCEPTED);
    }
}
