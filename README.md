# Getting Started

This repository contains three local projects used in the examples:

- `order-service` (Spring Boot)
- `payment-service` (Spring Boot)
- `custom-ibm-mq` (IBM MQ Docker image/Dockerfile)

## Requirements
- Docker
- Docker Compose

## Quick start

1. Build the images:

```
docker-compose build
```

2. Start the services in the background:

```
docker-compose up -d
```

### Service ports
- Order service: http://localhost:8080
- Payment service: http://localhost:9090
- IBM MQ client port: 1414
- IBM MQ console: https://localhost:9443/ibmmq/console/

### Stop and clean up

```
docker-compose down
```

## Test

Send an order message to the order service:
```
curl -X POST \
http://localhost:8080/orders \
-H 'content-type: application/json' \
-d '{
"message": "This is a test message",
"identifier": "1234567890"
}'
```

Check logs:
```
order-service_1    | 2026-02-24T10:10:04.906Z  INFO 1 --- [nio-8080-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
order-service_1    | 2026-02-24T10:10:04.906Z  INFO 1 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
order-service_1    | 2026-02-24T10:10:04.907Z  INFO 1 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 1 ms
order-service_1    | 2026-02-24T10:10:05.020Z  INFO 1 --- [nio-8080-exec-1] c.n.ibmjmsclient.rest.OrderController    : ### 1 ### Order Service sending order message 'This is a test message' to the queue
mqserver           | 2026-02-24T10:10:05.026Z Environment variable MQ_ADMIN_PASSWORD is deprecated, use secrets to set the passwords
mqserver           | 2026-02-24T10:10:05.035Z Environment variable MQ_ADMIN_PASSWORD is deprecated, use secrets to set the passwords
payment-service_1  | 2026-02-24T10:10:05.086Z  INFO 1 --- [ntContainer#0-1] c.n.paymentservice.jms.PaymentService    : ### 2 ### Payment Service received message: This is a test message with correlationId: 1234567890
payment-service_1  | 2026-02-24T10:10:05.086Z  INFO 1 --- [ntContainer#0-1] c.n.paymentservice.jms.PaymentService    : ### 3 ### Payment Service sending response
order-service_1    | 2026-02-24T10:10:05.120Z  INFO 1 --- [ntContainer#0-1] c.n.i.jms.OrderResponseListener          : ### 4 ### Order Service received message response : payment_ok with correlation id: 1234567890
mqserver
```
