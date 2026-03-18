<div align="center">

  <br>
  <img src="https://icongr.am/feather/shield.svg?size=48&color=A1A1A6" alt="Shield Icon" />

  <h1 style="color: #FFFFFF; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;">
    <b>SENTINEL</b>
  </h1>
  <p style="color: #A1A1A6;"><i>Event-Driven Remote Monitoring & Task Execution Microservice</i></p>

  <a href="https://github.com/pedroforbeck/sentinel">
    <img src="https://readme-typing-svg.demolab.com?font=-apple-system,BlinkMacSystemFont,San+Francisco,Helvetica+Neue&weight=400&size=14&duration=4000&pause=1000&color=A1A1A6&center=true&vCenter=true&width=600&lines=Event-Driven+Architecture;Apache+Kafka+Message+Broker;Real-Time+Observability+Stack;Remote+Agent+Task+Execution" alt="Typing SVG" />
  </a>

  <br><br>

  <img src="https://img.shields.io/badge/Java_17-1C1C1E?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" />
  <img src="https://img.shields.io/badge/Spring_Boot_4-1C1C1E?style=for-the-badge&logo=spring-boot&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/Apache_Kafka-1C1C1E?style=for-the-badge&logo=apachekafka&logoColor=white" alt="Apache Kafka" />
  <img src="https://img.shields.io/badge/PostgreSQL-1C1C1E?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL" />
  <img src="https://img.shields.io/badge/Prometheus-1C1C1E?style=for-the-badge&logo=prometheus&logoColor=white" alt="Prometheus" />
  <img src="https://img.shields.io/badge/Grafana-1C1C1E?style=for-the-badge&logo=grafana&logoColor=white" alt="Grafana" />

  <br><br>

  <img src="https://img.shields.io/badge/Pattern-Event--Driven-1C1C1E?style=for-the-badge&logo=apachekafka&logoColor=white" alt="Pattern" />
  <img src="https://img.shields.io/badge/Observability-Actuator+Prometheus+Grafana-1C1C1E?style=for-the-badge&logo=grafana&logoColor=white" alt="Observability" />
  <img src="https://img.shields.io/badge/Status-Active_Development-FF9F0A?style=for-the-badge&logo=git&logoColor=white" alt="Status" />

  <br><br>

  <img src="https://github.com/pedroforbeck/sentinel/actions/workflows/build.yml/badge.svg" alt="CI" />

</div>

<br><br>

> **Abstract**
> **Sentinel** is a distributed system for remote machine monitoring and task execution, built on an event-driven architecture using **Apache Kafka**. It consists of two core components: a centralized **REST API** that manages machine registration and task lifecycle, and a lightweight **Agent** that runs on remote machines, self-registers, polls for pending tasks, executes them, and reports results asynchronously. The entire system is instrumented with a full observability stack — **Spring Boot Actuator**, **Micrometer**, **Prometheus**, and **Grafana** — providing real-time JVM, HTTP, and database metrics.

<br>

## <img src="https://icongr.am/feather/layers.svg?size=24&color=A1A1A6" align="absmiddle" /> Table of Contents

