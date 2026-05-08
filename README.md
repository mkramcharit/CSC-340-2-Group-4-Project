# PC+ Spring Boot MVC Application

## Team Members

Michael Ramcharitar  
Eddy Arriaga-Barrientos

## Version

Final CSC 340 submission

## Course

CSC 340

## Project Scope

PC+ implements two main actors:

- Customer
- Publisher, also referred to as Provider in earlier course documents

SysAdmin functionality is not part of the final implemented scope.

## Project Summary

PC+ is a Spring Boot MVC web application for a digital game storefront. Customers can create an account, log in, browse the game catalog, view game details, add games to a cart, check out, view purchased games in their library, update their profile/avatar, reset their password with a PIN, and leave reviews for games they own.

Publishers can create a publisher account, log in, access a publisher dashboard, add new game listings, edit existing games they own, remove their own games, view sales and review information for their games, reply to reviews, and delete reviews on games they published.

The application uses Spring Boot, Spring MVC controllers, service classes, Spring Data JPA repositories, FreeMarker templates, Tailwind CSS styling, and a PostgreSQL database hosted through Neon.

## How the Project Is Compartmentalized

The project is organized by application responsibility so each layer has a clear job.

| Area | Folder or File | Purpose |
|---|---|---|
| Application entry point | `src/main/java/com/pcplus/PcPlusApplication.java` | Starts the Spring Boot application |
| Configuration | `src/main/java/com/pcplus/config` | Holds application configuration, security setup, and database seed data |
| Controllers | `src/main/java/com/pcplus/controller` | Receives browser or API requests and sends work to the correct service |
| Services | `src/main/java/com/pcplus/service` | Holds the business logic and rules for login, catalog, cart, checkout, reviews, and publisher management |
| Repositories | `src/main/java/com/pcplus/repository` | Communicates with the Neon PostgreSQL database through Spring Data JPA |
| Models | `src/main/java/com/pcplus/model` | Defines the database-backed objects such as User, Game, CartItem, Purchase, and Review |
| DTOs | `src/main/java/com/pcplus/dto/Dtos.java` | Defines request and response objects used by the API |
| Security | `src/main/java/com/pcplus/security` | Handles JWT creation, validation, and request authentication |
| Exceptions | `src/main/java/com/pcplus/exception` | Handles API errors in a consistent format |
| MVC views | `src/main/resources/templates` | FreeMarker pages shown to customers and publishers |
| App settings | `src/main/resources/application.properties` | Contains Spring Boot, database, server port, and JWT settings |
| Database schema | `schema.sql` | Defines database table structure for the app |
| SRS document | `PC+SRS.md` | Lists the final use cases and requirements |
| UML diagram | `docs/uml-class-diagram.png` | Shows the final main class/object relationships |

## Tech Stack

- Java
- Spring Boot
- Spring MVC
- Spring Data JPA
- Spring Security
- FreeMarker
- PostgreSQL hosted on Neon
- Tailwind CSS
- Maven

## Local Site URL

```text
http://localhost:8081/pcplus
```

Main catalog page:

```text
http://localhost:8081/pcplus/catalog
```

## API Base URL

```text
http://localhost:8081/api
```

## UML Class Diagram

The final UML class diagram is located at:

```text
docs/uml-class-diagram.png
```

A copy may also be kept at:

```text
backend-api/docs/uml-class-diagram.png
```

## Important Security Note

The submitted repository should not contain a real Neon database password or secret. The `src/main/resources/application.properties` file should use environment variables or safe placeholders before pushing to GitHub.

Recommended format:

```properties
spring.datasource.url=${NEON_DB_URL}
spring.datasource.username=${NEON_DB_USERNAME}
spring.datasource.password=${NEON_DB_PASSWORD}
pcplus.jwt.secret=${PCPLUS_JWT_SECRET:PCPlusSecretKey_ChangeThisInProduction_CSC340_Group4_2026}
```

## Environment Variables

### PowerShell

