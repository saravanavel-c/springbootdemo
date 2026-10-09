# Digital Banking Application

A full-stack digital banking platform built with **Spring Boot** on the backend and **React** on the frontend, secured with **Keycloak** for OAuth2/JWT authentication. The application supports role-based access control with four distinct roles: `ADMIN`, `MAKER`, `CHECKER`, and `USER`.

---

## Architecture Overview

```
┌─────────────────────────────────────────────────┐
│                  NGINX (Port 80)                │
│  ┌──────────────┐    ┌──────────────────────┐   │
│  │  React SPA   │    │  Spring Boot API     │   │
│  │  /           │    │  /api/**             │   │
│  │  (Port 80)   │    │  /health             │   │
│  │              │    │  (Port 8080)         │   │
│  └──────────────┘    └──────────────────────┘   │
└─────────────────────────────────────────────────┘
                          │
                   ┌──────┴───────┐
                   │  Keycloak    │
                   │  (Port 8081) │
                   └──────┬───────┘
                          │
                   ┌──────┴───────┐
                   │  PostgreSQL  │
                   │  (Port 5432) │
                   └──────────────┘
```

---

## Tech Stack

### Backend
| Technology | Version | Purpose |
|---|---|---|
| Spring Boot | 4.0.7 | Application framework |
| Spring Web MVC | — | REST API |
| Spring Data JPA | — | Database ORM |
| Spring Security | — | Security layer |
| Spring OAuth2 Resource Server | — | JWT validation |
| Spring Validation | — | Input validation |
| PostgreSQL | — | Relational database |
| Lombok | — | Boilerplate reduction |
| Java | 21 | Runtime |
| Maven | — | Build tool |

### Frontend
| Technology | Version | Purpose |
|---|---|---|
| React | 19.x | UI framework |
| Vite | 8.x | Dev server & bundler |
| keycloak-js | 26.x | Keycloak OIDC client |
| Oxlint | — | Fast JS/JSX linter |

### Infrastructure
| Technology | Purpose |
|---|---|
| Nginx | Reverse proxy / API gateway |
| Keycloak | Identity & Access Management (IAM) |
| PostgreSQL | Persistent data store |

---

## Features

### Role-Based Access Control
| Feature | ADMIN | MAKER | CHECKER | USER |
|---|:---:|:---:|:---:|:---:|
| View Dashboard | ✅ | ✅ | ✅ | ✅ |
| Manage Customers | ✅ | ❌ | ❌ | ❌ |
| View Accounts | ✅ | ✅ | ✅ | ✅ |
| Create Transactions | ✅ | ✅ | ❌ | ❌ |
| Approve Transactions | ✅ | ❌ | ✅ | ❌ |
| Manage Beneficiaries | ✅ | ✅ | ✅ | ✅ |
| Manage Consents | ✅ | ✅ | ✅ | ✅ |

### Core Modules
- **Dashboard** – Summary view with key metrics
- **Customers** – Customer lifecycle management (Admin only)
- **Accounts** – Bank account management and selection
- **Transactions** – Transaction history and fund transfers with maker-checker workflow
- **Beneficiaries** – Manage payee/beneficiary records
- **Consents** – Data consent and permission management
- **Audit Logs** – Full audit trail of all operations
- **Profile** – User profile management

---

## Project Structure

```
springbootdemo/
├── src/
│   └── main/
│       ├── java/com/example/springbootdemo/
│       │   ├── SpringbootdemoApplication.java   # Entry point
│       │   ├── config/
│       │   │   ├── SecurityConfig.java          # OAuth2 & RBAC rules
│       │   │   └── WebConfig.java               # CORS configuration
│       │   ├── controller/                      # REST controllers
│       │   │   ├── AccountController.java
│       │   │   ├── ApiController.java
│       │   │   ├── AuditLogController.java
│       │   │   ├── BeneficiaryController.java
│       │   │   ├── ConsentController.java
│       │   │   ├── CustomerController.java
│       │   │   ├── DashboardController.java
│       │   │   ├── ProfileController.java
│       │   │   ├── TransactionController.java
│       │   │   └── TransferController.java
│       │   ├── dto/                             # Data Transfer Objects
│       │   ├── entity/                          # JPA Entities
│       │   │   ├── Account.java
│       │   │   ├── AuditLog.java
│       │   │   ├── Beneficiary.java
│       │   │   ├── Consent.java
│       │   │   ├── Customer.java
│       │   │   └── Transaction.java
│       │   ├── exception/                       # Global exception handling
│       │   ├── repository/                      # Spring Data JPA repositories
│       │   └── service/                         # Business logic layer
│       └── resources/
│           └── application.properties           # App configuration
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   │   ├── Dashboard.jsx
│   │   │   ├── CustomerSection.jsx
│   │   │   ├── AccountSection.jsx
│   │   │   ├── TransactionSection.jsx
│   │   │   ├── BeneficiarySection.jsx
│   │   │   └── ConsentSection.jsx
│   │   ├── App.jsx                              # Root component & routing
│   │   ├── api.js                               # API client utilities
│   │   ├── keycloak.js                          # Keycloak initialization
│   │   └── main.jsx                             # React entry point
│   ├── package.json
│   └── vite.config.js
├── nginx/
│   └── nginx.conf                               # Reverse proxy config
└── pom.xml
```

