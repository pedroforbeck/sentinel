<div align="center">

  <br>
  <img src="https://icongr.am/feather/shield.svg?size=48&color=A1A1A6" alt="Shield Icon" />

  <h1 style="color: #FFFFFF; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;">
    <b>SENTINEL</b>
  </h1>
  <p style="color: #A1A1A6;"><i>Event-Driven Monitoring & Security Auditing Microservice</i></p>

  <a href="https://github.com/pedroforbeck/sentinel">
    <img src="https://readme-typing-svg.demolab.com?font=-apple-system,BlinkMacSystemFont,San+Francisco,Helvetica+Neue&weight=400&size=14&duration=4000&pause=1000&color=A1A1A6&center=true&vCenter=true&width=600&lines=Event-Driven+Architecture;Apache+Kafka+Message+Broker;Distributed+Tracing+%26+Auditing;Work+in+Progress+Ecosystem" alt="Typing SVG" />
  </a>

  <br><br>

  <img src="https://img.shields.io/badge/Java_17-1C1C1E?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" />
  <img src="https://img.shields.io/badge/Spring_Boot-1C1C1E?style=for-the-badge&logo=spring-boot&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/Apache_Kafka-1C1C1E?style=for-the-badge&logo=apachekafka&logoColor=white" alt="Apache Kafka" />
  <img src="https://img.shields.io/badge/PostgreSQL-1C1C1E?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL" />

  <br><br>

  <img src="https://img.shields.io/badge/Role-Event%20Consumer-1C1C1E?style=for-the-badge&logo=apachekafka&logoColor=white" alt="Role" />
  <img src="https://img.shields.io/badge/Pattern-Async%20Auditing-1C1C1E?style=for-the-badge&logo=confluent&logoColor=white" alt="Pattern" />
  <img src="https://img.shields.io/badge/Status-Work%20In%20Progress-FF9F0A?style=for-the-badge&logo=git&logoColor=white" alt="Status" />

</div>

<br><br>

> **Abstract**<br>
> This repository contains **Sentinel**, a centralized monitoring and auditing service currently under active development. Acting as the silent observer of the ecosystem, its primary responsibility is to consume events asynchronously via **Apache Kafka**. It records user actions, system alerts, and task statuses in real-time, providing an immutable audit trail without impacting the performance of the core domain microservices.

<br>

## <img src="https://icongr.am/feather/layers.svg?size=24&color=A1A1A6" align="absmiddle" /> Table of Contents

- [System Architecture](#-system-architecture)
- [Core Capabilities](#-core-capabilities)
- [Development Roadmap](#-development-roadmap)
- [Deployment & Setup](#-deployment--setup)

---

## <img src="https://icongr.am/feather/cpu.svg?size=24&color=A1A1A6" align="absmiddle" /> System Architecture

By leveraging a message broker, Sentinel completely decouples the logging and monitoring logic from the rest of the application. Core services (like the Task Engine) simply publish events to Kafka topics and continue executing. Sentinel independently consumes these topics, processes the payloads, and persists them for future querying and security analysis.

<br>

<details>
<summary><b style="color: #A1A1A6; cursor: pointer;">View Component Topology (Glass/Wireframe Diagram)</b></summary>
<br>

```mermaid
flowchart TD;
    %% Glassmorphism / Apple Aesthetic Styling
    classDef default fill:none,stroke:#A1A1A6,stroke-width:1px,color:#A1A1A6,rx:8,ry:8;
    classDef highlight fill:none,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF,rx:12,ry:12;
    classDef kafka fill:none,stroke:#FFFFFF,stroke-width:1px,color:#FFFFFF,stroke-dasharray: 5 5,rx:4,ry:4;

    %% Nodes
    TaskService[Task Engine\nPort 8082]:::default
    NotifyService[Notification Service\nPort 8083]:::default
    Broker{{Apache Kafka\nMessage Broker}}:::kafka
    Sentinel{Sentinel Service\nPort 8084}:::highlight
    DB[(Audit Database\nPostgreSQL)]:::default

    %% Connections
    TaskService -->|"Publishes Task Events"| Broker
    NotifyService -->|"Publishes Alerts"| Broker
    Broker -->|"Consumes Topics"| Sentinel
    Sentinel <-->|"Persists Audit Logs"| DB
```
</details>

---

## <img src="https://icongr.am/feather/command.svg?size=24&color=A1A1A6" align="absmiddle" /> Core Capabilities

| Feature | Description |
| :--- | :--- |
| <img src="https://icongr.am/feather/radio.svg?size=18&color=A1A1A6" align="absmiddle" /> **Kafka Integration** | Consumes streams of high-throughput data asynchronously via dedicated Kafka topics. |
| <img src="https://icongr.am/feather/database.svg?size=18&color=A1A1A6" align="absmiddle" /> **Immutable Auditing** | Persists a reliable, unalterable log of cross-service activities into a centralized PostgreSQL database. |
| <img src="https://icongr.am/feather/eye.svg?size=18&color=A1A1A6" align="absmiddle" /> **System Observability** | Centralizes payload monitoring to track bottlenecks, failures, and unauthorized access attempts. |
| <img src="https://icongr.am/feather/maximize-2.svg?size=18&color=A1A1A6" align="absmiddle" /> **Fault Tolerance** | Designed to survive network spikes; if Sentinel goes down, Kafka retains the events until it spins back up. |

---

## <img src="https://icongr.am/feather/tool.svg?size=24&color=A1A1A6" align="absmiddle" /> Development Roadmap

As a **Work in Progress (WIP)**, Sentinel is being built iteratively. Below is the current progress of the core modules:

- [x] **Phase 1:** Project initialization and PostgreSQL schema setup.
- [x] **Phase 2:** Apache Kafka consumer configuration and listener bindings.
- [ ] **Phase 3:** Deserialization of complex system event payloads.
- [ ] **Phase 4:** Threat detection rules (e.g., flagging multiple failed tasks/logins).
- [ ] **Phase 5:** REST endpoints for administrators to query the audit logs.

---

## <img src="https://icongr.am/feather/terminal.svg?size=24&color=A1A1A6" align="absmiddle" /> Deployment & Setup

To run Sentinel locally, ensure you have **Java 17+**, **Maven 3.8+**, **PostgreSQL**, and **Apache Kafka / Zookeeper** running in your local environment or via Docker.

### 1. Database & Kafka Configuration
Ensure your PostgreSQL database (e.g., `db_sentinel`) is created. You will also need Kafka running on its default port (`9092`).

### 2. Environment Variables
Configure your `application.properties` or `application.yml` with your local credentials:

```yaml
# Server Configuration
server.port: 8084

# Database Configuration (Audit Schema)
spring.datasource.url: jdbc:postgresql://localhost:5432/db_sentinel
spring.datasource.username: your_postgres_user
spring.datasource.password: your_postgres_password
spring.jpa.hibernate.ddl-auto: update

# Apache Kafka Configuration
spring.kafka.bootstrap-servers: localhost:9092
spring.kafka.consumer.group-id: sentinel-auditor-group
spring.kafka.consumer.auto-offset-reset: earliest
```

### 3. Build & Execute
Navigate to the project root directory and start the Spring Boot application:

```bash
# Clone the repository
git clone https://github.com/pedroforbeck/sentinel.git
cd sentinel

# Switch to the active development branch
git checkout develop

# Run the application
./mvnw spring-boot:run
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
