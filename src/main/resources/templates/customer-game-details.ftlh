<#-- this page keeps the original pc plus details layout while using the live database game and review data -->

<!doctype html>

<#-- sets the language of the page to English -->
<html lang="en">

<head>
    <#-- sets the character encoding for the page -->
    <meta charset="UTF-8" />

    <#-- this helps the page size correctly on phones and laptops -->
    <meta name="viewport" content="width=device-width,initial-scale=1" />

    <#-- sets the browser tab title using the current game title -->
    <title>PC+ | ${game.title}</title>

    <#-- tailwind is loaded from the cdn so the page can use utility classes -->
    <script src="https://cdn.tailwindcss.com"></script>

    <script>
        // this tailwind config keeps the same original theme colors
        tailwind.config = {
            theme: {
                extend: {
                    // custom colors used by the PC Plus theme
                    colors: {
                        // darkest teal background color
                        teal0: "#041a1a",

                        // dark teal page color
                        teal1: "#062626",

                        // medium teal background color
                        teal2: "#083535",

                        // brighter teal accent shade
                        teal3: "#0b4747",

                        // transparent panel color used for glass style cards
                        panel: "rgba(255,255,255,.06)",

                        // transparent border color used around cards and panels
                        stroke: "rgba(255,255,255,.10)",

                        // orange accent color used for buttons and highlights
                        accent: "#ff7a18"
                    }
                }
            }
        };
    </script>

    <#-- these lines load the custom font used for the original pc plus styling -->
    <link rel="preconnect" href="https://fonts.googleapis.com" />

    <#-- connects early to the Google font file server -->
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />

    <#-- loads the Yuji Mai font used by the PC Plus pages -->
    <link href="https://fonts.googleapis.com/css2?family=Yuji+Mai&display=swap" rel="stylesheet" />

    <#-- this file contains the shared visual styles for the mvc version of pc plus -->
    <link rel="stylesheet" href="/css/pcplus-mvc.css" />

    <style>
        /* makes dropdown options match the PC Plus theme */
        select option {
            background: #0b4747;
            color: #ffffff;
        }
    </style>

</head>

<#-- sets the default back target to the catalog page -->
<#assign resolvedBackTarget = backTarget!'/pcplus/catalog'>

<#-- sets the default back button label -->
<#assign resolvedBackLabel = backLabel!'Back to Catalog'>

<#-- if the details page was opened from the library, change the back button to return to the library -->
<#if (from!'') == 'library'>
    <#assign resolvedBackTarget = '/pcplus/library'>
    <#assign resolvedBackLabel = 'Back to Library'>
</#if>

<#-- the body uses the shared page styling from the custom stylesheet -->
<body class="pcplus-body min-h-screen text-white selection:bg-accent/30 selection:text-white">

<#-- this element is used for the soft cursor glow effect -->
<div id="cursorGlow" class="pcplus-cursor-glow"></div>

