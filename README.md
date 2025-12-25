# LDAP Authentication Wrapper with Email MFA

## Overview

This project is a **standalone, non-web Java 21** that demonstrates a **secure, scalable LDAP authentication wrapper** with **pluggable Multi-Factor Authentication (MFA)** using **email magic links**.

The focus of this Production grade PoC is to demonstrate:

* Clean architecture
* Correct authentication flow design
* Modern Java concurrency (virtual threads)
* Security-aware decision making
* Extensibility for production systems

This is **not a full production system**, but it is intentionally designed so that production hardening can be added **without architectural changes**.

**CLI** has been added as an interface to this wrapper 

---

## Key Capabilities

### Authentication

* Primary authentication using **LDAP / LDAPS**
* Service-account bind + user credential verification
* User Authentication with OpenLDAP (CLI can be used to simulate authentication)

### Multi-Factor Authentication (MFA)

* Email magic-link based MFA
* Signed, time-bound token (10-minute validity)
* Non-blocking authentication using `challengeId`
* MFA verification (CLI can be used to simulate magic-link click)

### Concurrency & Scalability

* Built on **Java 21 virtual threads**
* Handles multiple concurrent users safely
* Blocking I/O (LDAP, SMTP) handled without thread exhaustion
* Stateless service interfaces

### Architecture

* **Hexagonal (Ports & Adapters) architecture**
* Clear separation of:

    * Domain
    * Application orchestration
    * Infrastructure adapters
* External JAR support for MFA providers

---

## Technology Stack

| Area         | Technology                    |
| ------------ | ----------------------------- |
| Language     | Java 21                       |
| Framework    | Spring (Core + LDAP, non-web) |
| LDAP         | OpenLDAP                      |
| MFA          | Email Magic Link              |
| Email        | SMTP                          |
| Tokens       | JWT (signed)                  |
| CLI          | Picocli                       |
| Build        | Maven                         |
| Architecture | Hexagonal / Ports & Adapters  |

---

## Project Structure

```
src/main/java/com/example/auth
├── domain        # Core domain models
├── ports         # Interfaces (LDAP, MFA, Token, Email, etc.)
├── application   # Use cases (authentication, MFA verification)
├── adapters      # LDAP, SMTP, JWT, in-memory stores
└── config        # Spring wiring (non-web)

src/main/java/com/example
└── cli           # CLI commands (login, verify)
```

---

## High-Level Flow

### 1. Primary Authentication

1. User submits username and password via CLI
2. Application binds to LDAP using service account
3. User credentials are validated via LDAP bind
4. On success, MFA is triggered

### 2. MFA (Email Magic Link)

1. A unique `challengeId` is generated
2. A signed, time-bound token is created
3. Token is sent via email
4. Authentication returns `MFA_REQUIRED`

### 3. MFA Verification

1. User submits `challengeId` + token via CLI
2. Token signature and expiry are validated
3. Challenge is atomically consumed
4. An **access token** is issued on success

---

## Concurrency Model

* Uses **Java 21 virtual threads**
* One virtual thread per authentication request
* One virtual thread per verification request
* Blocking LDAP and SMTP calls are safe and efficient
* No reactive programming or callbacks required

This approach keeps the code:

* Readable
* Debuggable
* Scalable

---

## Security Considerations (Scope)

Implemented:

* Secure credential handling
* LDAPS support (truststore-based)
* Signed and time-bound MFA tokens
* Replay-safe MFA challenges
* Audit logging hooks

Intentionally **out of scope for this PoC** (but planned for production):

* Externalized secrets management
* Persistent audit storage
* Distributed rate limiting
* Token revocation / refresh tokens

---

## Testing Strategy

* Unit tests mock LDAP and SMTP (Not added for the POC)
* Domain and application logic fully testable
* Infrastructure-free testing for fast feedback

---

## Extensibility

This design supports:

* Additional MFA providers (SMS, TOTP, WebAuthn)
* Multiple LDAP configurations (failover / load balancing)
* REST or gRPC API exposure
* Externalized state (Redis, DB) without refactoring

---

## Productionization Notes

This PoC intentionally focuses on **architecture and correctness**.

In a production system, the following would be added:

* Secrets manager integration
* Executor bulkheads and back-pressure
* Observability (metrics, tracing)
* Persistent audit logs
* CI/CD policy enforcement

These are **deliberately omitted** here to keep the PoC concise and readable.

---

## Running this project/task

### Prerequisites

* Java 21
* Maven
* Docker

---

### 1. Start OpenLDAP and SMTP server

```bash
docker-compose up -d
```

* LDAP is automatically bootstrapped with demo data.

**Demo user**

```
username: alice
password: secret
email: alice@example.org
```

* SMTP server is automatically bootstrapped with web UI at http://localhost:8025.

---

### 2. Build and Run the Project

```bash
mvn clean package
java -jar target/ldap-mfa-auth-wrapper-1.0.0.jar
```

---

### 3. Login (Triggers MFA)

```bash
Auth CLI started. Type 'help' or 'exit'.
auth> login --username alice --password secret --deviceId abc
```

Expected output:

```
AuthResult{status=MFA_REQUIRED, challengeId=1234}
```

---

### 4. Verify MFA

```bash
auth> verify --challengeId 1234 --deviceId abc --token eyJhbGciOiJIUzI1NiJ9...
```

Expected output:

```
AuthResult{status=SUCCESS, accessToken=eyJhbGciOiJIUzI1NiJ9...}
```

---

### 5. Exit

```bash
auth> exit
```

---
## Why CLI Is Used Instead of REST (Intentional)

* It avoids web-layer boilerplate
* Keeps focus on authentication logic
* Makes flows deterministic and testable
* Demonstrates API-first design

The CLI runs as a long-lived process to simulate a real authentication service. 
MFA challenges are stored in memory for the lifetime of the process.

In production, the services can be exposed via:
* REST
* gRPC
* Messaging

In production, this state (MFA challenges store) would be externalized using:
* Redis or database.

No refactoring required.

## Summary

This PoC demonstrates:

* Correct LDAP authentication patterns
* Secure MFA orchestration
* Modern Java concurrency with virtual threads
* Clean, extensible architecture

It is designed to be **easy to reason about**, **easy to extend**, and **safe under concurrency**, making it a solid foundation for a production authentication service.

---
