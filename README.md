# Kafka Demo

This repository demonstrates how Apache Kafka decouples microservices through asynchronous messaging.Three applications collaborate:

- **order-service** – REST producer that publishes order events to Kafka.
- **notification-service** – Kafka consumer that logs notifications for each order.
- **web-shell (root app)** – Quarkus app that serves the demo UI.

Infrastructure is provided via Docker Compose: a single-node Kafka broker running in KRaft mode and Kafka UI for observing topics.

## Project layout

```
.
├── docker-compose.yml          # Kafka + Kafka UI
├── services
│   ├── order-service           # Producer microservice
│   └── notification-service    # Consumer microservice
└── src/main/resources/...      # Web UI served by the root Quarkus app
```

## Prerequisites

- Docker & Docker Compose
- Java 21 or newer (JDK)
- Maven (wrapper provided via `./mvnw`)

## Bootstrap the stack

1. Start Kafka and Kafka UI:

   ```bash
   docker compose up
   ```

   Kafka UI is available at [http://localhost:8090](http://localhost:8090), the broker listens on `localhost:39092`.
2. Run each Quarkus application in dev mode (three terminals recommended):

   ```bash
   # Terminal 1 – producer
   cd services/order-service
   ./mvnw quarkus:dev

   # Terminal 2 – consumer
   cd services/notification-service
   ./mvnw quarkus:dev

   # Terminal 3 – web UI
   cd /path/to/kafka-demo
   ./mvnw quarkus:dev
   ```

   Ports: UI `8080`, order service `8081`, notification service `8082`.

## Run everything via Docker Compose

For demo-only scenarios you can run the full stack in containers:

```bash
docker compose up --build            # builds (tests skipped) and starts Kafka, services, UI and Kafka UI
```

Services remain reachable on the usual localhost ports:

- UI: <http://localhost:8080>
- Order API: <http://localhost:8081>
- Notification feed: <http://localhost:8082/notifications>
- Kafka UI: <http://localhost:8090>

Stop the stack with `docker compose down`. Use `docker compose up --build --force-recreate` after making code changes to ensure images are rebuilt.  
> The Dockerfiles invoke `./mvnw package -Dmaven.test.skip=true`, so tests are neither executed nor compiled during image builds.

## Browser walk-through

Open [http://localhost:8080](http://localhost:8080). The UI issues `POST http://localhost:8081/orders` and polls `http://localhost:8082/notifications` to illustrate how Kafka transports the messages.
Both services ship with a `CorsFilter` that pins `Access-Control-Allow-Origin` to `http://localhost:8080`. Override the origin via `DEMO_FRONTEND_ORIGIN` if the UI runs elsewhere.

## Development workflow

- **Hot reload** – `./mvnw quarkus:dev` in each module enables live coding. Changes in the UI or services are detected automatically.
- **Testing** – run unit/integration tests per module:

  ```bash
  ./mvnw test                # from the project root (tests UI only)
  ./mvnw test -pl services/order-service
  ./mvnw test -pl services/notification-service
  ```

  or execute inside each service directory.
- **Kafka configuration** – override `KAFKA_BOOTSTRAP_SERVERS` in each service when pointing to a remote cluster.

## Production builds

| Module                            | Jar build          | Über-jar / native example                                   | Docker image                                            |
| --------------------------------- | ------------------ | ------------------------------------------------------------ | ------------------------------------------------------- |
| Root web UI                       | `./mvnw package` | `./mvnw package -Dquarkus.package.type=uber-jar`           | `./mvnw package -Dquarkus.container-image.build=true` |
| `services/order-service`        | `./mvnw package` | `./mvnw package -Dnative -Dquarkus.native.container-build` | `./mvnw package -Dquarkus.container-image.build=true` |
| `services/notification-service` | `./mvnw package` | `./mvnw package -Dnative -Dquarkus.native.container-build` | `./mvnw package -Dquarkus.container-image.build=true` |

Resulting runnable jars reside in `target/quarkus-app/`. Start them with `java -jar target/quarkus-app/quarkus-run.jar`, ensuring the `KAFKA_BOOTSTRAP_SERVERS` environment variable points to your Kafka cluster.

## Configuration quick reference

| Key / Env Var                      | Description                         | Default                                           |
| ---------------------------------- | ----------------------------------- | ------------------------------------------------- |
| `KAFKA_BOOTSTRAP_SERVERS`        | Kafka broker endpoint for services  | `localhost:39092`                               |
| `quarkus.http.port` (per module) | HTTP port override                  | UI `8080`, producer `8081`, consumer `8082` |
| `demo.frontend.origin` / `DEMO_FRONTEND_ORIGIN` | Allowed origin for browser requests | `http://localhost:8080` |

## Troubleshooting

- **CORS errors** – confirm the services were restarted after editing `CorsFilter` or CORS properties.
- **Kafka connectivity** – services fail fast if the broker is down; check `docker compose logs`.
- **Port conflicts** – adjust `quarkus.http.port` in each module or run `./mvnw quarkus:dev -Dquarkus.http.port=...`.

## Stretch ideas

- Add additional consumers (billing, fulfillment) to show fan-out.
- Partition the topic and observe assignments in Kafka UI.
- Persist notifications in a DB and surface them in the UI.