<#-- this is the sticky navigation bar at the top of the page -->
<header class="sticky top-0 z-50 border-b border-white/10 bg-black/20 backdrop-blur-xl">

    <#-- centers the navigation content and keeps it within the max page width -->
    <div class="mx-auto flex max-w-7xl items-center gap-4 px-5 py-4">

        <#-- this is the site logo and takes the customer back to the catalog -->
        <a class="font-semibold tracking-tight hover:text-white/90 transition" href="/pcplus/catalog">
            PC<span class="text-accent">+</span>
        </a>

        <#-- this navigation section takes the customer back to the main catalog or to their profile -->
        <nav class="ml-2 flex flex-1 items-center gap-1 text-sm text-white/80">

            <a class="rounded-lg px-3 py-2 hover:bg-white/5 hover:text-white transition" href="/pcplus/catalog">Home</a>

            <#-- if the customer is logged in show customer only navigation links -->
            <#if isLoggedIn>

                <#-- link to the customer library page -->
                <a class="rounded-lg px-3 py-2 hover:bg-white/5 hover:text-white transition" href="/pcplus/library">Library</a>

                <#-- link to the customer cart page and shows the cart count if there are items in the cart -->
                <a class="rounded-lg px-3 py-2 hover:bg-white/5 hover:text-white transition" href="/pcplus/cart">Cart <#if currentCartCount gt 0>(${currentCartCount})</#if></a>

                <#-- link to the customer profile page -->
                <a class="rounded-lg px-3 py-2 hover:bg-white/5 hover:text-white transition" href="/pcplus/profile">Profile</a>

                <#-- link that logs the customer out of the site -->
                <a class="rounded-lg px-3 py-2 hover:bg-white/5 hover:text-white transition" href="/pcplus/logout">Logout</a>
            <#else>

                <#-- if the customer is not logged in show authentication links instead -->
                <a class="rounded-lg px-3 py-2 hover:bg-white/5 hover:text-white transition" href="/pcplus/login">Login</a>

                <#-- link to create a new account -->
                <a class="rounded-lg px-3 py-2 hover:bg-white/5 hover:text-white transition" href="/pcplus/signup">Sign Up</a>
            </#if>

            <#-- this button takes the customer back to the correct previous section -->
            <a href="${resolvedBackTarget}" class="ml-auto hidden sm:inline-flex rounded-xl border border-white/10 bg-white/5 px-4 py-2 text-sm text-white/80 hover:bg-white/10 hover:text-white transition">
                ${resolvedBackLabel}
            </a>

            <#-- if the customer is logged in show their current avatar and display name in the navbar -->
            <#if isLoggedIn>
                <a href="/pcplus/profile" class="pcplus-nav-user ml-3 hidden sm:inline-flex">

                    <#-- displays the current avatar of the customer -->
                    <img src="${currentAvatarUrl}" alt="Current avatar" class="pcplus-nav-avatar" />

                    <#-- displays the customer’s display name (or email address if their display name is not set) -->
                    <span class="max-w-[140px] truncate text-sm text-white/85">${currentUser.displayName!currentUser.email}</span>
                </a>
            </#if>
        </nav>
    </div>
</header>

