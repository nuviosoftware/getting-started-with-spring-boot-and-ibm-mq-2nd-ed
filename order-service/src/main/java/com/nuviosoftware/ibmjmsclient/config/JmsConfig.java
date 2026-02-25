package com.nuviosoftware.ibmjmsclient.config;

import com.ibm.mq.jakarta.jms.MQXAConnectionFactory;
import com.ibm.msg.client.jakarta.wmq.WMQConstants;
import jakarta.jms.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

@Configuration
public class JmsConfig {
    @Bean
    public ConnectionFactory mqConnectionFactory() throws Exception {

        MQXAConnectionFactory factory = new MQXAConnectionFactory();
        factory.setHostName("localhost");
        factory.setPort(1414);
        factory.setQueueManager("QM1");
        factory.setChannel("DEV.APP.SVRCONN");
        factory.setTransportType(WMQConstants.WMQ_CM_CLIENT);

        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        return new JmsTemplate(connectionFactory);
    }
}