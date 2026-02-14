Requirements – Starter Template

Project Name: PC+
Team: Michael Ramcharitar, Eddy Arriaga-Barrientos
Course: CSC 340
Version: 1.0
Date: 2026-02-08


1. Overview

Vision:
PC+ is a web-based application that lets users browse a catalog of games, view detailed game pages, simulate purchasing games, and maintain a library of purchased games, while allowing publishers to manage game listings and respond to reviews.


Glossary (Terms used in the project):

Catalog:
The collection of all games on the platform.

Library:
The list of games owned by a user after completing a simulated purchase.

Simulated Checkout:
The fictitious purchasing process that gives the user ownership of the purchased game.

Provider:
A publisher or developer who uploads and maintains games on the platform.

Customer:
A user who browses, purchases (simulates) and reviews games.


Primary Users / Roles:

Customer:
Browses for games, simulates purchasing games, maintains a library of purchased games, reviews games.

Provider:
Uploads and maintains games, views basic statistics, responds to reviews.

SysAdmin (optional):
Maintains system integrity and manages system-level settings.


Scope (this semester):

- User registration, login and logout
- Browsing a game catalog and viewing game details
- Simulated purchase process
- View and manage a library of purchased games
- Customer reviews, provider responses
- Game management by provider, basic statistics


Out of Scope (deferred):

- Real payment processing
- Actual game download or DRM
- Advanced recommendation algorithms
- Social features beyond reviews

This document is requirements-level.
Design decisions (UI, API, schema) are documented separately.


2. Functional Requirements (User Stories)

Each story follows the format:
As a role, I want a capability, so that I receive a benefit.
Each story will have at least one Given / When / Then acceptance scenario.


2.1 Customer Stories


US-CUST-001 – Register and Log In

Story:
As a customer, I want to register for an account and log in, so that I can manage my account and library of purchased games.

Acceptance:
Scenario: Register with valid credentials
Given I am not logged in
When I submit valid registration information
Then my account is created and I am logged in



US-CUST-002 – Browse Game Catalog

Story:
As a customer, I want to browse the catalog of available games so that I can choose which ones to purchase.

Acceptance:
Scenario: View game catalog
Given games exist in the catalog
When I view the catalog page
Then I see a list of available games with titles, prices, and images



US-CUST-003 – View Game Details

Story:
As a customer, I want to view the details of a specific game so that I can make an informed purchasing decision.

Acceptance:
Scenario: Open game detail page
Given a game is selected from the catalog
When I request to view the game details
Then I see the game's description, price, images, and average rating



US-CUST-004 – Simulated Purchase

Story:
As a customer, I want to purchase a game so that it is added to my library of purchased games.

Acceptance:
Scenario: Complete simulated purchase process
Given I have selected a game to purchase
When I complete the checkout process
Then the game is added to my library of purchased games



US-CUST-005 – Leave a Review

Story:
As a customer, I want to leave a review for a game I have purchased so that I can share my thoughts with other customers.

Acceptance:
Scenario: Submit a review for a game
Given I have purchased the game
When I submit a review for the game
Then my review is displayed on the game's detail page



US-CUST-006 – View Purchased Library

Story:
As a customer, I want to view my purchased game library so that I can easily see the games I own.

Acceptance:
Scenario: View library
Given I am logged in and have purchased games
When I open my library page
Then I see all games associated with my account



US-CUST-007 – Prevent Duplicate Purchases

Story:
As a customer, I want the system to prevent me from purchasing the same game twice so that my library stays accurate.

Acceptance:
Scenario: Attempt duplicate purchase
Given I already own a game
When I attempt to purchase the same game again
Then the system prevents the purchase and informs me that I already own the game



2.2 Provider Stories


US-PROV-001 – Manage Provider Profile

Story:
As a provider, I want to manage my provider profile so that my publisher details are up to date.

Acceptance:
Scenario: Update profile information
Given I am logged in as a provider
When I submit my updated profile information
Then my profile information is updated



