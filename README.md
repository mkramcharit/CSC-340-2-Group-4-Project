# PC+ Spring Boot MVC Application

## Team Members

Michael Ramcharitar  
Eddy Arriaga-Barrientos

## Version

1.1

## Course

CSC 340

## Project Scope

Customer and Provider (Publisher) use cases

## Local Site URL

```text
http://localhost:8081/pcplus/catalog
```

## API Base URL

```text
http://localhost:8081/api
```

## Overview

The PC+ application allows customers and publishers to interact with a digital game catalog. Customers can browse the catalog, view individual game details, create an account, log in, add games to a cart for purchase, checkout, view their library of purchased games, and write reviews for the games they have purchased. Publishers can create an account, log in, access a publisher dashboard, add new games, edit existing games, remove games from the catalog, view sales statistics for their games, delete reviews for their games, and reply to comments on those reviews.

The backend utilizes Spring Boot, Spring MVC, Spring Data JPA, Spring Security, FreeMarker templates, Tailwind CSS for styling, and a PostgreSQL database hosted on Neon.

The SysAdmin endpoints for the application are outside of the scope of this project.

## Tech Stack

- Java
- Spring Boot
- Spring MVC
- Spring Data JPA
- Spring Security
- FreeMarker
- PostgreSQL (hosted on Neon)
- Tailwind CSS
- Maven

## UML Class Diagram

The class diagram for the PC+ application is included with the project. The file location is:

```text
backend-api/docs/uml-class-diagram.png
```

## Neon Configuration

The database configuration for the application (application.properties) is located at the following address within the repository:

```text
src/main/resources/application.properties
```

The file contains database passwords replaced with environment variables. The following environment variables must be configured on the local machine to connect to the Neon database.

### PowerShell

```powershell
$env:NEON_DB_URL="jdbc:postgresql://YOUR_NEON_HOST/neondb?sslmode=require"

$env:NEON_DB_USERNAME="neondb_owner"

$env:NEON_DB_PASSWORD="YOUR_NEON_PASSWORD"
```

### macOS or Linux Terminal

```bash
export NEON_DB_URL="jdbc:postgresql://YOUR_NEON_HOST/neondb?sslmode=require"

export NEON_DB_USERNAME="neondb_owner"

export NEON_DB_PASSWORD="YOUR_NEON_PASSWORD"
```

## How to Run the Project

Once the environment variables are configured, the project can be compiled and run using the following command:

```bash
mvn spring-boot:run
```

Alternatively, the following command can be used to invoke the Maven wrapper:

```bash
./mvnw spring-boot:run
```

On Windows PowerShell using the Maven wrapper:

```powershell
.\mvnw spring-boot:run
```

Once the application is running, navigate to the following URL:

```text
http://localhost:8080/pcplus/catalog
```

## Main MVC Pages

| Page | URL | Description |
|---|---|---|
| Catalog | `/pcplus/catalog` | List of all available games |
| Game Details | `/pcplus/games/{gameId}` | Page with details about a specific game |
| Login | `/pcplus/login` | Page to login to the application |
| Signup | `/pcplus/signup` | Page to create an account |
| Forgot Password | `/pcplus/forgot-password` | Page to reset the password by entering the email and 4-digit PIN associated to the account |
| Cart | `/pcplus/cart` | Page that shows the items added to the cart |
| Library | `/pcplus/library` | Page that lists all of the games purchased by the customer |
| Profile | `/pcplus/profile` | Page that displays information about the user |
| Publisher Dashboard | `/pcplus/publisher/dashboard` | Publisher page that displays sales and review statistics |
| New Publisher Game | `/pcplus/publisher/games/new` | Form page to create a new game by the publisher |
| Edit Publisher Game | `/pcplus/publisher/games/{gameId}/edit` | Form page to edit the information of a game published by the publisher |

## API Endpoints

The following endpoints are available for the customers and publishers actors as well as the MVC frontend.

### Auth Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/signup` | End point to create a new customer or publisher |
| POST | `/api/auth/login` | Endpoint to login to the application |
| POST | `/api/auth/reset-password` | Endpoint to reset the password for the user |
| GET | `/api/auth/me` | Returns information about the user that is currently logged in |
| PATCH | `/api/auth/avatar` | Endpoint to change the avatar for the user that is currently logged in |

### Game Catalog Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/games` | Returns a list of all of the games in the catalog |
| GET | `/api/games/{id}` | Returns the details for a specific game |
| GET | `/api/games/top-sellers` | Returns a list of the best selling games |
| GET | `/api/games/newest` | Returns a list of the newest games added to the catalog |
| GET | `/api/games/on-sale` | Returns a list of all of the games on sale |
| GET | `/api/games/search?q=` | Searches for games in the catalog by the title and related text of the game |
| GET | `/api/games/{gameId}/reviews` | Returns a list of all of the reviews for a specific game |
| POST | `/api/games/{gameId}/reviews` | Endpoint for customers to leave a review for a specific game |

