# 🧩 Employee Management Microservices System

A **modular, event-driven microservice architecture** for managing employees and departments.  
The system uses **Spring Boot 3**, **Spring Cloud**, **Kafka**, **Docker Compose**, and **JWT authentication** for secure inter-service communication.

---

## 🏗️ Architecture Overview

This project follows a **microservices-based architecture** with centralized configuration, service discovery, and API gateway routing.

### 🔹 Core Components
| Service | Description | Port |
|----------|--------------|------|
| **Config Server** | Centralized configuration for all microservices. | 8888 |
| **Discovery Service (Eureka)** | Service registry for inter-service communication. | 8761 |
| **Authentication Service** | Handles user registration and JWT-based login. | 8083 |
| **Employee Service** | Manages employee and department entities. | 8081 |
| **API Gateway Service** | Entry point for all client requests, handles routing and security. | 8080 |
| **Kafka & Zookeeper** | Message broker for async communication (user-created event). | 9092 / 2181 |
| **Kafdrop** | Kafka UI tool for monitoring topics and messages. | 9000 |

---

## 🧩 Key Technologies

- **Java 21**
- **Spring Boot 3.5.7**
- **Spring Cloud 2024.x**
- **Spring Cloud Gateway (MVC)**
- **Spring Security + JWT**
- **Spring Data JPA (Hibernate)**
- **Eureka Discovery & Config Server**
- **Apache Kafka**
- **Docker Compose**
- **Lombok**
- **Swagger / SpringDoc (API Docs)**

---

## ⚙️ Setup Instructions

### 🔸 1. Prerequisites

Before running, ensure you have:

- [Java 21+](https://adoptium.net/)
- [Maven 3.9+](https://maven.apache.org/)
- [Docker & Docker Compose](https://www.docker.com/)
- [Git](https://git-scm.com/)

---

### 🔸 2. Clone the Repository
```bash
git clone https://github.com/Imanuelkarl/employee-management-system.git
cd employee-management-system

mvn clean install -DskipTests
docker-compose up --build
```

---

## 🧰 Configuration Repository

All microservices fetch their configuration from an external Git-based configuration repository:  
🔗 **Config Repo:** [https://github.com/Imanuelkarl/darum-config-repo.git](https://github.com/Imanuelkarl/darum-config-repo.git)

Ensure the Config Server has access to this repo before starting the other services.

---

## 🔐 Default Admin Access

You can log in using the default system admin credentials below:

| Field | Value |
|--------|--------|
| **Login URL** | [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) |
| **Email** | info@darumtest.com |
| **Password** | Password123 |

---

## 💡 Assumptions Made

- Every registered user in the system is considered an **Employee**.
- The **Admin** account is automatically created on startup to manage departments and employee data.
- Each microservice runs independently and communicates via REST or **Kafka events**.
- Authentication tokens (JWT) are verified through the **API Gateway** before routing.
- Employee Service depends on Department data but remains loosely coupled through APIs.
- All microservices share the same database configuration pattern, though each has its own schema or database.

---

## 🚀 Running with Docker

This project includes a `docker-compose.yml` file that sets up all required infrastructure and services automatically.

```bash
docker-compose up --build
```

Once running:
- Visit **http://localhost:8080/swagger-ui/index.html** to test APIs.
- Visit **http://localhost:8761** to view Eureka service registry.
- Visit **http://localhost:9000** for Kafka topic monitoring via Kafdrop.

---

## 🧠 Architecture Decision Highlights

- **Microservices:** To allow independent deployment and scaling.
- **Eureka Discovery:** Simplifies inter-service communication and load balancing.
- **Spring Cloud Config:** Enables centralized and version-controlled configuration management.
- **Kafka:** Used for event-driven communication such as user creation or department updates.
- **Gateway Security:** JWT validation occurs at the entry point for consistency and safety.
- **Swagger Integration:** Each service exposes its own API documentation for testing.

---

## 🧪 Sample API Endpoints

### 🔹 Authentication Service
```

POST /auth/login
```

### 🔹 Employee Service
```
GET /api/employees
POST /api/employees
PUT /api/employees/{id}
GET /api/employees/department/{id}
DELETE /api/employees/{id}
```

### 🔹 Department Service
```
GET /api/departments
POST /api/departments
PUT /api/departments/{id}
DELETE /api/departments/{id}
```

---

## 🧱 CI/CD 
- Currently not available in this version
- Future versions may integrate **GitHub Actions** or **Jenkins** pipelines for automated build and deployment.
- Current setup uses **Maven + Docker Compose** for local development orchestration.

---

## 🧑‍💼 Author

**Emmanuel Dozie**  
📧 [devemmanueldozie@gmail.com](mailto:devemmanueldozie@gmail.com)  
💻 [GitHub Profile](https://github.com/Imanuelkarl)

---

© 2025 Emmanuel Dozie — Employee Management System
