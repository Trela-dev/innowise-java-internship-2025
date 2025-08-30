
# 📚 Library3000App

**Library3000** has been refactored into a robust **microservices-based architecture**, with each responsibility encapsulated in its own containerized service. Configuration is centralized, services are discoverable, and all communication flows through a secured gateway.
![img_4.png](img_4.png)
---

## ✨ Features

✅ Retrieve all books
✅ Retrieve a book by ID
✅ Add a new book
✅ Update an existing book
✅ Delete a book
✅ Search books by keyword (Spring JPA query)
✅ Upload and download book covers using **GridFS** (supports JPG and PNG)
✅ Global exception handling
✅ **Spring Security with JWT authentication**
✅ **Role-based access control (USER, ADMIN)**
✅ **Test endpoint accessible only by ADMIN**
✅ **User registration and login with JWT token generation**

---

## 🛠 Requirements

* Java 21
* Gradle
* Docker & Docker Compose
* Postman (for API testing)

---

## 🚀 Setup & Running

### 1️⃣ Start the Database

Run the following command in the project root to start application in docker containers (wait for all services to be up it can take a few minutes) 
after starting you can visit the application at `http://localhost:8500` and see if all services are registered and healthy and then you can start using the application.:

```sh
docker-compose up -d
```
![img_5.png](img_5.png)
---

## 🐳 Dev-Friendly Docker Compose

For easier development, the project includes a **`docker-compose.dev.yaml`** file.

### 🎯 Purpose

Use it when you want to:

* Run only core services (e.g., PostgreSQL, MongoDB, Consul)
* Start other microservices manually in your IDE

### 🚀 How to Use

```sh
docker compose -f docker-compose.dev.yaml up -d
```

This setup speeds up development and gives you full control over which services run locally.

---

📌 **Liquibase** will automatically initialize the database schema and insert sample data.

---
### 🔄 Services Overview

| Service       | Port | Description                                                                                    |
| ------------- | ---- |------------------------------------------------------------------------------------------------|
| Gateway       | 8080 | Entry point for all API requests (Spring Cloud Gateway) + Spring Security to verify JWT tokens |
| Auth Service  | 8081 | Handles JWT token generation                                                                   |
| Book Service  | 8082 | Manages books, CRUD operations, and secured endpoints                                          |
| Image Service | 8083 | Uploading/downloading book cover images using GridFS                                           |
| Config Server | 8888 | Centralized configuration server (fetches from GitHub repo)                                    |
| Consul        | 8500 | Service discovery and health checks                                                            |

---
## ⚙️ Configuration Server

The application uses a centralized **Spring Cloud Config Server** that retrieves external configurations from the following GitHub repository:

📁 Config Repository:
`https://github.com/TrelaDev-Innowise/library3000-microservices-config`

All services automatically fetch their configuration from this repository upon startup.

---
## 🔐 Authentication & Authorization

The application uses **Spring Security** with **JWT** tokens for authentication and role-based authorization.
![img_2.png](img_2.png)
![img_3.png](img_3.png)

### Available Test Users

| Username | Password | Roles |
| -------- | -------- | ----- |
| admin    | admin    | ADMIN |
| user1    | user1    | USER  |
| user2    | user2    | USER  |
| user3    | user3    | USER  |
| user4    | user4    | USER  |
| user5    | user5    | USER  |

---

## 📡 Authentication Endpoints

### 🔑 Login (Get JWT Token)

```
POST http://localhost:8080/api/auth/login
```

**Request Body (JSON):**

```json
{
  "username": "admin",
  "password": "admin"
}
```

**Response:**

* JWT token will be returned in the **Authorization** header as `Bearer <token>`.
* Use this token to authorize subsequent requests.

---

### 📝 Register a New User

```
POST http://localhost:8080/api/auth/register
```

**Request Body (JSON):**

```json
{
  "username": "newuser",
  "password": "newpassword"
}
```

**Response:**

* New user is created with default USER role.
* Then login with the new credentials to get a JWT token.

---

### 🔒 Using the JWT Token

For secured endpoints (including all book-related APIs):

* In Postman, go to the **Authorization** tab
* Choose **Bearer Token** type
* Paste the JWT token (without the `Bearer` prefix)
* Make your API requests
![img_1.png](img_1.png)

---

## 📂 Project Structure

```
Library3000/
├── auth-service/         # Handles login, registration, JWT
├── book-service/         # Handles book management
├── image-service/        # Handles cover image upload/download (GridFS)
├── gateway-service/      # Spring Cloud Gateway for routing and filtering
├── config-server/        # Central config server (Spring Cloud Config)
├── docker-compose.yaml   # Starts the entire microservice ecosystem
├── docker-compose.dev.yaml
├── README.md
```

---

## 📡 API Endpoints (via Postman)

### 📖 Get All Books

```
GET http://localhost:8080/api/books
```

### 📖 Get Book by ID

```
GET http://localhost:8080/api/books/{id}
```

Example:

```
GET http://localhost:8080/api/books/2
```

### ➕ Add a New Book

```
POST http://localhost:8080/api/books
```

**Request Body (JSON):**

```json
{
  "title": "Murder on the Orient Express",
  "description": "Hercule Poirot investigates a murder on a snowbound train.",
  "pages": 256,
  "rating": 4.30,
  "authors": [
    {"name": "Agatha Christie"},
    {"name": "Alex Michaelides"}
  ],
  "genre": {
    "name": "Mystery"
  }
}
```

