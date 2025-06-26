# User Course Enrollment - A Spring Boot Microservices Project

This repository contains a demonstration of a complete event-driven microservice architecture built for an online learning platform. The system is developed using Java, Spring Boot, Spring Cloud, RabbitMQ for messaging, and MySQL for persistence.

The architecture emphasizes key microservice patterns, including Service Discovery, a centralized API Gateway, asynchronous event-driven communication, and the Database per Service pattern.

## Architecture Diagram

The system is composed of six core microservices that interact through a service registry, an API gateway, and a message broker.

```
+----------+      (1) API Requests       +-----------------+
|          | ------------------------> |                 |      (3) Service Discovery      +---------------------+
| Frontend |                           |   API Gateway   | <-----------------------------> |   Discovery Service |
| (Postman)| <------------------------ |   (Port 8080)   |                                 |       (Eureka)      |
|          |      (2) API Responses    |                 | <-----------------------------> |     (Port 8761)     |
+----------+                           +-------+---------+                                 +----------+----------+
                                               |                                                      ^
                                               | (4) Internal Forwarded Requests                      | (Service Registration)
                                               | (Load Balanced via Eureka)                           |
                                  +------------+------------+------------------------------------------+------------------+
                                  |            |            |                                          |                  |
                                  v            v            v                                          |                  |
                        +---------------+ +---------------+ +---------------+                          |                  |
                        |  User Service | | Course Service| |Enrollment Svc |                          |                  |
                        | (Random Port) | | (Random Port) | | (Random Port) |                          |                  |
                        +-------+-------+ +-------+-------+ +-------+-------+                          |                  |
                                |                 |                 |                                  |                  |
                                | Events Published (UserRegistered, CourseCreated, UserEnrolled)       |                  |
                                +-----------------+-----------------+                                  |                  |
                                                  |                                                    |                  |
                                                  v                                                    |                  |
                                        +-------------------+                                          |                  |
                                        |                   |                                          |                  |
                                        |  RabbitMQ Broker  |                                          |                  |
                                        |                   |                                          |                  |
                                        +---------+---------+                                          |                  |
                                                  |                                                    |                  |
                                                  | (7) Events Consumed                                |                  |
                                                  v                                                    |                  |
                                        +--------------------+                                         |                  |
                                        |                    | <---------------------------------------+                  |
                                        |Notification Service|                                                            |
                                        |   (Random Port)    |                                                            |
                                        +--------------------+

```

## Services Overview

| Service | Port | Database Schema | Purpose |
| :--- | :--- | :--- |:--- |
| **`discovery-service`** | `8761` | N/A | The Eureka service registry that all other services connect to. |
| **`api-gateway`** | `8080` | N/A | The single entry point for all API requests. Routes traffic to other services. |
| **`user-service`** | Random | `users_db` | Manages user registration, profiles, and authentication. |
| **`course-service`** | Random | `courses_db` | Manages the course catalog. |
| **`enrollment-service`**| Random | `enrollments_db` | Manages the relationship between users and courses. |
| **`notification-service`**| Random| N/A | A background worker that listens for events to send notifications. |

## Technology Stack

  - **Framework:** Java 17, Spring Boot, Spring Cloud
  - **Service Discovery:** Netflix Eureka
  - **API Gateway:** Spring Cloud Gateway
  - **Messaging:** RabbitMQ
  - **Database:** MySQL (Persistence via Spring Data JPA)
  - **Build Tool:** Apache Maven