<#-- this wrapper keeps the page content both centered on the screen and provides spacing around the content -->
<main class="relative z-10 mx-auto max-w-7xl px-5 pb-16 pt-10">

    <#-- show a success message if the backend returns one -->
    <#if successMessage??>
        <div class="mb-5 rounded-2xl border border-emerald-400/20 bg-emerald-500/15 px-4 py-3 text-sm text-emerald-100">
            ${successMessage}
        </div>
    </#if>

    <#-- show an error message if the backend returns one -->
    <#if errorMessage??>
        <div class="mb-5 rounded-2xl border border-red-400/20 bg-red-500/15 px-4 py-3 text-sm text-red-100">
            ${errorMessage}
        </div>
    </#if>

    <#-- this row holds the back button and pills that contain quick information about the current game -->
    <div class="mb-6 flex items-center justify-between gap-3">

        <#-- this button takes the customer back to the correct previous section -->
        <a href="${resolvedBackTarget}" class="inline-flex items-center gap-2 rounded-xl border border-white/10 bg-white/5 px-4 py-2 text-sm text-white/80 hover:bg-white/10 hover:text-white transition">
            <span aria-hidden="true">←</span> ${resolvedBackLabel}
        </a>

        <#-- these pills show quick information about the current game -->
        <div class="flex items-center gap-2">

            <#-- displays the game genres in a pill or a fallback if the game has no genres -->
            <span class="max-w-[420px] break-words rounded-full border border-white/10 bg-white/5 px-3 py-1 text-xs text-white/70">
                ${game.genres!"General"}
            </span>

            <#-- if the game is on sale, display the sale price instead of the normal price of the game -->
            <#if game.salePrice??>
                <span class="rounded-xl bg-accent px-3 py-1 text-xs font-semibold text-white">$${game.salePrice}</span>
            <#else>
                <span class="rounded-xl bg-accent px-3 py-1 text-xs font-semibold text-white">$${game.price}</span>
            </#if>
        </div>
    </div>

    <#-- this section contains the main content related to the game along with the overview section -->
    <section class="grid gap-5 xl:grid-cols-[minmax(0,1.25fr)_minmax(320px,.9fr)]">

        <#-- this card contains the main hero area related to the current game -->
        <div class="pcplus-reveal pcplus-shine pcplus-glass rounded-3xl p-6">

            <#-- this row displays the title, description, statistics and the state of the game can be bought -->
            <div class="flex items-start justify-between gap-4">

                <#-- this side displays the game’s title, description and other quick statistics about the game -->
                <div class="min-w-0">

                    <#-- displays the title of the game -->
                    <h1 class="truncate text-3xl sm:text-4xl font-semibold tracking-tight">${game.title}</h1>

                    <#-- if the game does not have a description, display a message stating that the game does not have a description -->
                    <p class="mt-2 text-white/70">
                        ${game.description!"No description has been added for this game yet."}
                    </p>

                    <#-- these pills show quick information about the current game -->
                    <div class="mt-4 flex flex-wrap items-center gap-2 text-xs text-white/70">

                        <#-- displays the publisher of the game -->
                        <span class="rounded-full border border-white/10 bg-white/5 px-3 py-1">
                            Publisher ${game.publisher!"PC+"}
                        </span>

                        <#-- displays the average rating for the game from other customers -->
                        <span class="rounded-full border border-white/10 bg-white/5 px-3 py-1">
                            Rating ${game.avgRating} / 5
                        </span>

                        <#-- displays the total number of reviews written for the game -->
                        <span class="rounded-full border border-white/10 bg-white/5 px-3 py-1">
                            Reviews ${game.reviewCount}
                        </span>

                        <#-- displays the total number of times the game has been downloaded by other customers -->
                        <span class="rounded-full border border-white/10 bg-white/5 px-3 py-1">
                            Downloads ${game.downloadCount}
                        </span>
                    </div>
                </div>

                <#-- if the customer is logged in, show the customer their ownership of the game -->
                <#if isLoggedIn>

                    <#-- if the customer already owns the game, indicate this in the user interface -->
                    <#if owned>
                        <span class="shrink-0 rounded-xl border border-emerald-400/20 bg-emerald-500/15 px-4 py-2 text-sm text-emerald-100">
                            In Library
                        </span>

                    <#-- if the game is already in the cart, link to the cart -->
                    <#elseif inCart>
                        <a href="/pcplus/cart" class="shrink-0 rounded-xl border border-white/10 bg-white/5 px-4 py-2 text-sm text-white/80 hover:bg-white/10 hover:text-white transition">
                            In Cart
                        </a>
                    <#else>

                        <#-- if the customer does not yet own the game, indicate this in the user interface with an add to cart button -->
                        <form action="/pcplus/cart/add/${game.id}" method="post">
                            <button class="shrink-0 rounded-xl bg-accent px-4 py-2 text-sm font-semibold text-white hover:brightness-110 transition" type="submit">
                                Add to Cart
                            </button>
                        </form>
                    </#if>
                <#else>

                    <#-- if the customer is not logged in, make the add to cart button hidden and show a sign in button instead -->
                    <a href="/pcplus/login" class="shrink-0 rounded-xl bg-accent px-4 py-2 text-sm font-semibold text-white hover:brightness-110 transition">
                        Sign In
                    </a>
                </#if>
            </div>

            <#-- this block contains the main hero image for the game -->
            <div class="mt-6 overflow-hidden rounded-2xl border border-white/10 bg-black/30">

                <#-- the main hero image for the game that changes when the customer clicks on the carousel buttons -->
                <img id="heroImg" class="h-72 sm:h-96 w-full object-cover transition duration-700" src="${heroImage!}" alt="${game.title}" />
            </div>

            <#-- this row contains the game hero image carousel controls and dots -->
            <div class="mt-4 flex items-center justify-between gap-3">

                <#-- previous and next buttons for the carousel -->
                <div class="flex items-center gap-2">

                    <#-- moves the game hero image carousel to the previous image -->
                    <button id="prevImg" class="rounded-xl border border-white/10 bg-white/5 px-4 py-2 text-sm text-white/80 hover:bg-white/10 hover:text-white transition" type="button">Prev</button>

                    <#-- moves the game hero image carousel to the next image -->
                    <button id="nextImg" class="rounded-xl border border-white/10 bg-white/5 px-4 py-2 text-sm text-white/80 hover:bg-white/10 hover:text-white transition" type="button">Next</button>
                </div>

                <#-- image dot controls for the game’s hero image carousel -->
                <div id="imgDots" class="flex items-center gap-2">
                    <#list galleryImages as image>

                        <#-- each dot can be clicked to jump directly to that specific image within the game’s hero image carousel -->
                        <button
                                class="pcplus-dot <#if image?index == 0>active</#if>"
                                type="button"
                                data-image-index="${image?index}"
                                aria-label="Open image ${image?index + 1}">
                        </button>
                    </#list>
                </div>
            </div>
        </div>

        <#-- this card contains the overview of the game and its system requirements -->
        <div class="pcplus-reveal pcplus-glass rounded-3xl p-6">

            <#-- this label is used to indicate the overview section for the game -->
            <p class="text-xs tracking-[0.35em] text-white/60">OVERVIEW</p>

            <#-- this row contains the game section title and the average rating for the game from customers -->
            <div class="mt-2 flex items-center justify-between gap-3">
                <h2 class="text-xl font-semibold tracking-tight">Details</h2>
                <span class="rounded-full border border-white/10 bg-white/5 px-3 py-1 text-xs text-white/70">
                    ${game.avgRating} rating
                </span>
            </div>

            <#-- this paragraph contains the overview text for the game or a message if the game does not have one -->
            <p class="mt-3 text-sm text-white/70 leading-relaxed">
                ${game.description!"No overview has been written yet."}
            </p>

            <#-- this grid displays the details for the game in cards -->
            <div class="mt-5 grid gap-3 sm:grid-cols-2">

                <#-- card that displays the game’s genre -->
                <div class="min-w-0 rounded-2xl border border-white/10 bg-white/5 p-4">
                    <p class="text-xs tracking-[0.35em] text-white/60">GENRE</p>
                    <p class="mt-1 break-words text-sm leading-relaxed text-white/80">${game.genres!"General"}</p>
                </div>

                <#-- card that displays the game’s publisher -->
                <div class="min-w-0 rounded-2xl border border-white/10 bg-white/5 p-4">
                    <p class="text-xs tracking-[0.35em] text-white/60">PUBLISHER</p>
                    <p class="mt-1 break-words text-sm leading-relaxed text-white/80">${game.publisher!"PC+"}</p>
                </div>

                <#-- card that displays the regular price for the game -->
                <div class="min-w-0 rounded-2xl border border-white/10 bg-white/5 p-4">
                    <p class="text-xs tracking-[0.35em] text-white/60">PRICE</p>
                    <p class="mt-1 break-words text-sm leading-relaxed text-white/80">$${game.price}</p>
                </div>

                <#-- card that displays the sale price for the game -->
                <div class="min-w-0 rounded-2xl border border-white/10 bg-white/5 p-4">
                    <p class="text-xs tracking-[0.35em] text-white/60">SALE PRICE</p>
                    <p class="mt-1 break-words text-sm leading-relaxed text-white/80">

                        <#-- if the game does not have a sale price for it, display a message indicating that -->
                        <#if game.salePrice??>
                            $${game.salePrice}
                        <#else>
                            No active sale
                        </#if>
                    </p>
                </div>
            </div>

            <#-- this block contains information related to the system requirements and playability of the game -->
            <div class="mt-6 rounded-2xl border border-white/10 bg-white/5 p-5">

                <#-- this row contains information related to the system requirements for the game -->
                <div class="flex items-center justify-between gap-3">
                    <span class="text-sm text-white/70">System requirements</span>

                    <#-- if the game is playable, it will show a badge to indicate this -->
                    <#if game.playable>
                        <span class="rounded-full border border-emerald-400/20 bg-emerald-500/15 px-3 py-1 text-xs text-emerald-100">
                            Playable
                        </span>
                    <#else>

                        <#-- if the game is not playable on the browser, show a fallback badge -->
                        <span class="rounded-full border border-white/10 bg-white/5 px-3 py-1 text-xs text-white/70">
                            Not playable in browser
                        </span>
                    </#if>
                </div>

                <#-- this grid contains information related to the system requirements of the game -->
                <div class="mt-4 grid gap-3 sm:grid-cols-2">

                    <#-- system requirement for the operating system to play the game -->
                    <div>
                        <p class="text-xs tracking-[0.35em] text-white/60">OS</p>
                        <p class="mt-1 text-sm text-white/80">${game.reqOs!"Not listed"}</p>
                    </div>

                    <#-- system requirement for the cpu to play the game -->
                    <div>
                        <p class="text-xs tracking-[0.35em] text-white/60">CPU</p>
                        <p class="mt-1 text-sm text-white/80">${game.reqCpu!"Not listed"}</p>
                    </div>

                    <#-- system requirement for the amount of ram to play the game -->
                    <div>
                        <p class="text-xs tracking-[0.35em] text-white/60">RAM</p>
                        <p class="mt-1 text-sm text-white/80">${game.reqRam!"Not listed"}</p>
                    </div>

                    <#-- system requirement for the gpu to play the game -->
                    <div>
                        <p class="text-xs tracking-[0.35em] text-white/60">GPU</p>
                        <p class="mt-1 text-sm text-white/80">${game.reqGpu!"Not listed"}</p>
                    </div>

                    <#-- storage requirement for the game to play on the customer’s computer -->
                    <div class="sm:col-span-2">
                        <p class="text-xs tracking-[0.35em] text-white/60">STORAGE</p>
                        <p class="mt-1 text-sm text-white/80">${game.reqStorage!"Not listed"}</p>
                    </div>
                </div>
            </div>

            <#-- this row contains information related to the customer’s ownership of the game -->
            <div class="mt-6 flex items-center justify-between gap-3">
                <div>
                    <p class="text-xs tracking-[0.35em] text-white/60">STATUS</p>

                    <#-- this text displays the customer’s ownership of the game -->
                    <p class="mt-1 text-sm text-white/80">
                        <#if isLoggedIn>
                            <#if owned>
                                You already own this game
                            <#elseif inCart>
                                This game is in your cart
                            <#else>
                                Ready to add to cart
                            </#if>
                        <#else>
                            Sign in to buy and review
                        </#if>
                    </p>
                </div>

                <#-- if the customer is logged in, show the customer their actions related to their ownership of the game -->
                <#if isLoggedIn>

                    <#-- if the customer does not yet own the game, show an add to cart button -->
                    <#if !owned && !inCart>
                        <form action="/pcplus/cart/add/${game.id}" method="post">
                            <button class="rounded-2xl bg-accent px-5 py-3 text-sm font-semibold text-white hover:brightness-110 transition" type="submit">
                                Add to Cart
                            </button>
                        </form>
                    <#elseif inCart>

                        <#-- if the customer already owns the game, show a link to their cart -->
                        <a href="/pcplus/cart" class="rounded-2xl bg-accent px-5 py-3 text-sm font-semibold text-white hover:brightness-110 transition">
                            Open Cart
                        </a>
                    </#if>
                <#else>

                    <#-- if the customer is not logged in, make the add to cart button hidden and show the sign in button -->
                    <a href="/pcplus/login" class="rounded-2xl bg-accent px-5 py-3 text-sm font-semibold text-white hover:brightness-110 transition">
                        Sign In
                    </a>
                </#if>
            </div>
        </div>
    </section>

    <#-- this section contains information related to the reviews of the game -->
    <section class="mt-10 grid gap-5 xl:grid-cols-[minmax(0,1.25fr)_minmax(320px,.9fr)]">

        <#-- this card contains all of the reviews that have been written for the game -->
        <div class="pcplus-reveal pcplus-glass rounded-3xl p-6">

            <#-- this row contains the heading for all the reviews and the total number of reviews written for the game -->
            <div class="flex items-center justify-between gap-3">
                <div>
                    <p class="text-xs tracking-[0.35em] text-white/60">REVIEWS</p>
                    <h2 class="mt-1 text-2xl font-semibold tracking-tight">Community feedback</h2>
                </div>

                <#-- displays the total number of game reviews and adjusts to plural wording if necessary -->
                <span class="rounded-full border border-white/10 bg-white/5 px-3 py-1 text-xs text-white/70">
                    ${reviews?size} review<#if reviews?size != 1>s</#if>
                </span>
            </div>

            <#-- review list container -->
            <div class="mt-5 space-y-4">

                <#-- if the game does not yet have any reviews, display a message stating this -->
                <#if reviews?size == 0>
                    <div class="rounded-2xl border border-white/10 bg-white/5 p-5 text-sm text-white/70">
                        No reviews yet. Once a customer owns the game they can leave one here.
                    </div>
                <#else>

                    <#-- if the game does have reviews, display each of them in a row -->
                    <#list reviews as review>

                        <#-- single review card -->
                        <article class="rounded-2xl border border-white/10 bg-white/5 p-5">

                            <#-- each of these cards contain information about each review such as the reviewer’s email, the date of the review, the rating of the review and the written text of the review -->
                            <div class="flex items-start justify-between gap-4">
                                <div>

                                    <#-- displays the email address of the person who wrote the review -->
                                    <p class="text-sm font-semibold text-white">${review.userEmail}</p>

                                    <#-- displays the date on which the review was written -->
                                    <p class="mt-1 text-xs text-white/50">${review.createdAt!""}</p>
                                </div>

                                <#-- displays the rating of the game in the review -->
                                <span class="rounded-full bg-accent px-3 py-1 text-xs font-semibold text-white">
                                    ${review.rating} / 5
                                </span>
                            </div>

                            <#-- displays the text of the review written by the customer -->
                            <p class="mt-4 text-sm leading-relaxed text-white/80">${review.body}</p>

                            <#-- if the game’s publisher has written a reply to the review, display it in this element -->
                            <#if review.publisherReply?has_content>
                                <div class="mt-4 rounded-2xl border border-white/10 bg-black/20 p-4">
                                    <p class="text-xs tracking-[0.35em] text-white/60">PUBLISHER REPLY</p>
                                    <p class="mt-2 text-sm text-white/80">${review.publisherReply}</p>
                                </div>
                            </#if>
                        </article>
                    </#list>
                </#if>
            </div>
        </div>

        <#-- this card contains the form that allows customers to write a review for the game or a message that indicates that the customer cannot write reviews for the game -->
        <div class="pcplus-reveal pcplus-glass rounded-3xl p-6">

            <#-- this heading introduces the write a review section -->
            <p class="text-xs tracking-[0.35em] text-white/60">WRITE A REVIEW</p>
            <h2 class="mt-1 text-2xl font-semibold tracking-tight">Share your thoughts</h2>

            <#-- if the customer owns the game, show the write a review form -->
            <#if owned>

                <#-- this form allows the customer to write a review for the game -->
                <form class="mt-5 space-y-4" action="/pcplus/games/${game.id}/reviews" method="post">
                    <div>
                        <label class="text-sm text-white/70" for="rating">Rating</label>

                        <#-- a select menu for the customer to choose a five star rating for the game -->
                        <select
                                id="rating"
                                name="rating"
                                class="mt-2 w-full rounded-2xl border border-white/10 bg-white/5 px-4 py-3 text-sm text-white outline-none"
                                required>
                            <option value="5">5 stars</option>
                            <option value="4">4 stars</option>
                            <option value="3">3 stars</option>
                            <option value="2">2 stars</option>
                            <option value="1">1 star</option>
                        </select>
                    </div>

                    <div>
                        <label class="text-sm text-white/70" for="body">Review</label>

                        <#-- textarea that allows the customer to write the text of their review of the game -->
                        <textarea
                                id="body"
                                name="body"
                                rows="6"
                                class="mt-2 w-full rounded-2xl border border-white/10 bg-white/5 px-4 py-3 text-sm text-white outline-none placeholder:text-white/40"
                                placeholder="Write what you liked or disliked about the game"
                                required></textarea>
                    </div>

                    <#-- this button allows the customer to submit the form to leave their review -->
                    <button class="w-full rounded-2xl bg-accent px-4 py-3 text-sm font-semibold text-white hover:brightness-110 transition" type="submit">
                        Submit Review
                    </button>
                </form>
            <#else>

                <#-- if the customer does not yet own the game, show the message indicating that they cannot write a review yet -->
                <div class="mt-5 rounded-2xl border border-white/10 bg-white/5 p-5 text-sm text-white/70 leading-relaxed">

                    <#-- if the customer is logged in but does not own the game, show the message indicating that they must purchase the game to write a review -->
                    <#if isLoggedIn>
                        You need to own this game before you can write a review.
                    <#else>

                        <#-- if the customer is not logged in, show the message indicating that they must sign in and purchase the game to write a review -->
                        Sign in and purchase the game to unlock the review form.
                    </#if>
                </div>

                <#-- this block contains the action that the customer must take to unlock the review functionality -->
                <div class="mt-4">

                    <#-- if the customer is logged in, show a button to add the game to the cart -->
                    <#if isLoggedIn>
                        <#if inCart>

                            <#-- if the customer already owns the game in their cart, show a button to open their cart -->
                            <a href="/pcplus/cart" class="block w-full rounded-2xl bg-accent px-4 py-3 text-center text-sm font-semibold text-white hover:brightness-110 transition">
                                Open Cart
                            </a>
                        <#else>

                            <#-- if the customer does not yet own the game in their cart, show a form to add it to their cart -->
                            <form action="/pcplus/cart/add/${game.id}" method="post">
                                <button class="w-full rounded-2xl bg-accent px-4 py-3 text-sm font-semibold text-white hover:brightness-110 transition" type="submit">
                                    Add to Cart
                                </button>
                            </form>
                        </#if>
                    <#else>

                        <#-- if the customer is not logged in, show a sign in button instead -->
                        <a href="/pcplus/login" class="block w-full rounded-2xl bg-accent px-4 py-3 text-center text-sm font-semibold text-white hover:brightness-110 transition">
                            Sign In
                        </a>
                    </#if>
                </div>
            </#if>
        </div>
    </section>
