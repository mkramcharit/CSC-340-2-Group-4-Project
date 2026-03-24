# PC+ Game Store — Setup Guide

**Team:** Michael Ramcharitar, Eddy Arriaga-Barrientos  
**Course:** CSC 340 — Group 4

---

## How the Architecture Works

```
Browser (HTML/JS frontend)
        ↕  REST API calls (JSON)
Spring Boot backend (localhost:8080)
        ↕  JDBC / JPA
Neon.tech PostgreSQL (cloud database)
```

When the Spring Boot backend is **running and connected to Neon**:
- Login/signup/cart/library → all saved to Neon.tech PostgreSQL
- JWT tokens used for authentication

When the Spring Boot backend is **not running** (offline/prototype mode):
- auth.js automatically falls back to localStorage only
- Accounts exist only in the browser — **nothing saved to Neon**
- This is why login "works" even without the backend running

---

## ⚠️ Common Mistake: Why Nothing Shows Up in Neon

If you can log in but **nothing appears in the Neon tables**, it means:
- The Spring Boot backend is not running, OR
- `application.properties` still has the placeholder `YOUR_NEON_HOST` values

Fix: follow the steps below completely.

---

## Step 1 — Fix the Neon Data (Run This First)

Your `users` table may have the demo customer account with a plain-text password
(which Spring Security cannot verify). Run this SQL in the **Neon SQL Editor** to fix it.

