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
git clone https://github.com/<your-username>/employee-management-system.git
cd employee-management-system

mvn clean install -DskipTests

docker-compose up --build