US-PROV-002 – Add Game Listings

Story:
As a provider, I want to add new game listings to the catalog so that customers can purchase my games.

Acceptance:
Scenario: Add a new game listing
Given I am logged in as a provider
When I submit details for my new game including required fields
Then it appears in the game catalog



US-PROV-003 – View Game Statistics

Story:
As a provider, I want to view basic statistics about my games so that I can see how they are performing.

Acceptance:
Scenario: View statistics for my game
Given my game is listed in the catalog
When I access my game's statistics page
Then I see information about download counts, ratings, and review totals



US-PROV-004 – Respond to Reviews

Story:
As a provider, I want to respond to customer reviews so that I can address their concerns publicly.

Acceptance:
Scenario: Respond to a review from a customer
Given there is an existing review for one of my games
When I submit my response to the review
Then it appears below the review on the game's detail page



US-PROV-005 – Edit Existing Game Listing

Story:
As a provider, I want to edit an existing game listing so that I can update its price or description.

Acceptance:
Scenario: Update game listing
Given I am logged in as a provider and own a listed game
When I update the game information
Then the changes are reflected in the catalog and detail page



US-PROV-006 – Define System Requirements

Story:
As a provider, I want to define minimum system requirements for my game so that customers understand hardware expectations.

Acceptance:
Scenario: Add system requirements
Given I am logged in as a provider and own a listed game
When I submit minimum system requirements
Then the requirements are saved and displayed on the game detail page



2.3 SysAdmin Stories


US-ADMIN-001 – Moderate Reviews

Story:
As a SysAdmin, I want to moderate customer reviews on the site so that any inappropriate content is removed promptly.

Acceptance:
Scenario: Remove an inappropriate review
Given a review violates site policies
When I process its removal
Then it is no longer displayed on the site



US-ADMIN-002 – Manage User Accounts

Story:
As a SysAdmin, I want to manage user accounts on the site so that it remains secure for all users.

Acceptance:
Scenario: Remove an account belonging to a user violating site policies 
Given a user has violated site policies 
When I identify their account 
Then their account is placed into an inactive state



US-ADMIN-003 – Enforce Role-Based Access

Story:
As a SysAdmin, I want the system to enforce role-based access control so that users cannot perform actions outside their role.

Acceptance:
Scenario: Unauthorized action attempt
Given a customer attempts to access provider-only functionality
When the action is requested
Then the system denies access



3. Non-Functional Requirements

Performance / Usability:

Pages must load within 2 seconds even under typical use by academic users.
(If we set up separate testing accounts for instructors, we may allow more time.)

Availability and Reliability:

The system must be available 99 percent of the time for auditing purposes.
We can negotiate what this means for reliability. (Internal testing?)

Security and Privacy:

All user authentication and accounts must be protected from unauthorized access.
Do we care about subsequent attempts at misuse?

Other:

The system should be easy to use by anyone who has not used it before.
A first-time user should be able to browse the system and simulate purchase of an item without external help.


4. Assumptions, Constraints, and Policies

We need to define explicit or implicit assumptions, constraints (if any), and policies regarding these functions.

- Only registered and authenticated users can purchase products or leave reviews.
- No real monetary transactions will take place.
- Providers will only be able to manage products they themselves provided.
- No limits on product availability or purchases per day / week / semester.
- The app will only be used for educational purposes.


5. Milestones (course-related)

This will match course milestones yet to come.
Use existing repository structures or create new ones as needed.


M2 Requirements:
This SRS opened as an issue.

M3 High-fidelity Prototype: Core customer/provider functions are fully functional but not final.

M4 Design Architecture & Schemas defined 

M5 Backend API: Some key endpoints implemented. Unit tests defined. 

M6 Increment: Use cases completed in any state needed for review  

M7 Final: Complete system


6. Change Management

We create issues regarding alterations. Changes are reviewed and mergeable.
If changes disrupt this requirement document's accuracy but are major,
changes must also update this requirements document.

This area contains any rules related to requirements management in general.