### ✏️ Update a Book

```
PUT http://localhost:8080/api/books/{id}
```

**Request Body (JSON):**

```json
{
  "title": "UPDATE",
  "description": "UPDATE",
  "pages": 100,
  "rating": 5.00,
  "authors": [
    {"name": "UPDATED AUTHOR"},
    {"name": "UPDATEDAUTHOR2"}
  ],
  "genre": {
    "name": "Mystery"
  }
}
```

### 🔍 Search Books by Keyword

```
GET http://localhost:8080/api/books/search?keyword=tolkien
```

### ❌ Delete a Book

```
DELETE http://localhost:8080/api/books/{id}
```

Example:

```
DELETE http://localhost:8080/api/books/1
```

---

## 📸 Book Cover Upload & Download (GridFS)


### ➕ Upload Book Cover

Upload a book cover image (supported formats: **JPG**, **PNG**) using:

**🔒 Access Restricted:**

📌 **This endpoint is accessible only to users with the `ADMIN` role.**
Make sure you are logged in as an **admin user** before making this request.

**✅ Admin Credentials:**

```json
{
  "username": "admin",
  "password": "admin"
}
```


```
POST http://localhost:8080/api/books/{id}/cover
```
![img.png](img.png)
**Request:**

* Use `form-data` in Postman.
* Add a key named `coverImage` with the image file you want to upload as the value.

Example:

| Key        | Type | Value               |
| ---------- | ---- | ------------------- |
| coverImage | File | your\_cover.jpg/png |

---

### 📥 Download Book Cover

Retrieve the cover image for a book using:

```
GET http://localhost:8080/api/books/{id}/cover
```

This endpoint returns the image binary data with appropriate content-type (`image/jpeg` or `image/png`) so it can be displayed or saved.

---

## ✅ Tests Included

The project includes both **integration** and **unit tests** to ensure reliability and correctness of key functionalities.

### 🔬 Integration Tests (`controller` package)

Integration tests verify that the application components work together as expected using real HTTP requests and a containerized PostgreSQL instance via **Testcontainers**.

Examples:

* `AuthControllerIT`: Tests user registration and input validation.
* `BookControllerIT`: Covers book creation, retrieval by ID, and listing all books.

These tests use `TestRestTemplate` and start the application on a random port.

### ⚙️ Unit Tests (`service` package)

Unit tests validate individual service logic with mocked dependencies using **JUnit 5** and **Mockito**.

Examples:

* `BookServiceTest`: Verifies authors and genres are properly processed before saving a book.
* `GenreServiceTest`: Ensures genre retrieval works as expected.
* `UserServiceTest`: Validates user registration rules and handles duplicate usernames or invalid inputs.

All tests are located under the `src/test/java/dev/trela/` directory and can be run using:

```sh
./gradlew test
```

Test results will be available in the `build/reports/tests/test/index.html` file after execution.

---

## 🛡️ Circuit Breaker (Resilience4j)

The application uses **Resilience4j** to implement circuit breakers for fault tolerance and graceful degradation when dependent services become unavailable.

### 🔌 Testing Circuit Breaker Functionality

To simulate a service failure and observe circuit breaker behavior, you can temporarily **stop one of the microservices**, such as the **Auth Service**.

#### 🔁 Example Scenario: Auth Service Failure

1. **Stop the Auth Service**:

2. **Attempt to login** via the Gateway:

   ```http
   POST http://localhost:8080/api/auth/login
   ```

   **Expected Response:**

   ![img_6.png](img_6.png)

   This response is returned by the circuit breaker's fallback mechanism when the Auth Service is unavailable.

3. **Check Circuit Breaker State**:
   You can monitor the state of the circuit breaker using the Spring Boot actuator endpoint:

   ```
   GET http://localhost:8080/actuator/health
   ```

   This will display the health status of the gateway and include information about the circuit breakers — for example, whether the circuit is **OPEN**, **CLOSED**, or **HALF\_OPEN**.
![img_7.png](img_7.png)

---

## ⚙️ Technologies Used

* **Java 21** – Primary programming language
* **Spring Boot** – Microservices foundation (REST, DI, AOP)
* **Spring Security with JWT** – Role-based authentication and authorization
* **Spring Cloud Gateway** – API gateway with circuit breakers and load balancing
* **Spring Cloud Config Server** – Externalized configuration management
* **Spring Data JPA** – Database abstraction (Book & Auth services)
* **PostgreSQL** – Main relational database
* **MongoDB GridFS** – Storage for book cover images (Image Service)
* **Liquibase** – Automated database migrations
* **Consul** – Service discovery and service registry
* **Circuit Breaker (Resilience4j)** – Fault tolerance and graceful fallback for failing services
* **Load Balancer (Spring Cloud LoadBalancer)** – Client-side load balancing between service instances
* **Docker & Docker Compose** – Containerized deployment of all services
* **Testcontainers** – Integration testing with containerized PostgreSQL
* **JUnit 5 & Mockito** – Unit and integration testing
* **Postman** – API testing tool
* **Lombok** – Reduces boilerplate code (e.g., constructors, getters/setters)

---
📌 Make sure Docker is running before starting the application. All API endpoints can be tested using the Postman collection located in the `Postman/` directory.

🗂️ The collection of requests is included in the Postman/ folder — import it into Postman to quickly access and test all endpoints.

---

