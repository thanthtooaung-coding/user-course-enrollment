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

### Step 5: Run the Application with Docker Compose

Now, instead of running each service manually, you can start the entire stack with a single command from the project root.

```bash
docker compose up --build
```

This command will:

1.  Build a Docker image for each of your six microservices.
2.  Start containers for MySQL and RabbitMQ.
3.  Automatically run the `init.sql` script to set up the databases.
4.  Start all six microservice containers in the correct order based on their dependencies.

### Verifying the Setup

Once all containers are running, you can access the following services:

  - **API Gateway:** `http://localhost:8080` (All API requests go here)
  - **Eureka Dashboard:** `http://localhost:8761` (To see registered services)
  - **RabbitMQ Management UI:** `http://localhost:15672` (user: `guest`, pass: `guest`)
  - **MySQL Database:** Connect using a client on `localhost` at port `3307`.

## API Endpoints

All requests should be sent to the **API Gateway** on port `8080`.

### User Service (`/api/users`)

| Method | Path | Description |
| :--- | :--- | :--- |
| `POST` | `/` | Registers a new user. |
| `GET` | `/` | Retrieves a list of all users. |
| `GET` | `/{id}` | Retrieves a single user by their UUID. |
| `PUT` | `/{id}` | Updates an existing user's details. |
| `DELETE`| `/{id}` | Deletes a user. |

### Course Service (`/api/courses`)

| Method | Path | Description |
| :--- | :--- | :--- |
| `POST` | `/` | Creates a new course. |
| `GET` | `/` | Retrieves a list of all courses. |
| `GET` | `/{id}` | Retrieves a single course by its UUID. |
| `PUT` | `/{id}` | Updates an existing course's details. |
| `DELETE`| `/{id}` | Deletes a course. |

### Enrollment Service (`/api/enrollments`)

| Method | Path | Description |
| :--- | :--- | :--- |
| `POST` | `/` | Enrolls a user in a course. |
| `GET` | `/{id}` | Retrieves a single enrollment record by its UUID. |
| `GET` | `/user/{userId}` | Retrieves all enrollments for a specific user. |
| `DELETE`| `/{id}` | Cancels (deletes) an enrollment. |

### Example: Enrolling a User in a Course

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
