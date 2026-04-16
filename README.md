# Query Guard Demo Application

## 1. Overview

This repository is a demo Spring Boot application for showcasing the Query Guard Spring Boot starter in a realistic backend service.

The Query Guard starter is consumed as a published Maven dependency, and this application demonstrates how it can be added to a Spring Boot application to observe query explosion patterns such as N+1 queries and repeated repository calls.

This repository is not the core Query Guard library. It is an integration/demo application that uses the starter.

## 2. Purpose

This demo exists to:

- reproduce query explosion and N+1 scenarios in a small domain model
- show how Query Guard is enabled in a real Spring Boot application
- provide a local setup with PostgreSQL and optional observability tooling
- make it easy to call endpoints that trigger inefficient query access patterns

## 3. Tech Stack

- Java 21
- Spring Boot 3.5.13
- Spring Web
- Spring Data JPA
- Spring Boot Actuator
- Micrometer Prometheus registry
- PostgreSQL
- Docker Compose
- Loki
- Promtail
- Grafana
- Maven
- Lombok

## 4. Query Guard Dependency

The application uses the Query Guard Spring Boot starter from Maven Central:

```xml
<dependency>
    <groupId>io.github.poojithairosha</groupId>
    <artifactId>query-guard-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 5. What This Demo Shows

The demo uses a course enrollment domain with users, courses, lessons, enrollments, and teachers.

Implemented query explosion scenarios:

- `GET /api/users/courses`
  - Loads all users with `userRepository.findAll()`.
  - Iterates through each user's lazy `enrollments`.
  - Accesses each enrollment's lazy `course`.
  - This can trigger N+1 style query behavior when data exists.

- `GET /api/courses/lessons`
  - Loads all courses with `courseRepository.findAll()`.
  - Iterates through each course's lazy `lessons`.
  - This can trigger N+1 style query behavior when data exists.

- `POST /api/enrollments/by-ids`
  - Accepts a list of enrollment IDs.
  - Calls `enrollmentRepository.findById(id)` once per ID.
  - This demonstrates repeated repository calls in a loop.

Query Guard is enabled through application configuration. The configured analyzers include duplicate query detection and N+1 detection.

## 6. Project Structure

Important source files:

```text
src/main/java/io/poojithairosha/query_guard_test_app
├── QueryGuardTestAppApplication.java
├── config
│   └── WebConfig.java
├── controller
│   └── MainController.java
├── dto
│   └── UserDTO.java
├── model
│   ├── Course.java
│   ├── Enrollment.java
│   ├── Lesson.java
│   ├── Teacher.java
│   └── User.java
├── repository
│   ├── CourseRepository.java
│   ├── EnrollmentRepository.java
│   ├── LessonRepository.java
│   ├── TeacherRepository.java
│   └── UserRepository.java
└── service
    ├── CourseService.java
    ├── EnrollmentService.java
    └── UserService.java
