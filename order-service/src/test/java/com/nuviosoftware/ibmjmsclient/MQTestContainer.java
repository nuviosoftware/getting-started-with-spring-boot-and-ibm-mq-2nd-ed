package com.nuviosoftware.ibmjmsclient;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.Duration;
import java.util.Map;

public class MQTestContainer {

    @SuppressWarnings("resource")
    public static GenericContainer<?> setupMqContainer() {
        Map<String, String> environmentVariables = Map.of(
                "LICENSE", "accept",
                "MQ_QMGR_NAME", "QM1",
                "ICC_SHIFT", "3",
                "MQ_ADMIN_PASSWORD", "password123"
        );

        return new GenericContainer<>("nuviosoftware-mq-local")
                .withExposedPorts(1414)
                .withEnv(environmentVariables)
                .waitingFor(Wait.forListeningPort())
                .withStartupTimeout(Duration.ofMinutes(1));
    }

}