---

## ⚙️ Prerequisites

- **Java 21** or later
- **Maven 3.9+**
- **Node.js 20+** and **npm**
- **PostgreSQL 15+**
- **Keycloak 26+** (running instance)

---

## 🛠️ Setup & Configuration

### 1. PostgreSQL Database

Create the database:
```sql
CREATE DATABASE springdemo;
```

### 2. Keycloak Setup

1. Start Keycloak on port `8081`
2. Create a realm named **`banking-realm`**
3. Create a client for the backend resource server
4. Create roles: `ADMIN`, `MAKER`, `CHECKER`, `USER`
5. Assign roles to users as required

### 3. Backend Configuration

Update `src/main/resources/application.properties`:

```properties
spring.application.name=springbootdemo

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/springdemo
spring.datasource.username=<your-db-username>
spring.datasource.password=<your-db-password>

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Keycloak / OAuth2
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8081/realms/banking-realm
```

### 4. Frontend Configuration

Update `frontend/.env` with your Keycloak details:

```env
VITE_KEYCLOAK_URL=http://localhost:8081
VITE_KEYCLOAK_REALM=banking-realm
VITE_KEYCLOAK_CLIENT_ID=<your-client-id>
```

---

## Running the Application

### Backend

```bash
# From the project root
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

The dev server will be available at `http://localhost:5173`.

### With Nginx (Production-like)

Configure and start Nginx using the provided `nginx/nginx.conf`. This routes:

| Path | Target |
|---|---|
| `/` | React frontend (port 80) |
| `/api/**` | Spring Boot backend (port 8080) |
| `/health` | Spring Boot health endpoint (port 8080) |

---

## Security

- All API endpoints are secured via **JWT Bearer tokens** issued by Keycloak
- The backend validates tokens against the Keycloak JWKS endpoint
- Role claims (`realm_access.roles`) from the JWT are used for authorization
- The frontend uses `keycloak-js` to handle the OIDC login flow and token refresh automatically

---

## Running Tests

```bash
# From the project root
./mvnw test
```

---

## Building for Production

### Backend JAR

```bash
./mvnw clean package -DskipTests
# Output: target/springbootdemo-0.0.1-SNAPSHOT.jar
java -jar target/springbootdemo-0.0.1-SNAPSHOT.jar
```

### Frontend Static Build

```bash
cd frontend
npm run build
# Output: frontend/dist/
```

---

## API Endpoints

| Method | Endpoint | Description | Role Required |
|---|---|---|---|
| `GET` | `/health` | Health check | Public |
| `GET` | `/api/dashboard` | Dashboard stats | Authenticated |
| `GET` | `/api/customers` | List customers | ADMIN |
| `POST` | `/api/customers` | Create customer | ADMIN |
| `GET` | `/api/accounts` | List accounts | Authenticated |
| `POST` | `/api/accounts` | Create account | ADMIN, MAKER |
| `GET` | `/api/transactions` | List transactions | Authenticated |
| `POST` | `/api/transactions` | Initiate transaction | MAKER |
| `PUT` | `/api/transactions/{id}` | Approve/reject transaction | CHECKER |
| `GET` | `/api/beneficiaries` | List beneficiaries | Authenticated |
| `POST` | `/api/beneficiaries` | Add beneficiary | Authenticated |
| `GET` | `/api/consents` | List consents | Authenticated |
| `GET` | `/api/audit-logs` | View audit trail | ADMIN |
| `GET` | `/api/profile` | Current user profile | Authenticated |
| `POST` | `/api/transfer` | Fund transfer | MAKER |

---

## License

This project is for demonstration and learning purposes.