```powershell
$env:NEON_DB_URL="jdbc:postgresql://YOUR_NEON_HOST/neondb?sslmode=require"
$env:NEON_DB_USERNAME="neondb_owner"
$env:NEON_DB_PASSWORD="YOUR_NEON_PASSWORD"
$env:PCPLUS_JWT_SECRET="YOUR_LONG_SECRET_VALUE"
```

### macOS or Linux Terminal

```bash
export NEON_DB_URL="jdbc:postgresql://YOUR_NEON_HOST/neondb?sslmode=require"
export NEON_DB_USERNAME="neondb_owner"
export NEON_DB_PASSWORD="YOUR_NEON_PASSWORD"
export PCPLUS_JWT_SECRET="YOUR_LONG_SECRET_VALUE"
```

## How to Run the Project

From the root project folder, run:

```bash
./mvnw spring-boot:run
```

On Windows PowerShell, run:

```powershell
.\mvnw spring-boot:run
```

If Maven is installed globally, this also works:

```bash
mvn spring-boot:run
```

Then open:

```text
http://localhost:8081/pcplus
```

To stop the server, press `Ctrl + C` in the terminal.

## Main MVC Pages

| Page | URL | Description |
|---|---|---|
| Catalog | `/pcplus/catalog` | Lists the available live games |
| Game Details | `/pcplus/games/{id}` | Shows details, price, images, ownership state, and reviews for one game |
| Customer Signup | `/pcplus/signup` | Creates a customer account |
| Publisher Signup | `/pcplus/publisher/signup` | Creates a publisher account |
| Login | `/pcplus/login` | Logs in either a customer or publisher |
| Forgot Password | `/pcplus/forgot-password` | Resets a password using email and 4-digit PIN |
| Cart | `/pcplus/cart` | Shows games added to the cart |
| Library | `/pcplus/library` | Shows games purchased by the logged-in customer |
| Profile | `/pcplus/profile` | Shows and updates customer profile information |
| Publisher Dashboard | `/pcplus/publisher/dashboard` | Shows publisher games, sales data, and reviews |
| New Publisher Game | `/pcplus/publisher/games/new` | Form for a publisher to add a new game |
| Edit Publisher Game | `/pcplus/publisher/games/{id}/edit` | Form for a publisher to edit one of their games |

## Customer API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/signup` | Creates a customer or publisher account |
| POST | `/api/auth/login` | Logs in and returns authentication data |
| POST | `/api/auth/reset-password` | Resets a password using email and PIN |
| GET | `/api/auth/me` | Returns the currently authenticated user |
| PATCH | `/api/auth/avatar` | Updates the current user's avatar |
| GET | `/api/games` | Lists all live games |
| GET | `/api/games/{id}` | Shows one game by id |
| GET | `/api/games/top-sellers` | Lists top selling games |
| GET | `/api/games/newest` | Lists newest games |
| GET | `/api/games/on-sale` | Lists games with sale prices |
| GET | `/api/games/search?q=` | Searches games by keyword |
| GET | `/api/games/{gameId}/reviews` | Lists reviews for one game |
| POST | `/api/games/{gameId}/reviews` | Creates a review for an owned game |
| GET | `/api/cart` | Lists the current user's cart |
| POST | `/api/cart` | Adds a game to the cart |
| DELETE | `/api/cart/{gameId}` | Removes one game from the cart |
| DELETE | `/api/cart` | Clears the cart |
| GET | `/api/library` | Lists purchased games |
| GET | `/api/library/owns/{gameId}` | Checks whether the current customer owns a game |
| POST | `/api/library/checkout` | Checks out every item in the cart |
| POST | `/api/library/buy/{gameId}` | Immediately buys one game |