```

Key areas:

- `controller/MainController.java` exposes the demo API endpoints.
- `service/UserService.java` contains the user-to-enrollment-to-course traversal that can trigger N+1 behavior.
- `service/CourseService.java` contains the course-to-lesson traversal that can trigger N+1 behavior.
- `service/EnrollmentService.java` demonstrates repeated `findById` calls in a loop.
- `model/*` defines the JPA entities and lazy relationships used by the demo.
- `repository/*` contains Spring Data JPA repositories.
- `config/WebConfig.java` configures CORS for `http://localhost:5173`.
- `observability/*` contains Prometheus, Loki, Promtail, and Grafana configuration.

## 7. Running the Demo Locally

### Prerequisites

- Java 21
- Docker and Docker Compose
- Maven, or the included Maven wrapper

### Start PostgreSQL

Start only the database:

```bash
docker compose up -d postgres
```

The application expects PostgreSQL at:

```text
jdbc:postgresql://localhost:5432/qg-test-db
```

Database credentials:

```text
username: qg-user
password: password
database: qg-test-db
```

### Start the Application

```bash
./mvnw spring-boot:run
```

The application runs on the default Spring Boot port:

```text
http://localhost:8080
```

### Start the Full Observability Stack

To run PostgreSQL, Loki, Promtail, Grafana, and Prometheus:

```bash
docker compose up -d
```

Services:

- PostgreSQL: `localhost:5432`
- Grafana: `http://localhost:3000`
- Loki: `http://localhost:3100`
- Prometheus: `http://localhost:9090`

Grafana is configured with the admin password:

```text
admin
```

### Sample Data

This repository does not currently include a `data.sql`, `schema.sql`, `CommandLineRunner`, or other sample data loader.

Hibernate is configured with:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
```

That means the schema can be created or updated automatically, but demo data must be inserted separately before the query explosion endpoints show meaningful behavior.

## 8. Example Flow

1. Start PostgreSQL:

   ```bash
   docker compose up -d postgres
   ```

2. Start the Spring Boot application:

   ```bash
   ./mvnw spring-boot:run
   ```

3. Insert sample rows for users, courses, enrollments, and lessons.

4. Call an endpoint that traverses lazy relationships:

   ```bash
   curl http://localhost:8080/api/users/courses
   ```

5. Observe Query Guard output in the application logs and in `logs/queryguard.log`.

6. If the observability stack is running, inspect logs through Loki/Grafana and metrics through Prometheus.

## 9. Configuration

Application configuration is defined in `src/main/resources/application.yaml`.

### Spring Application

```yaml
spring:
  application:
    name: query-guard-test-app
```

### Datasource

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/qg-test-db
    username: qg-user
    password: password
```

### JPA

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
```

### Logging

```yaml
logging:
  file:
    name: logs/queryguard.log
  level:
    io.poojithairosha: INFO
```

### Query Guard

```yaml
query-guard:
  enabled: true
  analyzers:
    duplicate: true
    n-plus-one: true
  logging:
    enabled: true
  tracing:
    exclude-paths:
      - /actuator/**
      - /favicon.ico
```

### Actuator and Prometheus

```yaml
management:
  endpoints:
    web:
      exposure:
        include: prometheus
      cors:
        allowed-origins: "http://localhost:5173"
        allowed-methods: GET
        allowed-headers: "*"
  metrics:
    tags:
      application: queryguard
```

## 10. Metrics and Monitoring

Actuator and the Micrometer Prometheus registry are included.

The Prometheus endpoint is exposed at:

```text
http://localhost:8080/actuator/prometheus
```

Prometheus is configured in `observability/prometheus/prometheus.yml` to scrape:

```text
host.docker.internal:8080/actuator/prometheus
```

Promtail is configured to read application logs from:

```text
logs/*.log
```

The Promtail pipeline extracts Query Guard log fields such as:

- `type`
- `endpoint`
- `method`
- `traceId`
- `queryCount`
- `nplusOneDetected`
- `overallSeverity`

The repository also includes a Grafana dashboard JSON file:

```text
observability/grafana/query_guard_dashboard_grafana_loki.json
```

## 11. Sample API Endpoints

### Get User Course Names

```http
GET /api/users/courses
```

Example:

```bash
curl http://localhost:8080/api/users/courses
```

Demonstrates traversal from users to enrollments to courses through lazy JPA relationships.

### Get Course Lesson Titles

```http
GET /api/courses/lessons
```

Example:

```bash
curl http://localhost:8080/api/courses/lessons
```

Demonstrates traversal from courses to lessons through a lazy JPA relationship.

### Get Enrollments by IDs

```http
POST /api/enrollments/by-ids
```

Example:

```bash
curl -X POST http://localhost:8080/api/enrollments/by-ids \
  -H "Content-Type: application/json" \
  -d '[1,2,3]'
```

Demonstrates repeated `findById` calls in a loop.

### Get Teachers

```http
GET /api/teachers
```

Example:

```bash
curl http://localhost:8080/api/teachers
```

Returns all teachers through `teacherRepository.findAll()`.

## 12. Expected Output

API responses depend on the data inserted into PostgreSQL.

With no sample data loaded, endpoints that read entities may return empty arrays:

```json
[]
```

When data exists and query-heavy endpoints are called, Query Guard logging is enabled and writes to:

```text
logs/queryguard.log
```

The observability configuration expects Query Guard log entries to include structured fields that can be parsed by Promtail, including endpoint, method, query count, N+1 detection status, and severity.

## 13. Notes and Limitations

- This repository demonstrates Query Guard integration in a Spring Boot application.
- It is not the Query Guard core library or starter source repository.
- The application uses PostgreSQL; no in-memory database profile is configured.
- The repository does not currently include seeded demo data.
- The JPA relationships used for the demo are lazy and intentionally accessed in loops to make query explosion patterns observable.
- The application exposes Prometheus metrics, but only the Prometheus actuator endpoint is explicitly configured for web exposure.
- The Grafana dashboard is provided as JSON, but automatic dashboard provisioning is not configured in `compose.yaml`.

## 14. Related Repositories

- Query Guard core/starter repository: not specified in this repository
- Query Guard frontend/dashboard repository: not specified in this repository

## 15. License

![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)
