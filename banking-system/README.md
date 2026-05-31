# 🏦 Online Banking System

A secure, production-ready **RESTful Banking API** built with **Spring Boot 3**, **Spring Security**, and **JWT authentication**. Features full account management, fund transfers, and a paginated transaction audit trail — with a modern web frontend served directly from the application.

---

## ✨ Features

- 🔐 **JWT-based Authentication** — Stateless, token-secured endpoints
- 👤 **User Registration & Login** — BCrypt password hashing
- 🏧 **Account Management** — Create SAVINGS or CHECKING accounts
- 💸 **Transactions** — Deposit, Withdraw, and Transfer funds
- 🔄 **Atomic Transfers** — `@Transactional` guarantees both debit and credit succeed or neither does
- 📋 **Transaction History** — Paginated audit trail with before/after balances
- 🛡️ **Global Exception Handling** — Structured JSON error responses
- 🌐 **Web Frontend** — Modern HTML/CSS/JS UI served as static files
- 🗃️ **H2 In-Memory Database** — Zero-config for development; swap for PostgreSQL in production

---

## 🛠️ Tech Stack

| Layer        | Technology                            |
|--------------|---------------------------------------|
| Framework    | Spring Boot 3.2.0                     |
| Security     | Spring Security + JWT (jjwt 0.11.5)   |
| Persistence  | Spring Data JPA + Hibernate           |
| Database     | H2 (dev) / PostgreSQL-ready (prod)    |
| Validation   | Jakarta Bean Validation               |
| Boilerplate  | Lombok 1.18.36                        |
| Build Tool   | Maven                                 |
| Java Version | Java 17                               |

---

## 📁 Project Structure

```
banking-system/
├── src/
│   └── main/
│       ├── java/com/banking/
│       │   ├── BankingApplication.java        # Entry point
│       │   ├── config/                        # Security & web configuration
│       │   ├── controller/
│       │   │   ├── AuthController.java        # POST /api/auth/register, /login
│       │   │   ├── AccountController.java     # GET/POST /api/accounts
│       │   │   └── TransactionController.java # Deposit, Withdraw, Transfer, History
│       │   ├── dto/
│       │   │   └── BankingDTOs.java           # All Request/Response DTOs
│       │   ├── entity/
│       │   │   ├── User.java                  # User entity
│       │   │   ├── BankAccount.java           # Account entity (SAVINGS/CHECKING)
│       │   │   └── TransactionHistory.java    # Transaction audit entity
│       │   ├── exception/
│       │   │   └── GlobalExceptionHandler.java # Centralised error handling
│       │   ├── repository/                    # Spring Data JPA repositories
│       │   ├── security/
│       │   │   ├── JwtUtils.java              # Token generation & validation
│       │   │   └── JwtAuthFilter.java         # JWT request filter
│       │   └── service/
│       │       ├── AuthService.java           # Registration & login logic
│       │       └── BankingService.java        # Core banking business logic
│       └── resources/
│           ├── application.properties         # App configuration
│           └── static/
│               └── index.html                 # Web frontend
└── pom.xml
```

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.6+

### Run the Application

```bash
# Clone the repository
git clone https://github.com/your-username/banking-system.git
cd banking-system/banking-system

# Build and run
mvn spring-boot:run
```

The application starts on **http://localhost:8081**

### Access the Web UI

Open your browser and navigate to:
```
http://localhost:8081
```

### Access the H2 Database Console (development only)

```
http://localhost:8081/h2-console
```

| Field    | Value                                         |
|----------|-----------------------------------------------|
| JDBC URL | `jdbc:h2:mem:bankingdb`                       |
| Username | `sa`                                          |
| Password | *(leave blank)*                               |

---

## 🔌 REST API Reference

All protected endpoints require the `Authorization: Bearer <token>` header.

### Authentication

| Method | Endpoint              | Description          | Auth Required |
|--------|-----------------------|----------------------|---------------|
| POST   | `/api/auth/register`  | Register a new user  | ❌            |
| POST   | `/api/auth/login`     | Login & get JWT      | ❌            |

