package com.nuviosoftware.paymentservice.jms;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    private final CircuitBreaker paymentCircuitBreaker;

    public PaymentService() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .slidingWindowSize(10)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .build();

        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        this.paymentCircuitBreaker = registry.circuitBreaker("paymentService");
    }

    @JmsListener(destination = "ORDER.REQUEST")
    public void receive(Message message) throws JMSException {
        try {
            paymentCircuitBreaker.executeCheckedSupplier(() -> {
                TextMessage textMessage = (TextMessage) message;
                log.info("Processing: {}", textMessage.getText());
                if (textMessage.getText().contains("Failure")) {
                    throw new RuntimeException("Simulated payment failure");
                }
                return null;
            });
        } catch (CallNotPermittedException e) {
            // This happens when the Circuit is OPEN
            log.error("### CIRCUIT OPEN ### Skipping processing. Message stays on MQ.");
            throw new RuntimeException("Circuit is open, backing off...", e);
        } catch (Throwable throwable) {
            // This happens when the logic itself fails
            log.error("### LOGIC FAILED ### Marking failure in Circuit Breaker.");
            throw new RuntimeException("Business logic failure", throwable);
        }
    }
}