</main>

<script>
    // this script will restore the glow and the reveal animations to the game details page that were defined in the prototype

    // this grabs the element that will allow the glow around the cursor to follow the customer
    const glow = document.getElementById("cursorGlow");

    // this event listener will listen for the movement of the customer’s mouse and update the position of the glow around the cursor and the shine on the page
    document.addEventListener("mousemove", function (event) {

        // stores the x position of the mouse in a css variable so that other elements can use it to display a shine
        document.documentElement.style.setProperty("--mx", event.clientX + "px");

        // stores the y position of the mouse in a css variable so that other elements can use it to display a shine
        document.documentElement.style.setProperty("--my", event.clientY + "px");

        // only activates if the glow element exists on the page
        if (glow) {

            // moves the glow to the current x position of the customer’s mouse cursor
            glow.style.left = event.clientX + "px";

            glow.style.top = event.clientY + "px";
        }
    });

    const revealItems = document.querySelectorAll(".pcplus-reveal");

    const revealObserver = new IntersectionObserver(function (entries) {
        entries.forEach(function (entry) {
            if (entry.isIntersecting) {
                entry.target.classList.add("in");
            }
        });
    }, { threshold: 0.08 });

    revealItems.forEach(function (item) {
        revealObserver.observe(item);
    });

    const images = [
        <#list galleryImages as image>"${image}"<#if image_has_next>, </#if></#list>
    ];

    let currentIndex = 0;

    const heroImg = document.getElementById("heroImg");
    const dots = Array.from(document.querySelectorAll("[data-image-index]"));

    function showImage(index) {
        if (!images.length) {
            return;
        }

        currentIndex = (index + images.length) % images.length;

        heroImg.src = images[currentIndex];

        dots.forEach(function (dot, dotIndex) {
            if (dotIndex === currentIndex) {
                dot.classList.add("active");
            } else {
                dot.classList.remove("active");
            }
        });
    }

    const prevButton = document.getElementById("prevImg");
    const nextButton = document.getElementById("nextImg");

    if (prevButton) {
        prevButton.addEventListener("click", function () {
            showImage(currentIndex - 1);
        });
    }

    if (nextButton) {
        nextButton.addEventListener("click", function () {
            showImage(currentIndex + 1);
        });
    }

    dots.forEach(function (dot) {
        dot.addEventListener("click", function () {
            const newIndex = Number(dot.getAttribute("data-image-index"));
            showImage(newIndex);
        });
    });
</script>

</body>
</html>