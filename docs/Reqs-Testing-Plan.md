# PC+ Requirements & Testing Plan

## System Overview

PC+ is a web application that allows users to browse, purchase, and review video games. The system utilizes:

- Spring Boot MVC architecture
- Freemarker templates for views
- Neon database for persistence

The application features two main types of users (actors):

- Customer: Users that browse and purchase games  
- Publisher: Users that create and manage games  

All data is stored in the Neon database.

---

# ACTOR 1: CUSTOMER

## Use Case 1: Create Account

### Scenario
A user signs up for an account.

### Steps
1. Navigate to the signup page  
2. Enter their:
   - Email  
   - Password  
   - Display name  
   - PIN  
3. Click create account  

### Expected Result
The user is created in the database and automatically logged in. The user is redirected to the catalog.

### Database Validation
A record is created in the users table.

---

## Use Case 2: Login

### Scenario
A user logs into their account.

### Steps
1. Navigate to the login page  
2. Enter the user’s:
   - Email  
   - Password  
3. Select customer  
4. Click sign in  

### Expected Result
The user is redirected to the catalog.

---

## Use Case 3: Browse Catalog

### Scenario
A user browses the available games.

### Steps
1. Navigate to the catalog  

### Expected Result
All games are displayed from the database.

---

## Use Case 4: Add to Cart

### Scenario
A user adds one of the games to their cart.

### Steps
1. Navigate to the details page for one of the games  
2. Click the add to cart button  

### Expected Result
The game appears in the cart. The cart count displays the new count.

### Database Validation
A record is added to the cart_items table.

---

## Use Case 5: Checkout

### Scenario
A customer completes buying all items in their cart.

### Steps
1. Navigate to the cart  
2. Click checkout  

### Expected Result
All items are removed from the cart. All items are added to the user’s library.

### Database Validation
- The item is removed from the cart_items table  
- An entry is created in the purchases table  

---

## Use Case 6: Write Review

### Scenario
A customer writes a review for one of the games they own.

### Steps
1. Navigate to the details for one of their games  
2. Write and submit a review  

### Expected Result
The review is published under the game’s details.

### Database Validation
An entry is created in the reviews table.

---

## Use Case 7: Profile Management

### Scenario
A customer updates their profile.

### Steps
1. Navigate to their profile  
2. Change their display name or avatar  
3. Save changes  

### Expected Result
The customer’s profile is updated.

---

# ACTOR 2: PUBLISHER

## Use Case 1: Create Publisher Account

### Scenario
A publisher creates an account.

### Steps
1. Navigate to the signup page  
2. Select publisher  
3. Enter their:
   - Company name  
   - Owner name  
   - Password  
   - Address  
   - Email  
   - PIN  
4. Click create account  

### Expected Result
The publisher is created and redirected to their dashboard.

---

## Use Case 2: Publisher Login

### Scenario
A publisher logs into their account.

### Steps
1. Navigate to the login page  
2. Select publisher  
3. Enter their credentials  

### Expected Result
The publisher is redirected to their dashboard.

---

## Use Case 3: View Dashboard

### Scenario
A publisher views their dashboard.

### Steps
1. Login as the publisher  

### Expected Result
The publisher sees information about their:

- Number of games  
- Sales  
- Revenue  
- Reviews  
- Average rating for their games  

---

## Use Case 4: Add Game

### Scenario
A publisher adds a new game to their catalog.

### Steps
1. Click the “Add Game” button on their dashboard  
2. Enter information about the game  

### Expected Result
The game appears on the publisher’s dashboard and on the public catalog.

### Database Validation
A record is added to the games table.

---

## Use Case 5: Edit Game

### Scenario
A publisher edits information about one of their games.

### Steps
1. Navigate to the game on the publisher’s catalog  
2. Click “Edit”  
3. Change information  
4. Save changes  

### Expected Result
The changes are visible on the catalog.

---

## Use Case 6: Delete Game

### Scenario
A publisher deletes one of their games from the catalog.

### Steps
1. Navigate to the game on the publisher’s catalog  
2. Click “Remove”  

### Expected Result
The game is removed from the catalog.

---

## Use Case 7: View Sales

### Scenario
A publisher views the sales information for their games.

### Steps
1. View the publisher’s dashboard  

### Expected Result
A list of sales and revenue for each game is visible.

---

## Use Case 8: Manage Reviews

### Scenario
A publisher moderates the comments and reviews for their games.

### Steps
1. Click on the “Reviews” link on the publisher’s dashboard  
2. Select a review to delete or reply  

### Expected Result
The selected review is deleted or the reply is saved.

---

## Use Case 9: Profile Access

### Scenario
A publisher views their profile.

### Steps
1. Click on the publisher’s profile icon  

### Expected Result
The publisher’s profile is visible.

---

# SYSTEM REQUIREMENTS VALIDATION

The system has been tested for:

- Persistence of data on the Neon database  
- Correct implementation of MVC architecture  
- Proper implementation of role-based access systems  

---

# DEMO PLAN (8 MINUTES)

## Use Case 1: Customer

Steps will include:

- Creating a customer account  
- Logging into the system  
- Browsing the available games  
- Adding one to the cart  
- Completing checkout  
- Demonstrating database updates  
- Adding reviews

---

## Use Case 2: Publisher

Steps will include:

- Login as a publisher  
- Adding a new game  
- Editing the game  
- Viewing it on the catalog  
- Viewing the publisher’s dashboard  
- Adding comments to reviews

---

## Use Case 3: Database Proof

The database will be displayed to verify the existence of the following tables:

- users  
- games  
- purchases  

---

# NOTES

- There is no use of hardcoded data for any features  
- Interactions with the database will be live and with the Neon database  
- Both customer and publisher actors are fully implemented  