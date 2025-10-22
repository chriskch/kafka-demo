# notification-service

A Kafka consumer that listens to the `orders` topic and logs simulated notification events.
Processed messages are kept in-memory so tests and the demo UI can query recent activity.

## Development

```bash
./mvnw quarkus:dev
```

- Default port: `8082`
- Kafka endpoint: `KAFKA_BOOTSTRAP_SERVERS` (defaults to `localhost:39092`)
- `CorsFilter` pins browser access to `http://localhost:8080` for the shared UI

## Testing

```bash
./mvnw test
```

`OrderNotificationConsumerTest` and `NotificationResourceTest` prove the consumer handles
messages and exposes them via REST.

## Packaging for production

| Artifact                            | Command                                                                 |
|-------------------------------------|-------------------------------------------------------------------------|
| Fast jar (default)                  | `./mvnw package`                                                        |
| Über-jar                            | `./mvnw package -Dquarkus.package.type=uber-jar`                        |
| Native executable                   | `./mvnw package -Dnative -Dquarkus.native.container-build=true`         |
| Container image                     | `./mvnw package -Dquarkus.container-image.build=true`                   |

Run the packaged app with:

```bash
java -jar target/quarkus-app/quarkus-run.jar
```

Remember to supply `KAFKA_BOOTSTRAP_SERVERS` so the service can reach your Kafka cluster.

### Container image / Docker Compose

- Build image directly: `docker build -f src/main/docker/Dockerfile.jvm -t kafka-demo/notification-service .` (the Dockerfile invokes `./mvnw package -DskipTests` during the build).
- Or use the root-level `docker compose up --build` to build and run the entire demo stack automatically.

## REST endpoints

| Path               | Description                                        |
|--------------------|----------------------------------------------------|
| `GET /notifications` | Returns the processed order messages in JSON format |
| `GET /q/health`      | Standard Quarkus health endpoint                   |

The demo UI polls `/notifications` to display activity.  
Only service logs (and the REST endpoint) simulate downstream work; no emails are actually sent.

## Configuration reference

| Property / Env                      | Description                            | Default             |
|-------------------------------------|----------------------------------------|---------------------|
| `KAFKA_BOOTSTRAP_SERVERS`           | Kafka bootstrap servers                | `localhost:39092`   |
| `quarkus.http.port`                 | HTTP port                              | `8082`              |
| `CorsFilter.FRONTEND_ORIGIN`        | Allowed browser origin                 | `http://localhost:8080` |

Health endpoints remain available even if Kafka is unreachable, making it easier to
demonstrate replay behaviour (start this service after producing orders).
