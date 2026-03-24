# PC+ Backend API Documentation

**Version:** 1.1  
**Base URL:** `http://localhost:8080/api`  
**Course:** CSC 340  
**Team Scope:** Customer + Provider (Publisher) use cases

---

## 1. Overview
This backend milestone implements the project API using:
- Java + Spring Boot
- Spring Data JPA
- PostgreSQL (Neon)
- Layered architecture with `@Entity`, `@Repository`, `@Service`, and `@RestController`

SysAdmin endpoints are intentionally out of team scope for this submission.

---

## 2. UML Class Diagram
- `docs/uml-class-diagram.png`

---

## 3. Neon Configuration (No Password Leaks)
`src/main/resources/application.properties` is committed with placeholders and supports env vars.

Set environment variables locally:

```powershell
$env:NEON_DB_URL="jdbc:postgresql://YOUR_NEON_HOST/neondb?sslmode=require"
$env:NEON_DB_USERNAME="neondb_owner"
$env:NEON_DB_PASSWORD="YOUR_NEON_PASSWORD"
```

Then run:

```powershell
mvn spring-boot:run
```

Optional local override file:
- `src/main/resources/application-local.properties` (gitignored)

---

## 4. API Endpoints

### 4.1 Auth Endpoints
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/auth/signup` | Generic signup (role=`customer` or `publisher`) |
| `POST` | `/auth/login` | Login and return JWT |
| `POST` | `/auth/reset-password` | Reset password by PIN |
| `GET` | `/auth/me` | Current user profile |
| `PATCH` | `/auth/avatar` | Update avatar |

### 4.2 Customer Actor Endpoints (Milestone-Mapped)
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/customers` | Create customer profile |
| `PUT` | `/customers/profile` | Modify customer profile |
| `GET` | `/customers/services` | View available services |
| `POST` | `/customers/subscriptions` | Subscribe to a service (`gameId`) |
| `GET` | `/customers/subscriptions` | View subscribed/purchased services |
| `POST` | `/customers/services/{gameId}/reviews` | Write review for subscribed service |

### 4.3 Provider Actor Endpoints (Milestone-Mapped)
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/providers/profile` | Create provider profile |
| `PUT` | `/providers/profile` | Modify provider profile |
| `POST` | `/providers/services` | Create service listing |
| `GET` | `/providers/statistics` | View customer statistics for provider services |
| `GET` | `/providers/reviews` | View reviews for provider services |
| `POST` | `/providers/reviews/{reviewId}/reply` | Reply to review (`gameId`, `reply`) |

### 4.4 Core Catalog/Commerce Endpoints
These are still available for frontend integration and detailed testing.

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/games` | All live games/services |
| `GET` | `/games/{id}` | Service details |
| `GET` | `/games/top-sellers` | Top services |
| `GET` | `/games/newest` | Newest services |
| `GET` | `/games/on-sale` | Discounted services |
| `GET` | `/games/search?q=` | Search services |
| `GET` | `/cart` | View cart |
| `POST` | `/cart` | Add to cart |
| `DELETE` | `/cart/{gameId}` | Remove one item |
| `DELETE` | `/cart` | Clear cart |
| `GET` | `/library` | Purchased library |
| `GET` | `/library/owns/{gameId}` | Ownership check |
| `POST` | `/library/checkout` | Checkout cart |
| `POST` | `/library/buy/{gameId}` | Buy one service |
| `GET` | `/games/{gameId}/reviews` | List reviews |
| `POST` | `/games/{gameId}/reviews` | Submit review |
| `PUT` | `/games/{gameId}/reviews/{reviewId}/reply` | Provider reply |
| `DELETE` | `/games/{gameId}/reviews/{reviewId}` | Delete review |

---

## 5. Use Case Mapping

### Customer Use Cases
| Use Case | Endpoint Mapping |
|---|---|
| Create customer profile | `POST /customers` |
| Modify customer profile | `PUT /customers/profile` |
| View available services | `GET /customers/services` |
| Subscribe to available services | `POST /customers/subscriptions` |
| Write review for subscribed service | `POST /customers/services/{gameId}/reviews` |

### Provider Use Cases
| Use Case | Endpoint Mapping |
|---|---|
| Create/modify provider profile | `POST /providers/profile`, `PUT /providers/profile` |
| Create services | `POST /providers/services` |
| View customer statistics | `GET /providers/statistics` |
| View reviews for provider services | `GET /providers/reviews` |
| Reply to reviews | `POST /providers/reviews/{reviewId}/reply` |

---

## 6. Presentation Demo Checklist
1. Start backend with Neon credentials set through env vars or local override file.
2. In Postman/EchoAPI, run Customer actor endpoints.
3. In Postman/EchoAPI, run Provider actor endpoints.
4. Show persistence in Neon tables: `users`, `games`, `purchases`, `reviews`.
5. Show UML from `backend-api/docs/uml-class-diagram.png`.
