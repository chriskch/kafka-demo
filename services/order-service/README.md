# order-service

The producer service accepts REST orders and publishes JSON messages to the Kafka `orders`
topic using SmallRye Reactive Messaging.

## Development

```bash
./mvnw quarkus:dev
```

- Default port: `8081`
- Kafka endpoint: `KAFKA_BOOTSTRAP_SERVERS` (defaults to `localhost:39092`)
- CORS: enforced by `CorsFilter` so the UI hosted at `http://localhost:8080` can call the API

## Testing

```bash
./mvnw test
```

`OrderResourceTest` uses the in-memory connector to assert messages are published to Kafka.

## Packaging for production

| Artifact                            | Command                                                                 |
|-------------------------------------|-------------------------------------------------------------------------|
| Fast jar (default)                  | `./mvnw package`                                                        |
| Über-jar                            | `./mvnw package -Dquarkus.package.type=uber-jar`                        |
| Native executable                   | `./mvnw package -Dnative -Dquarkus.native.container-build=true`         |
| Container image                     | `./mvnw package -Dquarkus.container-image.build=true`                   |

The runnable jar appears under `target/quarkus-app/`. Launch it with:

```bash
java -jar target/quarkus-app/quarkus-run.jar
```

Ensure `KAFKA_BOOTSTRAP_SERVERS` points to your Kafka broker when running outside dev mode.

### Container image / Docker Compose

- Build image directly: `docker build -f src/main/docker/Dockerfile.jvm -t kafka-demo/order-service .` (the Dockerfile runs `./mvnw package -Dmaven.test.skip=true` to avoid compiling/executing tests).
- Or let the top-level `docker compose up --build` target build and run the service together with Kafka and the other apps.

## API

### `POST /orders`

- Accepts a JSON body containing `customerEmail`, `items[]`, and `totalAmount`.
- Returns `202 Accepted` with a generated `orderId` when the message is queued.

Example request:

```bash
curl -X POST http://localhost:8081/orders \
  -H "Content-Type: application/json" \
  -d '{"customerEmail":"customer@example.com","items":["Coffee Beans"],"totalAmount":19.5}'
```

## Configuration reference

| Property / Env                      | Description                            | Default             |
|-------------------------------------|----------------------------------------|---------------------|
| `KAFKA_BOOTSTRAP_SERVERS`           | Kafka bootstrap servers                | `localhost:39092`   |
| `quarkus.http.port`                 | HTTP port                              | `8081`              |
| `demo.frontend.origin` / `DEMO_FRONTEND_ORIGIN` | Allowed browser origin | `http://localhost:8080` |

The UI itself lives in the root project (`http://localhost:8080`) and posts orders to this
service.
