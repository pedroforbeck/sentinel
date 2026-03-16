<div align="center">

  <br>
  <img src="https://icongr.am/feather/shield.svg?size=48&color=A1A1A6" alt="Shield Icon" />

  <h1 style="color: #FFFFFF; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;">
    <b>SENTINEL</b>
  </h1>
  <p style="color: #A1A1A6;"><i>Centralized API Gateway & Security Monitoring Microservice</i></p>

  <a href="https://github.com/pedroforbeck/sentinel">
    <img src="https://readme-typing-svg.demolab.com?font=-apple-system,BlinkMacSystemFont,San+Francisco,Helvetica+Neue&weight=400&size=14&duration=4000&pause=1000&color=A1A1A6&center=true&vCenter=true&width=600&lines=Zero-Trust+Security+Architecture;Centralized+API+Gateway;Rate+Limiting+%26+Traffic+Control;Work+in+Progress+Ecosystem" alt="Typing SVG" />
  </a>

  <br><br>

  <img src="https://img.shields.io/badge/Java_17-1C1C1E?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" />
  <img src="https://img.shields.io/badge/Spring_Cloud-1C1C1E?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Cloud" />
  <img src="https://img.shields.io/badge/Redis-1C1C1E?style=for-the-badge&logo=redis&logoColor=white" alt="Redis" />
  <img src="https://img.shields.io/badge/Docker-1C1C1E?style=for-the-badge&logo=docker&logoColor=white" alt="Docker" />

  <br><br>

  <img src="https://img.shields.io/badge/Role-API%20Gateway-1C1C1E?style=for-the-badge&logo=kong&logoColor=white" alt="Role" />
  <img src="https://img.shields.io/badge/Pattern-Zero%20Trust-1C1C1E?style=for-the-badge&logo=auth0&logoColor=white" alt="Pattern" />
  <img src="https://img.shields.io/badge/Status-Work%20In%20Progress-FF9F0A?style=for-the-badge&logo=git&logoColor=white" alt="Status" />

</div>

<br><br>

> **Abstract**<br>
> This repository contains **Sentinel**, an intelligent API Gateway and security layer currently under active development. Acting as the single point of entry for the ecosystem, its primary responsibility is to handle cross-cutting concerns such as authentication, request routing, rate limiting, and threat monitoring before traffic ever hits the underlying domain microservices.

<br>

## <img src="https://icongr.am/feather/layers.svg?size=24&color=A1A1A6" align="absmiddle" /> Table of Contents

- [System Architecture](#-system-architecture)
- [Core Capabilities](#-core-capabilities)
- [Development Roadmap](#-development-roadmap)
- [Deployment & Setup](#-deployment--setup)

---

## <img src="https://icongr.am/feather/cpu.svg?size=24&color=A1A1A6" align="absmiddle" /> System Architecture

By abstracting security and routing away from the core services (like the Task Engine and Notification Service), the ecosystem becomes inherently more scalable and secure. Sentinel intercepts incoming external requests, validates JWT tokens, checks rate limits against a Redis cache, and securely proxies the request to the correct internal port.

<br>

<details>
<summary><b style="color: #A1A1A6; cursor: pointer;">View Component Topology (Glass/Wireframe Diagram)</b></summary>
<br>

```mermaid
flowchart TD;
    %% Glassmorphism / Apple Aesthetic Styling
    classDef default fill:none,stroke:#A1A1A6,stroke-width:1px,color:#A1A1A6,rx:8,ry:8;
    classDef highlight fill:none,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF,rx:12,ry:12;
    classDef cache fill:none,stroke:#FF3B30,stroke-width:1px,color:#FF3B30,rx:4,ry:4;

    %% Nodes
    ClientMobile([External Clients]):::default
    Sentinel{Sentinel Gateway\nPort 8080}:::highlight
    Redis[(Redis\nRate Limiting)]:::cache
    TaskService[Task Engine\nPort 8082]:::default
    NotifyService[Notification Service\nPort 8083]:::default

    %% Connections
    ClientMobile -->|"REST / HTTPS"| Sentinel
    Sentinel <-->|"Validates Session and Rate Limits"| Redis
    Sentinel -->|"Proxy / Routing"| TaskService
    Sentinel -->|"Proxy / Routing"| NotifyService
```
</details>

---

## <img src="https://icongr.am/feather/command.svg?size=24&color=A1A1A6" align="absmiddle" /> Core Capabilities

| Feature | Description |
| :--- | :--- |
| <img src="https://icongr.am/feather/lock.svg?size=18&color=A1A1A6" align="absmiddle" /> **Authentication & JWT** | Validates incoming tokens centrally, ensuring unauthenticated requests are dropped at the edge. |
| <img src="https://icongr.am/feather/map-pin.svg?size=18&color=A1A1A6" align="absmiddle" /> **Dynamic Routing** | Acts as a reverse proxy, mapping external API calls to internal microservice endpoints. |
| <img src="https://icongr.am/feather/activity.svg?size=18&color=A1A1A6" align="absmiddle" /> **Rate Limiting** | Prevents abuse and DDoS attacks by throttling requests using a token bucket algorithm via Redis. |
| <img src="https://icongr.am/feather/eye.svg?size=18&color=A1A1A6" align="absmiddle" /> **Traffic Observability** | Injects trace IDs and logs metric data for request profiling and debugging across the ecosystem. |

---

## <img src="https://icongr.am/feather/tool.svg?size=24&color=A1A1A6" align="absmiddle" /> Development Roadmap

As a **Work in Progress (WIP)**, Sentinel is being built iteratively. Below is the current progress of the core modules:

- [x] **Phase 1:** Project initialization and reverse proxy configuration.
- [x] **Phase 2:** Integration of global JWT validation filters.
- [ ] **Phase 3:** Redis integration for distributed rate limiting.
- [ ] **Phase 4:** Circuit breakers and fallback mechanisms for downstream service failures.
- [ ] **Phase 5:** Comprehensive unit and integration test coverage.

---

## <img src="https://icongr.am/feather/terminal.svg?size=24&color=A1A1A6" align="absmiddle" /> Deployment & Setup

To run Sentinel locally, ensure you have **Java 17+**, **Maven 3.8+**, and **Redis** running.

### 1. Cache Configuration
Ensure your local Redis instance is running on the default port `6379`. This is required for the rate limiter to function.

### 2. Environment Variables
Configure your `application.properties` or `application.yml` with your local routing variables:

```yaml
# Server Configuration
server.port: 8080

# Redis Configuration (Rate Limiting)
spring.redis.host: localhost
spring.redis.port: 6379

# JWT Validation
api.security.token.secret: your_super_secret_key_here

# Microservice Routing Rules
routes.task-engine.url: http://localhost:8082
routes.notification-service.url: http://localhost:8083
```

### 3. Build & Execute
Navigate to the project root directory and start the Gateway application:

```bash
# Clone the repository
git clone https://github.com/pedroforbeck/sentinel.git

# Navigate to the directory
cd sentinel

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
