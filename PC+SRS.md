# PC+ Software Requirements Specification

## Project Information

Project Name: PC+  
Team: Michael Ramcharitar, Eddy Arriaga-Barrientos  
Course: CSC 340  
Version: Final Submission  

## 1. Overview

### Vision

PC+ is a web-based digital game storefront that lets customers browse games, view game details, simulate purchases, manage a purchased library, and write reviews. The system also lets publishers manage their own game listings, view sales and review information, reply to reviews, and delete reviews on their games.

### Implemented Actors

| Actor | Description |
|---|---|
| Customer | Browses games, manages a cart, checks out, views a library, updates profile information, and reviews purchased games |
| Publisher / Provider | Adds, edits, removes, and manages their own game listings, views dashboard statistics, and manages reviews on their games |

SysAdmin functionality was discussed earlier in the project but is outside the final implemented scope.

### Glossary

| Term | Meaning |
|---|---|
| Catalog | The collection of live games available to customers |
| Cart | A temporary list of games the customer plans to purchase |
| Library | The customer's purchased games after simulated checkout |
| Simulated Checkout | The purchase process that creates database purchase records without real payment processing |
| Publisher / Provider | A user who owns and manages game listings |
| Review | Customer feedback for a purchased game |

## 2. Scope

### In Scope

- Customer signup, login, logout, profile update, avatar update, and password reset by PIN
- Publisher signup, login, logout, dashboard access, and publisher game management
- Game catalog browsing, searching, and game detail pages
- Cart and simulated checkout
- Purchased game library
- Customer reviews for owned games
- Publisher replies to reviews
- Publisher deletion of reviews on their own games
- PostgreSQL persistence through Neon
- MVC pages using FreeMarker templates
- API endpoints for catalog, auth, cart, library, reviews, and publisher actions

### Out of Scope

- Real payment processing
- Real game downloads or DRM
- SysAdmin dashboard or system-wide moderation tools
- Advanced recommendation algorithms
- Social features outside of reviews

## 3. Functional Requirements and Use Cases

## 3.1 Customer Use Cases

### US-CUST-001 Create Customer Account

As a customer, I want to create an account so that I can log in and use customer features.

Acceptance Scenario:

Given I am not logged in  
When I submit valid signup information  
Then a new customer account is created and saved in the database

Implementation Mapping:

- MVC: `/pcplus/signup`
- API: `POST /api/auth/signup`
- Main files: `CustomerPageController`, `AuthController`, `AuthService`, `UserRepository`, `customer-signup.ftlh`

### US-CUST-002 Log In

As a customer, I want to log in so that I can access my cart, library, profile, and review features.

Acceptance Scenario:

Given I have an active account  
When I submit the correct login credentials  
Then I am logged in and redirected to the proper customer page

Implementation Mapping:

- MVC: `/pcplus/login`
- API: `POST /api/auth/login`
- Main files: `CustomerPageController`, `AuthController`, `AuthService`, `UserRepository`, `customer-login.ftlh`

### US-CUST-003 Reset Password

As a customer, I want to reset my password with my email and PIN so that I can recover access to my account.

Acceptance Scenario:

Given I know my account email and PIN  
When I submit a new password  
Then my password is updated in the database

Implementation Mapping:

- MVC: `/pcplus/forgot-password`
- API: `POST /api/auth/reset-password`
- Main files: `CustomerPageController`, `AuthController`, `AuthService`, `UserRepository`, `customer-forgot-password.ftlh`

### US-CUST-004 Browse and Search Catalog

As a customer, I want to browse and search the catalog so that I can find games I may want to buy.

Acceptance Scenario:

Given live games exist in the catalog  
When I open the catalog page or search by keyword  
Then I see matching game listings with titles, images, prices, and rating information

Implementation Mapping:

- MVC: `/pcplus/catalog`
- API: `GET /api/games`, `GET /api/games/search?q=`
- Main files: `CustomerPageController`, `GameController`, `GameService`, `GameRepository`, `customer-catalog.ftlh`

### US-CUST-005 View Game Details

As a customer, I want to view a game's detail page so that I can see more information before buying it.

Acceptance Scenario:

Given a game exists  
When I open the game detail page  
Then I see the game's description, price, images, system requirements, rating, and reviews

Implementation Mapping:

- MVC: `/pcplus/games/{id}`
- API: `GET /api/games/{id}`, `GET /api/games/{gameId}/reviews`
- Main files: `CustomerPageController`, `GameController`, `ReviewController`, `GameService`, `ReviewService`, `customer-game-details.ftlh`

### US-CUST-006 Add Game to Cart

As a customer, I want to add a game to my cart so that I can buy it during checkout.

Acceptance Scenario:

Given I am logged in as a customer  
When I add a game to the cart  
Then a cart item is created unless I already own the game or already have it in the cart

Implementation Mapping:

- MVC: `/pcplus/cart/add/{id}`
- API: `POST /api/cart`
- Main files: `CustomerPageController`, `CartController`, `CartService`, `CartItemRepository`, `PurchaseRepository`

### US-CUST-007 Remove Game from Cart

As a customer, I want to remove a game from my cart so that I can change what I plan to buy.

Acceptance Scenario:

Given I have a game in my cart  
When I remove it  
Then the cart item is deleted from my cart

Implementation Mapping:

- MVC: `/pcplus/cart/remove/{id}`
- API: `DELETE /api/cart/{gameId}`
- Main files: `CustomerPageController`, `CartController`, `CartService`, `CartItemRepository`, `customer-cart.ftlh`

### US-CUST-008 Checkout Cart

As a customer, I want to check out my cart so that the games are added to my library.

Acceptance Scenario:

Given I have games in my cart  
When I complete checkout  
Then purchase records are created and the cart is cleared

Implementation Mapping:

- MVC: `/pcplus/cart/checkout`
- API: `POST /api/library/checkout`
- Main files: `CustomerPageController`, `PurchaseController`, `PurchaseService`, `PurchaseRepository`, `CartItemRepository`, `customer-cart.ftlh`

### US-CUST-009 View Library

As a customer, I want to view my library so that I can see the games I purchased.

Acceptance Scenario:

Given I have purchased games  
When I open the library page  
Then I see the games connected to my account through purchase records

Implementation Mapping:

- MVC: `/pcplus/library`
- API: `GET /api/library`
- Main files: `CustomerPageController`, `PurchaseController`, `PurchaseService`, `PurchaseRepository`, `customer-library.ftlh`

### US-CUST-010 Write Review

As a customer, I want to review a game I own so that I can share feedback.

Acceptance Scenario:

Given I own the game  
When I submit a rating and review body  
Then the review is saved and appears on the game detail page

Implementation Mapping:

- MVC: `/pcplus/games/{id}/reviews`
- API: `POST /api/games/{gameId}/reviews`
- Main files: `CustomerPageController`, `ReviewController`, `ReviewService`, `ReviewRepository`, `PurchaseRepository`, `customer-game-details.ftlh`

### US-CUST-011 Update Profile and Avatar

As a customer, I want to update my profile and avatar so that my account information is current.

Acceptance Scenario:

Given I am logged in  
When I submit profile or avatar changes  
Then the updated information is saved and displayed in the UI

Implementation Mapping:

- MVC: `/pcplus/profile`
- API: `PATCH /api/auth/avatar`
- Main files: `CustomerPageController`, `AuthController`, `AuthService`, `UserRepository`, `customer-profile.ftlh`

## 3.2 Publisher Use Cases

### US-PUB-001 Create Publisher Account

As a publisher, I want to create a publisher account so that I can manage game listings.

Acceptance Scenario:

Given I am not logged in  
When I submit valid publisher signup information  
Then a publisher account is created and saved in the database

Implementation Mapping:

- MVC: `/pcplus/publisher/signup`
- API: `POST /api/auth/signup`
- Main files: `PublisherPageController`, `AuthController`, `AuthService`, `UserRepository`, `publisher-signup.ftlh`

### US-PUB-002 Log In as Publisher

As a publisher, I want to log in so that I can reach the publisher dashboard.

Acceptance Scenario:

Given I have a publisher account  
When I submit the correct login credentials  
Then I am redirected to the publisher dashboard

Implementation Mapping:

- MVC: `/pcplus/login`
- API: `POST /api/auth/login`
- Main files: `CustomerPageController`, `AuthController`, `AuthService`, `UserRepository`, `customer-login.ftlh`

### US-PUB-003 View Publisher Dashboard

As a publisher, I want to view a dashboard so that I can manage my games and see basic sales/review information.

Acceptance Scenario:

Given I am logged in as a publisher  
When I open the publisher dashboard  
Then I see my games, sales information, and reviews for games I published