### Cart Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/cart` | Returns the list of all of the items in the cart |
| POST | `/api/cart/{gameId}` | Endpoint for adding a specific game to the cart |
| DELETE | `/api/cart/{gameId}` | Endpoint for removing a specific game from the cart |
| DELETE | `/api/cart` | Endpoint for emptying the cart of all items |

## Library and Checkout Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/library` | Returns the list of games that have been purchased by the customer |
| GET | `/api/library/owns/{gameId}` | Endpoint for determining if a customer owns a specific game |
| POST | `/api/library/checkout` | Endpoint to checkout for all of the items in the cart |
| POST | `/api/library/buy/{gameId}` | Endpoint to purchase a specific game |

## Publisher Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/pcplus/publisher/dashboard` | Shows the publisher dashboard containing sales and review statistics |
| GET | `/pcplus/publisher/games/new` | Shows the game creation form |
| POST | `/pcplus/publisher/games` | Creates a new game published by the logged in publisher |
| GET | `/pcplus/publisher/games/{gameId}/edit` | Shows the edit form for the game published by the publisher |
| POST | `/pcplus/publisher/games/{gameId}/edit` | Updates the information for a game published by the publisher |
| POST | `/pcplus/publisher/games/{gameId}/delete` | Deletes a game published by the publisher |
| POST | `/pcplus/publisher/reviews/{reviewId}/delete` | Deletes the review from one of the publisher's games |
| POST | `/pcplus/publisher/reviews/{reviewId}/reply` | Allows the publisher to add a reply to one of the reviews of one of their games |
| GET | `/pcplus/publisher/logout` | Logs out the currently logged in publisher |

## Use Case Mapping

### Customer Use Cases

| Use Case | Related Pages or Endpoints |
|---|---|
| Create an account | `/pcplus/signup`, `/api/auth/signup` |
| Login | `/pcplus/login`, `/api/auth/login` |
| Browse the catalog | `/pcplus/catalog`, `/api/games` |
| View details for a game | `/pcplus/games/{gameId}`, `/api/games/{id}` |
| Add a game to cart | `/pcplus/cart`, `/api/cart/{gameId}` |
| Checkout cart | `/pcplus/cart`, `/api/library/checkout` |
| View purchased games | `/pcplus/library`, `/api/library` |
| Write a review | `/pcplus/games/{gameId}`, `/api/games/{gameId}/reviews` |
| Reset password | `/pcplus/forgot-password`, `/api/auth/reset-password` |

### Publisher Use Cases

| Use Case | Related Pages or Endpoints |
|---|---|
| Create an account | `/pcplus/signup`, `/api/auth/signup` |
| Login | `/pcplus/login`, `/api/auth/login` |
| View publisher dashboard | `/pcplus/publisher/dashboard` |
| Add a game | `/pcplus/publisher/games/new` |
| Edit a game | `/pcplus/publisher/games/{gameId}/edit` |
| Remove a game | `/pcplus/publisher/games/{gameId}/delete` |
| View sales | `/pcplus/publisher/dashboard` |
| Delete a review | `/pcplus/publisher/reviews/{reviewId}/delete` |
| Reply to a review | `/pcplus/publisher/reviews/{reviewId}/reply` |

## Demo Login Accounts

If the database is seeded with the following accounts, they can be used to test the application.

| Actor | Email | Password | PIN |
|---|---|---|---|
| Customer | customer | customer | 0000 |
| Publisher | publisher | publisher | 0000 |

If these login accounts do not work, they can be created via the signup page for the application. Additionally, there is an optional script to fix the database if it was created with an older version of the PC+ application.

## Presentation Demo Plan

To run the project, start the Spring Boot application on the local machine with the database credentials configured. Afterwards, present the project via the MVC website.

The following is a suggested series of steps to demonstrate the customer features of the application:

1. Navigate to the catalog
2. Log in as a customer
3. View the details of a game
4. Add the game to the cart
5. Navigate to the cart
6. Checkout the cart
7. View the game in the library
8. Confirm that the data is persisted in the Neon database

The following is a suggested series of steps to demonstrate the publisher features of the application:

1. Log in as a publisher
2. View the publisher dashboard
3. Add or edit a game
4. View the game on the customer catalog website
5. View sales and review information for the game published by the publisher
6. Reply to or delete a review for the game
7. Confirm that the data is persisted in the Neon database

## Project Notes

The PC+ application utilizes a live database hosted on Neon. The password for the database is not to be committed to the repository.

The application utilizes FreeMarker templates for the MVC website frontend.

Tailwind CSS is utilized for the website styling and is loaded from the CDN.

The main actors for this project are the customers and publishers.

The SysAdmin endpoints for the application are outside of the scope of this project.

## How to Run

From the root project folder, run:

./mvnw spring-boot:run

On Windows PowerShell, run:

.\mvnw spring-boot:run

Then open the site at: http://localhost:8081/pcplus

Once you confirm it works, stop the server in the terminal with: Ctrl + C

Then type: Y