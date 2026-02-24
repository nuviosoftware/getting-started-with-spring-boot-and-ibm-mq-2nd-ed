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