Implementation Mapping:

- MVC: `/pcplus/publisher/dashboard`
- API: `GET /api/publisher/dashboard`, `GET /api/publisher/reviews`
- Main files: `PublisherPageController`, `PublisherController`, `PublisherService`, `GameRepository`, `ReviewRepository`, `PurchaseRepository`, `publisher-dashboard.ftlh`

### US-PUB-004 Add Game

As a publisher, I want to add a new game so that customers can see it in the catalog.

Acceptance Scenario:

Given I am logged in as a publisher  
When I submit a valid game form  
Then the game is saved with me as the owner and appears in the catalog

Implementation Mapping:

- MVC: `/pcplus/publisher/games/new`, `/pcplus/publisher/games`
- API: `POST /api/publisher/games`
- Main files: `PublisherPageController`, `PublisherController`, `PublisherService`, `GameRepository`, `publisher-game-form.ftlh`

### US-PUB-005 Edit Game

As a publisher, I want to edit one of my games so that its information stays accurate.

Acceptance Scenario:

Given I own a game listing  
When I update its information  
Then the catalog displays the updated information

Implementation Mapping:

- MVC: `/pcplus/publisher/games/{id}/edit`, `/pcplus/publisher/games/{id}`
- API: `PUT /api/publisher/games/{id}`
- Main files: `PublisherPageController`, `PublisherController`, `PublisherService`, `GameRepository`, `publisher-game-form.ftlh`

### US-PUB-006 Remove Game

As a publisher, I want to remove one of my games so that it no longer appears as an active listing.

Acceptance Scenario:

Given I own a game listing  
When I delete it from the dashboard  
Then it is removed from the publisher's active management view and no longer shown as a normal live listing

Implementation Mapping:

- MVC: `/pcplus/publisher/games/{id}/delete`
- API: `DELETE /api/publisher/games/{id}`
- Main files: `PublisherPageController`, `PublisherController`, `PublisherService`, `GameRepository`, `publisher-dashboard.ftlh`

### US-PUB-007 Reply to Reviews

As a publisher, I want to reply to reviews on my games so that I can respond to customer feedback.

Acceptance Scenario:

Given a customer has reviewed one of my games  
When I submit a publisher reply  
Then the reply appears with the review

Implementation Mapping:

- MVC: `/pcplus/publisher/reviews/{id}/reply`
- API: `PUT /api/games/{gameId}/reviews/{reviewId}/reply`
- Main files: `PublisherPageController`, `ReviewController`, `ReviewService`, `ReviewRepository`, `publisher-dashboard.ftlh`

### US-PUB-008 Delete Reviews

As a publisher, I want to delete reviews on my games so that I can remove unwanted review entries from my dashboard/game pages.

Acceptance Scenario:

Given a review exists on one of my games  
When I delete the review  
Then it is marked removed and no longer displayed as a normal active review

Implementation Mapping:

- MVC: `/pcplus/publisher/reviews/{id}/delete`
- API: `DELETE /api/games/{gameId}/reviews/{reviewId}`
- Main files: `PublisherPageController`, `ReviewController`, `ReviewService`, `ReviewRepository`, `publisher-dashboard.ftlh`

## 4. Nonfunctional Requirements

| Category | Requirement |
|---|---|
| Usability | The UI should be clear enough for first-time users to browse, buy, and manage games without outside help |
| Reliability | Customer and publisher actions should persist to the Neon database |
| Security | Passwords should be stored as hashes, role-based pages should require the correct role, and database passwords should not be committed to GitHub |
| Maintainability | The system should keep controller, service, repository, model, and template responsibilities separate |
| Performance | Pages should load quickly enough for live classroom demonstration and normal academic testing |

## 5. Data Requirements

The final application uses the following main model classes:

| Model | Purpose |
|---|---|
| User | Stores customer and publisher accounts |
| Game | Stores game listings and publisher ownership |
| CartItem | Stores games temporarily added to a customer's cart |
| Purchase | Stores simulated purchases and library ownership |
| Review | Stores customer reviews and publisher replies |

## 6. Assumptions and Constraints

- A user must be authenticated to purchase games, access a cart, view a library, or write reviews.
- Publishers can only manage games they own.
- Customers can only review games they have purchased.
- Customers should not be able to purchase the same game twice.
- Real money is not processed.
- The project is for CSC 340 educational use.