- [System Architecture](#-system-architecture)
- [Components](#-components)
- [Observability Stack](#-observability-stack)
- [Screenshots](#-screenshots)
- [API Reference](#-api-reference)
- [Development Roadmap](#-development-roadmap)
- [Deployment and Setup](#-deployment--setup)

---

## <img src="https://icongr.am/feather/cpu.svg?size=24&color=A1A1A6" align="absmiddle" /> System Architecture

Sentinel decouples task execution from task management through an asynchronous message broker. The API handles all inbound requests and persists state to PostgreSQL. The Agent operates independently on remote machines, communicating task results back via Kafka topics. The monitoring stack runs as sidecar containers, scraping metrics without impacting application performance.

<br>

```mermaid
flowchart TD
    classDef default fill:none,stroke:#A1A1A6,stroke-width:1px,color:#A1A1A6
    classDef highlight fill:none,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef kafka fill:none,stroke:#FFFFFF,stroke-width:1px,color:#FFFFFF,stroke-dasharray:5 5
    classDef monitoring fill:none,stroke:#FF9F0A,stroke-width:1px,color:#FF9F0A

    Agent[Sentinel Agent\nRemote Machine]:::highlight
    API{Sentinel API\nPort 8080}:::highlight
    DB[(PostgreSQL\nPort 15432)]:::default
    Broker{{Apache Kafka\nPort 9092}}:::kafka
    Prometheus[Prometheus\nPort 9090]:::monitoring
    Grafana[Grafana Dashboard\nPort 3000]:::monitoring

    Agent -->|POST /api/machines/register| API
    Agent -->|GET /api/tasks/machine/id/pending| API
    Agent -->|PUT /api/tasks/id/status| API
    API -->|Publishes task events| Broker
    API <-->|Persists machines and tasks| DB
    Broker -->|Consumes task results| API
    Prometheus -->|Scrapes /actuator/prometheus| API
    Grafana -->|Queries metrics| Prometheus
```

---

## <img src="https://icongr.am/feather/package.svg?size=24&color=A1A1A6" align="absmiddle" /> Components

### Sentinel API

The central command hub. Exposes a secured REST API for machine registration, task creation and lifecycle management. Built with Spring Boot 4, Spring Data JPA, Flyway for schema migrations, and Spring Kafka for async event publishing.

| Responsibility | Technology |
| :--- | :--- |
| REST API | Spring Boot 4 + Spring WebMVC |
| Persistence | Spring Data JPA + PostgreSQL + Flyway |
| Async Messaging | Apache Kafka |
| Observability | Spring Actuator + Micrometer + Prometheus |
| Security | API Key Authentication via `X-API-KEY` header |

### Sentinel Agent

A lightweight autonomous process deployed on any remote machine. On startup it self-registers with the API, then continuously polls for pending tasks, executes them locally, and reports results back asynchronously via Kafka.

| Responsibility | Behavior |
| :--- | :--- |
| Self-Registration | Sends hostname, IP, and OS info on startup |
| Task Polling | Periodically fetches `PENDING` tasks from the API |
| Task Execution | Executes shell commands locally |
| Result Reporting | Updates task status to `COMPLETED` or `FAILED` via Kafka |

---

## <img src="https://icongr.am/feather/activity.svg?size=24&color=A1A1A6" align="absmiddle" /> Observability Stack

Sentinel ships with a fully integrated observability stack out of the box. All containers are defined in `docker-compose.yml` and start together with a single command.

| Endpoint | Description |
| :--- | :--- |
| `GET /actuator/health` | Database, Kafka, disk and liveness status |
| `GET /actuator/metrics` | JVM, HTTP, thread and GC metrics |
| `GET /actuator/prometheus` | Prometheus-formatted scrape endpoint |
| `GET /actuator/flyway` | Database migration history |
| `localhost:9090` | Prometheus query UI |
| `localhost:3000` | Grafana live dashboard |

The Grafana dashboard provides real-time visibility into:

- **JVM Memory** — Heap and Non-Heap usage across G1GC pools
- **CPU Usage** — System and Process CPU over time
- **HTTP Statistics** — Request rate, response time and error rate per endpoint
- **HikariCP** — Connection pool active, idle and pending connections
- **GC Statistics** — Pause times and memory promotion rates
- **Logback** — Log event rate per level (INFO, WARN, ERROR, DEBUG)

---

## <img src="https://icongr.am/feather/monitor.svg?size=24&color=A1A1A6" align="absmiddle" /> Screenshots

Visualizing the Sentinel API's performance, metrics, and logs during standard operation and under stress testing.

<table align="center">
  <tr>
    <td align="center"><b>Grafana Dashboard</b><br><br><img src="docs/dashboard.jpg" alt="Grafana Dashboard" width="100%"></td>
    <td align="center"><b>Dashboard Under Stress Test</b><br><br><img src="docs/stressgrafena.jpg" alt="Grafana Stress Test" width="100%"></td>
  </tr>
  <tr>
    <td align="center"><b>Grafana Login</b><br><br><img src="docs/logingrafena.jpg" alt="Grafana Login" width="100%"></td>
    <td align="center"><b>Application Logs</b><br><br><img src="docs/log.jpg" alt="Application Logs" width="100%"></td>
  </tr>
</table>

---

## <img src="https://icongr.am/feather/code.svg?size=24&color=A1A1A6" align="absmiddle" /> API Reference

All endpoints require the `X-API-KEY` header.

### Machines

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/machines/register` | Register or update a machine |
| `GET` | `/api/machines` | List all registered machines |

### Tasks

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/tasks/machine/{machineId}` | Create a task for a machine |
| `GET` | `/api/tasks/machine/{machineId}/pending` | Get pending tasks for a machine |
| `PUT` | `/api/tasks/{taskId}/status` | Update task status and output |

---

## <img src="https://icongr.am/feather/tool.svg?size=24&color=A1A1A6" align="absmiddle" /> Development Roadmap

- [x] **Phase 1:** Project initialization, PostgreSQL schema and Flyway migrations
- [x] **Phase 2:** Apache Kafka consumer/producer configuration
- [x] **Phase 3:** Machine registration and task lifecycle REST API
- [x] **Phase 4:** Sentinel Agent with self-registration and task polling
- [x] **Phase 5:** Full observability stack — Actuator, Prometheus, Grafana
- [ ] **Phase 6:** Spring Security — protect Actuator and API endpoints
- [ ] **Phase 7:** Dockerize API and Agent
- [ ] **Phase 8:** Threat detection rules — flag anomalous task failure rates
- [ ] **Phase 9:** Admin REST endpoints for querying audit logs

---

## <img src="https://icongr.am/feather/terminal.svg?size=24&color=A1A1A6" align="absmiddle" /> Deployment & Setup

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker + Docker Compose

### 1. Clone & Start Infrastructure

```bash
git clone [https://github.com/pedroforbeck/sentinel.git](https://github.com/pedroforbeck/sentinel.git)
cd sentinel
git checkout develop

# Start PostgreSQL, Kafka, Prometheus and Grafana
docker-compose up -d
```

### 2. Configure the API

Copy the example properties and fill in your values:

```bash
cp sentinel-api/api/src/main/resources/application.properties.example \
   sentinel-api/api/src/main/resources/application.properties
```

### 3. Run the API

```bash
cd sentinel-api/api
mvn spring-boot:run
```

Verify at: `http://localhost:8080/actuator/health`

### 4. Run the Agent

```bash
cd sentinel-agent/agent
mvn spring-boot:run
```

The agent will self-register and begin polling for tasks.

### 5. Open Grafana Dashboard

```
http://localhost:3000  →  admin / admin
Dashboards → SpringBoot APM Dashboard
```

---

<div align="center">
  <br>
  <p style="color: #A1A1A6;">Architected and maintained by <b><a href="https://github.com/pedroforbeck" style="color: #A1A1A6; text-decoration: none;">Pedro Forbeck</a></b>.</p>
  <p>
    <a href="https://github.com/pedroforbeck">
      <img src="https://img.shields.io/badge/GitHub-1C1C1E?style=flat-square&logo=github&logoColor=white" alt="GitHub" />
    </a>
    <a href="https://www.linkedin.com/in/pedro-forbeck-180a98390/">
      <img src="https://img.shields.io/badge/LinkedIn-1C1C1E?style=flat-square&logo=linkedin&logoColor=white" alt="LinkedIn" />
    </a>
  </p>
</div>
