# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A food ordering microservices system demonstrating Clean Architecture + DDD + SAGA + Transactional Outbox patterns with Kafka. Four independently deployable Spring Boot 3.x services (Java 17, Maven multi-module).

## Build & Test Commands

All Maven commands run from `food-ordering-system/` (the Maven root):

```bash
# Full build (compile + test + package)
cd food-ordering-system && mvn clean install

# Run all tests
cd food-ordering-system && mvn clean test
# or use the convenience script from repo root:
./test-all.sh

# Run tests for a single module
cd food-ordering-system && mvn -pl order-service/order-domain/order-application-service test

# Run a single test class
cd food-ordering-system && mvn -pl order-service/order-domain/order-application-service -Dtest=OrderApplicationServiceTest test

# Skip tests during build
cd food-ordering-system && mvn clean install -DskipTests
```

## Infrastructure Setup (Local Docker)

Prerequisites: Docker Desktop, PostgreSQL (user=`postgres`, pwd=`admin`), Java 17, Maven.

```bash
cd food-ordering-system/infrastructure/docker-compose

# 1. Start Zookeeper
docker compose -f common.yml -f zookeeper.yml up --remove-orphans

# 2. Start Kafka cluster (3 brokers: localhost:19092, 29092, 39092)
docker-compose -f common.yml -f kafka_cluster.yml up

# 3. Create Kafka topics (run once only)
docker-compose -f common.yml -f init_kafka.yml up

# 4. Kafka Manager UI: http://localhost:9000
#    Add cluster: name=food-ordering-system-cluster, zookeeper=zookeeper:2181
```

Then run each service from the IDE or via `mvn spring-boot:run` in its `-container` module.

## Architecture

### Module Structure (per service)

Each of the four services (`order-service`, `payment-service`, `restaurant-service`, `customer-service`) follows the same layered structure:

```
<service>/
  <service>-domain/
    <service>-domain-core/        # Pure domain: entities, value objects, domain events, OrderDomainService interface + impl
    <service>-application-service/ # Use cases: command handlers, SAGA steps, outbox schedulers, input/output port interfaces
  <service>-application/           # REST controllers (Spring MVC)
  <service>-dataaccess/            # JPA entities, repository adapters (implements output port repositories)
  <service>-messaging/             # Kafka listeners (implements input message ports) and publishers (implements output publisher ports)
  <service>-container/             # Spring Boot main class, application.yml, SecurityConfig, bean wiring
```

Shared infrastructure lives under `food-ordering-system/`:
- `common/common-domain` — base `AggregateRoot`, `Entity`, `ValueObject`, domain value objects (`OrderId`, `CustomerId`, etc.)
- `common/common-application` — `GlobalExceptionHandler`
- `common/common-dataaccess` — shared JPA entity base
- `infrastructure/saga` — `SagaStep<T>` interface (process/rollback)
- `infrastructure/outbox` — `OutboxScheduler` interface, `OutboxStatus`, `SagaStatus` enums
- `infrastructure/kafka/kafka-model` — Avro model classes (generated/hand-maintained)
- `infrastructure/kafka/kafka-producer` — `KafkaProducer<K,V>`, `KafkaMessageHelper`
- `infrastructure/kafka/kafka-consumer` — Kafka consumer config
- `infrastructure/kafka/kafka-config-data` — `KafkaConfigData`, `KafkaProducerConfigData`, `KafkaConsumerConfigData`

### Dependency Flow (Clean Architecture)

```
domain-core  <--  application-service  <--  (dataaccess + messaging + application)  <--  container
```

- `domain-core` has zero Spring/framework dependencies — pure Java domain logic.
- `application-service` defines **input ports** (interfaces the REST layer calls) and **output ports** (interfaces the domain calls for persistence and messaging). It depends only on `domain-core`.
- `dataaccess` and `messaging` implement the output ports.
- `container` wires everything together via Spring's `@Bean` / component scan.

### SAGA + Outbox Pattern

The order lifecycle is a distributed SAGA across three services:

```
Order Service → [payment-request topic] → Payment Service → [payment-response topic] → Order Service
Order Service → [restaurant-approval-request topic] → Restaurant Service → [restaurant-approval-response topic] → Order Service
Customer Service → [customer topic] → Order Service (projecting customer data)
```

**Outbox pattern**: Before publishing to Kafka, domain events are saved to the local outbox table within the same DB transaction. Scheduled `OutboxScheduler` beans (every 10 s) read `STARTED` outbox entries and publish them to Kafka, then mark them `COMPLETED` or `FAILED`.

Key classes to understand:
- `OrderPaymentSaga` — implements `SagaStep<PaymentResponse>`: `process()` pays the order and saves an approval outbox entry; `rollback()` cancels payment.
- `OrderApprovalSaga` — implements `SagaStep<RestaurantApprovalResponse>`: `process()` approves the order; `rollback()` initiates payment cancellation.
- `PaymentOutboxHelper` / `ApprovalOutboxHelper` — manage outbox entries in the DB.
- Outbox schedulers in `order-service/order-domain/order-application-service/src/main/java/.../outbox/scheduler/` poll and publish.

### Kafka Topics

| Topic | Direction |
|---|---|
| `payment-request` | Order → Payment |
| `payment-response` | Payment → Order |
| `restaurant-approval-request` | Order → Restaurant |
| `restaurant-approval-response` | Restaurant → Order |
| `customer` | Customer → Order |

Avro schemas are in `infrastructure/kafka/kafka-model`. Schema Registry runs at `http://localhost:8081`.

### Service Ports

| Service | Port |
|---|---|
| order-service | 8181 |
| payment-service | 8182 |
| restaurant-service | 8183 |
| customer-service | 8184 |

### Database

Each service has its own PostgreSQL schema (all in `localhost:5432/postgres`):
- `order` schema → order-service
- `payment` schema → payment-service  
- `restaurant` schema → restaurant-service
- `customer` schema → customer-service

Schema DDL is in each service's `src/main/resources/init-schema.sql`, applied automatically on startup (`spring.sql.init.mode: always`).

DB env var overrides: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`.

### Security

Each service has a `SecurityConfig` with Spring Security. Rate limiting via Resilience4j (`resilience4j.ratelimiter`). `@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")` guards service methods. JWT resource server is configurable via `JWT_JWK_SET_URI` env var (empty by default for local dev).

### Dependency Version Management

Security-sensitive dependency versions (`kafka-clients`, `postgresql`, `avro`, `commons-compress`, `snakeyaml`) are pinned centrally in the root `food-ordering-system/pom.xml` `<properties>`/`<dependencyManagement>` so all modules inherit the same patched versions. Fix Snyk/Dependabot alerts there rather than in individual service POMs — see `SECURITY-FIXES.md` for the current pinned versions and rationale. Kafka major-version upgrades (e.g. 3.x → 4.x) need coordinated testing with Spring Kafka and the broker/container images before bumping.

## Kubernetes Deployment

K8s manifests are in `food-ordering-system/k8s-yamls/docker-desktop/`. Helm charts for Kafka (Confluent cp-helm-charts) and kafka-ui live under `food-ordering-system-infra/helm/`. See `DEPLOY LOCAL K8S.md` for the full setup sequence.

## Postman Collection

`food-ordering-system/Clean Architecture - Food Ordering.postman_collection.json` contains all API calls for local testing.