1. Go to **[console.neon.tech](https://console.neon.tech)**
2. Click **SQL Editor** in the left sidebar
3. Paste and run the entire contents of **`pcplus-backend/neon-fix.sql`**

You should see 2 rows returned with `hash_prefix = '$2b$10$'` confirming both accounts are correct.

---

## Step 2 — Get Your Neon Connection String

1. Go to **[console.neon.tech](https://console.neon.tech)**
2. Open your project
3. Click **"Connection Details"** at the top of the dashboard
4. Select the **"Java"** tab (or copy from the connection string)

Your connection string looks like:
```
postgresql://neondb_owner:YourPassword@ep-something-123.us-east-2.aws.neon.tech/neondb?sslmode=require
```

Break it into these parts:
| What | Where it goes | Example |
|---|---|---|
| `ep-something-123.us-east-2.aws.neon.tech` | `YOUR_NEON_HOST` | `ep-cool-fog-62658539.us-east-2.aws.neon.tech` |
| `neondb_owner` | `YOUR_NEON_USERNAME` | `neondb_owner` |
| `YourPassword` | `YOUR_NEON_PASSWORD` | `AbCdEf1234` |

---

## Step 3 — Configure the Backend

Open `pcplus-backend/src/main/resources/application.properties` and fill in your values:

```properties
spring.datasource.url=jdbc:postgresql://YOUR_NEON_HOST/neondb?sslmode=require
spring.datasource.username=YOUR_NEON_USERNAME
spring.datasource.password=YOUR_NEON_PASSWORD
```

Real example (your values will be different):
```properties
spring.datasource.url=jdbc:postgresql://ep-cool-fog-62658539.us-east-2.aws.neon.tech/neondb?sslmode=require
spring.datasource.username=neondb_owner
spring.datasource.password=AbCdEf1234xyz
```

Also update the CORS origin to match how you're serving the frontend:

| How you open the frontend | `pcplus.cors.allowed-origin` value |
|---|---|
| `python3 -m http.server 3000` | `http://localhost:3000` |
| VS Code Live Server | `http://localhost:5500` |
| Double-click the HTML file | `null` |

---

## Step 4 — Run the Backend

### Prerequisites
- **Java 21** — download from [adoptium.net](https://adoptium.net)
- **Maven 3.9+** — download from [maven.apache.org](https://maven.apache.org)

Check you have them:
```bash
java --version    # should say 21.x.x
mvn --version     # should say 3.9.x
```

### Start the server
```bash
cd pcplus-backend
mvn spring-boot:run
```

You should see in the console:
```
[PC+] Seeded demo account: customer (customer)
[PC+] Seeded demo account: publisher (publisher)
Started PcPlusApplication in 3.2 seconds
```

If you see those seed lines, the backend is connected to Neon and the accounts exist.

The API is now available at **`http://localhost:8080`**

### Build a runnable JAR (optional)
```bash
mvn clean package -DskipTests
java -jar target/pcplus-api-1.0.0.jar
```

---

## Step 5 — Serve the Frontend

Open a **second terminal** (keep the backend running in the first).

```bash
# From the pcplus-frontend/ folder:
python3 -m http.server 3000
```

Then visit: **http://localhost:3000/customer/customer-home.html**

Or use VS Code Live Server — right-click any HTML file → "Open with Live Server".

**Do not open HTML files by double-clicking** (file:// origin causes CORS issues unless you set `pcplus.cors.allowed-origin=null` in application.properties).

---

## Step 6 — Verify Everything Works

1. Go to `http://localhost:3000/customer/login.html`
2. Log in with `customer` / `customer`
3. Go to **[console.neon.tech](https://console.neon.tech)** → Tables → `users`
4. You should see the customer row with `password_hash` starting with `$2b$10$`

For new signups:
1. Sign up with a real email (e.g. `test@gmail.com`), password, PIN
2. Check Neon → Tables → `users` — the new row should appear immediately

---

## Demo Accounts

| Role | Email | Password | PIN |
|---|---|---|---|
| Customer | `customer` | `customer` | `0000` |
| Publisher | `publisher` | `publisher` | `0000` |

These are automatically created/verified every time the Spring Boot app starts (`DataInitializer.java`).

---

## Troubleshooting

**"Nothing appears in Neon after signing up"**
→ Backend is not running. Open a terminal, run `mvn spring-boot:run` in `pcplus-backend/`.

**"Login says invalid credentials" on the website**
→ Run `neon-fix.sql` in the Neon SQL Editor. The password hash was plain text.

**Backend crashes on startup with "Connection refused" or "FATAL: password authentication failed"**
→ `application.properties` has wrong Neon credentials. Double-check your host/username/password from the Neon dashboard Connection Details page.

**Browser console shows CORS error**
→ `pcplus.cors.allowed-origin` in `application.properties` doesn't match where you're serving the frontend. Match the URL in the table in Step 3 above.

**"Failed to load dashboard data" on publisher pages**
→ The Spring Boot server isn't running. Check the terminal where you ran `mvn spring-boot:run`.

**JWT expired / "Please log in again"**
→ Tokens expire after 24 hours. Log out and log in again.

**Tables in Neon are missing / schema error**
→ Re-run `schema.sql` in the Neon SQL Editor, then run `neon-fix.sql`.

---

## Project File Structure

```
pc-plus-complete/
├── pcplus-backend/
│   ├── src/main/java/com/pcplus/
│   │   ├── PcPlusApplication.java       ← Spring Boot entry point
│   │   ├── config/
│   │   │   ├── DataInitializer.java     ← Seeds demo accounts on startup
│   │   │   └── SecurityConfig.java      ← JWT + CORS configuration
│   │   ├── controller/                  ← REST endpoints (Auth, Game, Cart, etc.)
│   │   ├── model/                       ← JPA entities (User, Game, etc.)
│   │   ├── repository/                  ← Spring Data JPA repos
│   │   └── security/                    ← JWT utility + auth filter
│   ├── src/main/resources/
│   │   └── application.properties       ← !! Fill in Neon credentials here !!
│   ├── schema.sql                        ← Run once in Neon SQL Editor to create tables
│   ├── neon-fix.sql                      ← Run in Neon SQL Editor to fix demo accounts
│   └── pom.xml
└── pcplus-frontend/
    ├── assets/
    │   ├── css/styles.css               ← Publisher page styles
    │   ├── img/                         ← All game images
    │   └── js/
    │       ├── api.js                   ← Backend REST API client
    │       └── auth.js                  ← Auth (backend-first + localStorage fallback)
    ├── customer/                        ← Customer-facing pages
    └── publisher/                       ← Publisher dashboard pages
```

---

## API Reference

### Auth
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/api/auth/signup` | None | Create account → returns JWT |
| `POST` | `/api/auth/login` | None | Login → returns JWT |
| `POST` | `/api/auth/reset-password` | None | Reset password via PIN |
| `GET`  | `/api/auth/me` | JWT | Get current user info |
| `PATCH`| `/api/auth/avatar` | JWT | Update avatar |

### Games (public)
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/games` | All live games |
| `GET` | `/api/games/{id}` | Single game by ID |
| `GET` | `/api/games/top-sellers` | Top 6 by download count |
| `GET` | `/api/games/newest` | 6 most recently added |
| `GET` | `/api/games/on-sale` | Games with a sale price |
| `GET` | `/api/games/search?q=` | Search by title/genre keyword |

### Cart (JWT required)
| Method | Endpoint | Description |
|---|---|---|
| `GET`    | `/api/cart` | Get all cart items |
| `POST`   | `/api/cart` | Add game `{ "gameId": 1 }` |
| `DELETE` | `/api/cart/{gameId}` | Remove one item |
| `DELETE` | `/api/cart` | Clear entire cart |

### Library / Purchases (JWT required)
| Method | Endpoint | Description |
|---|---|---|
| `GET`  | `/api/library` | Get all purchased games |
| `GET`  | `/api/library/owns/{gameId}` | Check if user owns a game |
| `POST` | `/api/library/checkout` | Purchase all cart items |
| `POST` | `/api/library/buy/{gameId}` | Buy a single game |

### Publisher (publisher role + JWT required)
| Method | Endpoint | Description |
|---|---|---|
| `GET`    | `/api/publisher/games` | My game listings |
| `POST`   | `/api/publisher/games` | Create listing |
| `PUT`    | `/api/publisher/games/{id}` | Update listing |
| `DELETE` | `/api/publisher/games/{id}` | Delete listing |
| `GET`    | `/api/publisher/dashboard` | Revenue + download KPIs |
| `GET`    | `/api/publisher/reviews` | Reviews on my games |

---

## Technologies

| Layer | Technology |
|---|---|
| Frontend | HTML5, Tailwind CSS (CDN), Vanilla JS |
| Backend | Java 21, Spring Boot 3.2.3, Spring Security, Spring Data JPA |
| Database | PostgreSQL via Neon.tech (serverless) |
| Auth | JWT (JJWT 0.12.5) |
| ORM | Hibernate (via Spring Data JPA) |
| Build | Apache Maven 3.9 |
| Font | Yuji Mai (Google Fonts) |