## Prerequisites

  - Java JDK 17 or later
  - Apache Maven 3.8 or later
  - Docker
  - An API client like [Postman](https://www.postman.com/)

## Getting Started

Follow these steps to get the entire system running locally.

### Step 1: Start Infrastructure (MySQL & RabbitMQ)

You can run MySQL and RabbitMQ instances using Docker.

```bash
# Start MySQL
docker run -d --name mysql-db -p 3306:3306 -e MYSQL_ROOT_PASSWORD=password mysql:8.0

# Start RabbitMQ
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

  - **RabbitMQ:** Management UI will be available at `http://localhost:15672` (user: `guest`, pass: `guest`).
  - **MySQL:** Will be available on port `3306` (user: `root`, pass: `password`).

### Step 2: Create Databases & User

Connect to your MySQL instance (using a tool like DBeaver, MySQL Workbench, or the command line) and run the following SQL script to create the user and separate databases for each service.

```sql
CREATE USER 'user_enrollment_service_user'@'localhost' IDENTIFIED BY 'password';

CREATE DATABASE users_db;
CREATE DATABASE courses_db;
CREATE DATABASE enrollments_db;

GRANT ALL PRIVILEGES ON users_db.* TO 'user_enrollment_service_user'@'localhost';
GRANT ALL PRIVILEGES ON courses_db.* TO 'user_enrollment_service_user'@'localhost';
GRANT ALL PRIVILEGES ON enrollments_db.* TO 'user_enrollment_service_user'@'localhost';
```

### Step 3: Important - Fix POM Versions

The current root `pom.xml` has a Spring Boot / Spring Cloud version mismatch. To ensure the project builds and runs correctly, **you must update the `<parent>` and `<properties>` sections in your root `pom.xml`** to use compatible versions.

```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <!-- Use a stable, real version -->
        <version>3.3.1</version>
        <relativePath/>
    </parent>

    <properties>
        <java.version>17</java.version>
        <!-- This version is compatible with Spring Boot 3.3.1 -->
        <spring-cloud.version>2023.1.2</spring-cloud.version>
    </properties>
```

### Step 4: Build the Project

Navigate to the project root and use Maven to build all microservice modules.

```bash
mvn clean install
```

### Step 5: Run the Microservices

Open a new terminal for each service and run them in the following order.

1.  **Start Discovery Service (Required First)**

    ```bash
    java -jar discovery-service/target/discovery-service-*.jar
    ```

    *Wait for it to start, then check the Eureka dashboard at `http://localhost:8761`.*

2.  **Start Business and Worker Services** (Order between these doesn't matter)

    ```bash
    # In a new terminal
    java -jar user-service/target/user-service-*.jar

    # In another new terminal
    java -jar course-service/target/course-service-*.jar

    # In another new terminal
    java -jar enrollment-service/target/enrollment-service-*.jar

    # In another new terminal
    java -jar notification-service/target/notification-service-*.jar
    ```

    *Refresh the Eureka dashboard to see the services appear.*

3.  **Start the API Gateway (Required Last)**

    ```bash
    # In a final terminal
    java -jar api-gateway/target/api-gateway-*.jar
    ```

    *All services should now be running and registered with Eureka.*

## API Endpoints

All requests should be sent to the **API Gateway** on port `8080`.

### 1\. Register a New User

  - **Method:** `POST`
  - **URL:** `http://localhost:8080/api/users/register`
  - **Body (raw, JSON):**
    ```json
    {
        "username": "vinn.dev",
        "email": "vinn.dev@example.com",
        "password": "securepassword123"
    }
    ```

### 2\. Create a New Course

  - **Method:** `POST`
  - **URL:** `http://localhost:8080/api/courses`
  - **Body (raw, JSON):**
    ```json
    {
        "title": "Advanced Microservices with Spring Boot",
        "instructor": "Sir Vinn",
        "price": 99.99
    }
    ```

### 3\. Enroll a User in a Course

> **Note:** First, get the real `userId` and `courseId` from your MySQL database. You must query the database to get the correctly formatted UUID strings.
>
> **Example SQL to get correct UUIDs:**
>
> ```sql
> SELECT BIN_TO_UUID(id) as uuid, username FROM users_db.users;
> SELECT BIN_TO_UUID(id) as uuid, title FROM courses_db.courses;
> ```

  - **Method:** `POST`
  - **URL:** `http://localhost:8080/api/enrollments`
  - **Body (raw, JSON):**
    ```json
    {
        "userId": "YOUR_REAL_USER_ID_FROM_DB",
        "courseId": "YOUR_REAL_COURSE_ID_FROM_DB"
    }
    ```