#### Register — `POST /api/auth/register`
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "securePass123"
}
```

#### Login — `POST /api/auth/login`
```json
{
  "username": "john_doe",
  "password": "securePass123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "type": "Bearer",
    "userId": 1,
    "username": "john_doe",
    "email": "john@example.com"
  }
}
```

---

### Accounts

| Method | Endpoint              | Description                     |
|--------|-----------------------|---------------------------------|
| POST   | `/api/accounts`       | Create a new bank account       |
| GET    | `/api/accounts`       | List all accounts for the user  |
| GET    | `/api/accounts/{id}`  | Get a specific account          |

#### Create Account — `POST /api/accounts`
```json
{
  "accountType": "SAVINGS",
  "initialDeposit": 1000.00
}
```

> **Account types:** `SAVINGS` | `CHECKING`

---

### Transactions

| Method | Endpoint                              | Description                   |
|--------|---------------------------------------|-------------------------------|
| POST   | `/api/accounts/{id}/deposit`          | Deposit funds                 |
| POST   | `/api/accounts/{id}/withdraw`         | Withdraw funds                |
| POST   | `/api/accounts/{id}/transfer`         | Transfer to another account   |
| GET    | `/api/accounts/{id}/transactions`     | Get paginated transaction history |

#### Deposit — `POST /api/accounts/{id}/deposit`
```json
{
  "amount": 500.00,
  "description": "Monthly salary"
}
```

#### Withdraw — `POST /api/accounts/{id}/withdraw`
```json
{
  "amount": 200.00,
  "description": "ATM withdrawal"
}
```

#### Transfer — `POST /api/accounts/{id}/transfer`
```json
{
  "targetAccountNumber": "550e8400-e29b-41d4-a716-446655440000",
  "amount": 100.00,
  "description": "Rent payment"
}
```

#### Transaction History — `GET /api/accounts/{id}/transactions?page=0&size=20`

**Response:**
```json
{
  "success": true,
  "message": "Transaction history retrieved",
  "data": {
    "content": [
      {
        "id": 1,
        "type": "DEPOSIT",
        "amount": 500.00,
        "balanceBefore": 1000.00,
        "balanceAfter": 1500.00,
        "description": "Monthly salary",
        "timestamp": "2026-05-31T14:30:00"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

---

## 🛡️ Security Design

- **Stateless JWT Auth** — No server-side sessions; each request is self-contained.
- **Token Identity** — Protected endpoints use `@AuthenticationPrincipal` to extract the user from the validated JWT. User-supplied usernames are never trusted.
- **BCrypt Passwords** — All passwords are hashed before storage.
- **Account Ownership** — Every account operation verifies the requesting user owns the account.
- **No Overdraft** — Withdrawal and transfer operations enforce sufficient-balance checks.
- **Atomic Transfers** — `@Transactional` ensures debit + credit are both committed or both rolled back.

---

## ⚙️ Configuration

Key settings in `src/main/resources/application.properties`:

```properties
server.port=8081

# JWT
jwt.secret=<your-256-bit-secret>
jwt.expiration.ms=86400000   # 24 hours

# H2 console (disable in production)
spring.h2.console.enabled=true
```

> ⚠️ **Production Checklist:**
> - Replace H2 with PostgreSQL / MySQL
> - Set `jwt.secret` via an environment variable, never hardcode it
> - Set `spring.h2.console.enabled=false`
> - Set `spring.jpa.show-sql=false`

---

## 📊 Data Model

```
User ──< BankAccount ──< TransactionHistory
```

- One **User** can own many **BankAccounts**
- Each **BankAccount** maintains a full **TransactionHistory** audit trail
- Each transaction records `balanceBefore` and `balanceAfter` for complete auditability
- Account numbers are auto-generated **UUIDs** (globally unique, not guessable)
- All monetary values use **BigDecimal** (never `float`/`double`) to avoid precision loss

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -m 'Add my feature'`
4. Push to the branch: `git push origin feature/my-feature`
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License.

---

*Built as part of the ShadowFox Java Development Internship Program.*