## Publisher API and MVC Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/publisher/games` | Lists games owned by the publisher |
| GET | `/api/publisher/profile` | Returns publisher profile information |
| PATCH | `/api/publisher/profile` | Updates publisher profile information |
| POST | `/api/publisher/games` | Creates a new game owned by the publisher |
| PUT | `/api/publisher/games/{id}` | Updates a publisher-owned game |
| DELETE | `/api/publisher/games/{id}` | Removes a publisher-owned game |
| GET | `/api/publisher/dashboard` | Returns dashboard summary data |
| GET | `/api/publisher/reviews` | Returns reviews for publisher-owned games |
| GET | `/pcplus/publisher/dashboard` | Shows the publisher dashboard page |
| POST | `/pcplus/publisher/games` | Creates a game through the MVC form |
| POST | `/pcplus/publisher/games/{id}` | Updates a game through the MVC form |
| POST | `/pcplus/publisher/games/{id}/delete` | Removes a game through the MVC dashboard |
| POST | `/pcplus/publisher/reviews/{id}/reply` | Adds a publisher reply to a review |
| POST | `/pcplus/publisher/reviews/{id}/delete` | Deletes a review from a publisher-owned game |
| GET | `/pcplus/publisher/logout` | Logs out the publisher |

## Use Case Mapping

### Customer Use Cases

| Use Case | MVC Page or API Endpoint |
|---|---|
| Create an account | `/pcplus/signup`, `/api/auth/signup` |
| Log in | `/pcplus/login`, `/api/auth/login` |
| Reset password | `/pcplus/forgot-password`, `/api/auth/reset-password` |
| Browse catalog | `/pcplus/catalog`, `/api/games` |
| Search catalog | `/pcplus/catalog?search=`, `/api/games/search?q=` |
| View game details | `/pcplus/games/{id}`, `/api/games/{id}` |
| Add game to cart | `/pcplus/cart/add/{id}`, `/api/cart` |
| Remove game from cart | `/pcplus/cart/remove/{id}`, `/api/cart/{gameId}` |
| Checkout cart | `/pcplus/cart/checkout`, `/api/library/checkout` |
| Buy one game | `/pcplus/games/{id}/buy`, `/api/library/buy/{gameId}` |
| View library | `/pcplus/library`, `/api/library` |
| Leave review | `/pcplus/games/{id}/reviews`, `/api/games/{gameId}/reviews` |
| Update profile or avatar | `/pcplus/profile`, `/api/auth/avatar` |

### Publisher Use Cases

| Use Case | MVC Page or API Endpoint |
|---|---|
| Create a publisher account | `/pcplus/publisher/signup`, `/api/auth/signup` |
| Log in as publisher | `/pcplus/login`, `/api/auth/login` |
| View publisher dashboard | `/pcplus/publisher/dashboard`, `/api/publisher/dashboard` |
| Add game | `/pcplus/publisher/games/new`, `/pcplus/publisher/games`, `/api/publisher/games` |
| Edit game | `/pcplus/publisher/games/{id}/edit`, `/pcplus/publisher/games/{id}`, `/api/publisher/games/{id}` |
| Remove game | `/pcplus/publisher/games/{id}/delete`, `/api/publisher/games/{id}` |
| View sales and review data | `/pcplus/publisher/dashboard`, `/api/publisher/dashboard`, `/api/publisher/reviews` |
| Reply to review | `/pcplus/publisher/reviews/{id}/reply`, `/api/games/{gameId}/reviews/{reviewId}/reply` |
| Delete review | `/pcplus/publisher/reviews/{id}/delete`, `/api/games/{gameId}/reviews/{reviewId}` |

## Presentation Demo Plan

### Customer Demo

1. Open `http://localhost:8081/pcplus/catalog`.
2. Log in or create a customer account.
3. Open a game detail page.
4. Add the game to the cart.
5. Open the cart.
6. Checkout.
7. Open the library and show that the game appears there.
8. Show Neon tables such as `users`, `cart_items`, and `purchases` to prove persistence.

### Publisher Demo

1. Log in or create a publisher account.
2. Open the publisher dashboard.
3. Add or edit a game.
4. Return to the customer catalog and show the game update.
5. Reply to or delete a review for a publisher-owned game.
6. Show Neon tables such as `games` and `reviews` to prove persistence.

## Demo Login Accounts

If the database is seeded with the following accounts, they can be used for testing.

| Actor | Email | Password | PIN |
|---|---|---|---|
| Customer | `customer` | `customer` | `0000` |
| Publisher | `publisher` | `publisher` | `0000` |

If those accounts do not work, create new accounts from the signup pages.
