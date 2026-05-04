<#-- this page shows the customer home catalog using the original PC plus style but with real data from the backend -->

<!doctype html>

<#-- sets the language of the page to English -->
<html lang="en">

<head>
    <#-- sets the character encoding for the page -->
    <meta charset="UTF-8" />

    <#-- ensures the page renders correctly on phones and laptops -->
    <meta name="viewport" content="width=device-width,initial-scale=1" />

    <#-- title that is displayed in the browser tab -->
    <title>PC+ | Home</title>

    <#-- loads tailwind from the cdn -->
    <script src="https://cdn.tailwindcss.com"></script>

    <script>
        // adds the colors for this project to tailwind
        // these colors match the dark teal and orange theme from the original pc plus prototype
        tailwind.config = {
            theme: {
                extend: {
                    // colors used throughout the pc plus project
                    colors: {
                        // darkest teal color for backgrounds
                        teal0: "#041a1a",

                        // dark teal color for the pages
                        teal1: "#062626",

                        // medium teal color for backgrounds
                        teal2: "#083535",

                        // brighter teal color for accents
                        teal3: "#0b4747",

                        // transparent color for panels
                        panel: "rgba(255,255,255,.06)",

                        // transparent color for borders
                        stroke: "rgba(255,255,255,.10)",

                        // orange color for accents
                        accent: "#ff7a18"
                    }
                }
            }
        };
    </script>

    <#-- loads the font that was used in the original pc plus project -->
    <link rel="preconnect" href="https://fonts.googleapis.com" />

    <#-- connects to the google font file server -->
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />

    <#-- loads the Yuji Mai font that was used in the original pc plus project -->
    <link href="https://fonts.googleapis.com/css2?family=Yuji+Mai&display=swap" rel="stylesheet" />

    <#-- this file contains the visual styles for the mvc version of pc plus -->
    <link rel="stylesheet" href="/css/pcplus-mvc.css" />
</head>

<#-- applies the background color and text color to the website -->
<body class="pcplus-body min-h-screen text-white selection:bg-accent/30 selection:text-white">

<#-- adds a glow cursor to the page using javascript -->
<div id="cursorGlow" class="pcplus-cursor-glow"></div>

<#-- adds an element to display toast messages -->
<div id="toast" class="pcplus-toast"></div>

<#-- adds a sticky navigation bar -->
<header class="sticky top-0 z-50 border-b border-white/10 bg-black/20 backdrop-blur-xl">

    <#-- centers the navigation bar within the page -->
    <div class="mx-auto flex max-w-7xl items-center gap-4 px-5 py-4">

        <#-- adds the pc plus logo -->
        <a class="font-semibold tracking-tight hover:text-white/90 transition" href="/pcplus/catalog">
            PC<span class="text-accent">+</span>
        </a>

        <#-- adds the main navigation bar for the project -->
        <nav class="ml-2 flex flex-1 items-center gap-1 text-sm text-white/80">

            <#-- adds a link to the current page (home catalog) -->
            <a class="rounded-lg px-3 py-2 bg-white/10 text-white transition" href="/pcplus/catalog">Home</a>

            <#-- adds customer only links to the navigation bar -->
            <#if isCustomerLoggedIn>
                <#-- adds a link to the customer library -->
                <a class="rounded-lg px-3 py-2 hover:bg-white/5 hover:text-white transition" href="/pcplus/library">Library</a>

                <#-- adds a link to the cart (with cart count) -->
                <a class="rounded-lg px-3 py-2 hover:bg-white/5 hover:text-white transition" href="/pcplus/cart">Cart <#if currentCartCount gt 0>(${currentCartCount})</#if></a>
            </#if>

            <#-- adds a link to the profile (for logged in users) -->
            <#if isLoggedIn>
                <a class="rounded-lg px-3 py-2 hover:bg-white/5 hover:text-white transition" href="/pcplus/profile">Profile</a>
            </#if>

            <#-- adds a link to the publisher portal (only shown for publishers) -->
            <#if isPublisherLoggedIn>
                <a class="rounded-lg px-3 py-2 bg-orange-500/20 text-orange-300 hover:bg-orange-500/30 transition" href="/pcplus/publisher/dashboard">Publisher Portal</a>
            </#if>

            <#-- shows logout for logged in users or login and sign up for non-logged in users -->
            <#if isLoggedIn>
                <#-- adds a link that will log the user out of the site -->
                <a class="rounded-lg px-3 py-2 hover:bg-white/5 hover:text-white transition" href="/pcplus/logout">Logout</a>
            <#else>
                <#-- adds a link to the login page -->
                <a class="rounded-lg px-3 py-2 hover:bg-white/5 hover:text-white transition" href="/pcplus/login">Login</a>

                <#-- adds a link to the signup page -->
                <a class="rounded-lg px-3 py-2 hover:bg-white/5 hover:text-white transition" href="/pcplus/signup">Sign Up</a>
            </#if>

            <#-- adds a search bar to the navigation bar for medium screens and larger -->
            <form class="ml-auto hidden md:flex items-center gap-2 rounded-xl border border-white/10 bg-white/5 px-3 py-2" action="/pcplus/catalog" method="get">

                <#-- adds the magnifying glass icon within the search bar -->
                <svg class="h-4 w-4 text-white/70" viewBox="0 0 24 24" fill="none">
                    <path d="M21 21l-4.3-4.3m1.3-5.2a7.5 7.5 0 1 1-15 0 7.5 7.5 0 0 1 15 0Z" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                </svg>

                <#-- adds the text that will search the catalog -->
                <input
                        id="searchBox"
                        name="q"
                        value="${query!}"
                        class="w-64 bg-transparent text-sm outline-none placeholder:text-white/40"
                        placeholder="Search games..."
                />
            </form>

            <#-- shows the user avatar and display name for logged in users -->
            <#if isLoggedIn>
                <a href="/pcplus/profile" class="pcplus-nav-user ml-3 hidden sm:inline-flex">

                    <#-- shows avatar image for user -->
                    <img src="${currentAvatarUrl}" alt="Current avatar" class="pcplus-nav-avatar" />

                    <#-- shows the display name for the user or their email if they do not have a display name -->
                    <span class="max-w-[140px] truncate text-sm text-white/85">${currentUser.displayName!currentUser.email}</span>
                </a>
            </#if>
        </nav>
    </div>
</header>

<#-- the main content area for the site -->
<main class="relative z-10 mx-auto max-w-7xl px-5 pb-16 pt-10">

    <#-- displays a success message from the backend -->
    <#if successMessage??>
        <div class="mb-5 rounded-2xl border border-emerald-400/20 bg-emerald-500/15 px-4 py-3 text-sm text-emerald-100">
            ${successMessage}
        </div>
    </#if>

    <#-- displays an error message from the backend -->
    <#if errorMessage??>
        <div class="mb-5 rounded-2xl border border-red-400/20 bg-red-500/15 px-4 py-3 text-sm text-red-100">
            ${errorMessage}
        </div>
    </#if>

    <#-- adds the top area of the page that contains the featured game and the search bar -->
    <section class="grid items-start gap-5 <#if !(query?? && query?has_content)>xl:grid-cols-[minmax(0,2fr)_minmax(320px,1fr)]</#if>">

        <#-- hides the featured game section if user is searching for a specific game -->
        <#if !(query?? && query?has_content)>

            <#-- adds the featured game content area -->
            <div class="pcplus-reveal pcplus-shine pcplus-glass self-start rounded-3xl p-6">

                <#if featuredGame??>

                    <#-- adds the featured game content and image in a 2 column layout -->
                    <div class="grid items-center gap-6 lg:grid-cols-[minmax(0,1fr)_minmax(360px,.95fr)]">

                        <#-- adds the left side of the featured game section -->
                        <div class="min-w-0">

                            <#-- adds the title for the featured game -->
                            <h1 class="text-3xl sm:text-5xl font-semibold tracking-tight">${featuredGame.title}</h1>

                            <#-- adds the description for the featured game -->
                            <p class="mt-3 text-white/70 leading-relaxed">
                                ${featuredGame.description!"Browse the PC+ catalog with live data pulled directly from the backend and database."}
                            </p>

                            <#-- adds badges for the featured game publisher, rating, reviews, and price -->
                            <div class="mt-5 flex flex-wrap items-center gap-2 text-xs text-white/70">

                                <#-- adds a badge for the publisher of the featured game -->
                                <span class="rounded-full border border-white/10 bg-white/5 px-3 py-1">
                                    Publisher ${featuredGame.publisher!"PC+"}
                                </span>

                                <#-- adds a badge for the average game rating -->
                                <span class="rounded-full border border-white/10 bg-white/5 px-3 py-1">
                                    Rating ${featuredGame.avgRating}
                                </span>

                                <#-- adds a badge for the number of reviews for the featured game -->
                                <span class="rounded-full border border-white/10 bg-white/5 px-3 py-1">
                                    Reviews ${featuredGame.reviewCount}
                                </span>

                                <#-- adds the sale and normal price for the featured game -->
                                <#if featuredGame.salePrice??>
                                    <span class="rounded-full bg-accent px-3 py-1 font-semibold text-white">
                                        Sale $${featuredGame.salePrice}
                                    </span>
                                <#else>
                                    <span class="rounded-full bg-accent px-3 py-1 font-semibold text-white">
                                        Price $${featuredGame.price}
                                    </span>
                                </#if>
                            </div>

                            <#-- adds buttons for the featured game -->
                            <div class="mt-6 flex flex-wrap gap-3">

                                <#-- adds a button to open the details for the featured game -->
                                <a href="/pcplus/games/${featuredGame.id}" class="rounded-2xl bg-accent px-5 py-3 text-sm font-semibold text-white hover:brightness-110 transition">
                                    View Details
                                </a>

                                <#-- adds an add to cart button for the featured game -->
                                <#if isCustomerLoggedIn>
                                    <#-- only visible for logged in users -->
                                    <form action="/pcplus/cart/add/${featuredGame.id}" method="post">

                                        <#-- adds a button to add the featured game to cart -->
                                        <button class="rounded-2xl border border-white/10 bg-white/5 px-5 py-3 text-sm text-white/80 hover:bg-white/10 hover:text-white transition" type="submit">
                                            Add to Cart
                                        </button>
                                    </form>

                                <#elseif !isLoggedIn>
                                    <#-- adds a button to view details for the featured game -->
                                    <a href="/pcplus/login" class="rounded-2xl border border-white/10 bg-white/5 px-5 py-3 text-sm text-white/80 hover:bg-white/10 hover:text-white transition">
                                        Sign In
                                    </a>
                                </#if>
                            </div>
                        </div>

                        <#-- adds the image for the featured game -->
                        <div class="overflow-hidden rounded-3xl border border-white/10 bg-black/20">
                            <img
                                    src="${featuredImage!}"
                                    alt="${featuredGame.title}"
                                    class="h-[360px] w-full object-cover lg:h-[440px]"
                            />
                        </div>
                    </div>

                <#else>
                    <#-- adds the hero content in case there is no featured game -->
                    <div class="mt-3">
                        <h1 class="text-3xl sm:text-5xl font-semibold tracking-tight">PC+ Catalog</h1>
                        <p class="mt-3 text-white/70 leading-relaxed">
                            Your original front end look is now powered by live backend data.
                        </p>
                    </div>
                </#if>
            </div>
        </#if>

        <#-- adds the search information panel -->
        <div class="pcplus-reveal pcplus-glass self-start rounded-3xl p-6 <#if (query?? && query?has_content)>max-w-3xl</#if>">

            <#-- adds a label for the search bar -->
            <p class="text-xs tracking-[0.35em] text-white/60">SEARCH</p>

            <#-- adds a heading that changes if the user is searching for a game -->
            <h2 class="mt-2 text-xl font-semibold tracking-tight">
                <#if query?? && query?has_content>
                    Search results
                <#else>
                    Find your next game
                </#if>
            </h2>

            <#-- changes the description text based on whether the user is searching for a game -->
            <p class="mt-3 text-sm text-white/70 leading-relaxed">
                <#if query?? && query?has_content>
                    Showing results for "${query}" using the real Spring Boot controller and live database records.
                <#else>
                    This page now uses the real Spring Boot controller and real database records instead of hardcoded cards.
                </#if>
            </p>

            <#-- adds the main search bar for the site -->
            <form class="mt-5 space-y-3" action="/pcplus/catalog" method="get">

                <#-- adds an input field for the game or publisher that the user is searching for -->
                <input
                        name="q"
                        value="${query!}"
                        class="w-full rounded-2xl border border-white/10 bg-white/5 px-4 py-3 text-sm text-white outline-none placeholder:text-white/40"
                        placeholder="Search title or publisher"
                />

                <#-- adds a button to submit the search query -->
                <button class="w-full rounded-2xl bg-accent px-4 py-3 text-sm font-semibold text-white hover:brightness-110 transition" type="submit">
                    Search Catalog
                </button>
            </form>

            <#-- adds small cards that display important statistics for the site -->
            <div class="mt-6 grid gap-3 sm:grid-cols-2 xl:grid-cols-1 <#if (query?? && query?has_content)>xl:grid-cols-2</#if>">

                <#-- adds a card that displays the total of all games -->
                <div class="rounded-2xl border border-white/10 bg-white/5 p-4">
                    <p class="text-xs tracking-[0.35em] text-white/60">
                        <#if query?? && query?has_content>RESULTS<#else>TOTAL GAMES</#if>
                    </p>
                    <p class="mt-1 text-2xl font-semibold">${games?size}</p>
                </div>

                <#-- adds a card that displays the number of games on sale -->
                <div class="rounded-2xl border border-white/10 bg-white/5 p-4">
                    <p class="text-xs tracking-[0.35em] text-white/60">ON SALE</p>
                    <p class="mt-1 text-2xl font-semibold">${onSale?size}</p>
                </div>
            </div>
        </div>
    </section>

    <#-- adds a section that displays all of the games in the live catalog -->
    <section class="mt-10">

        <#-- adds a title for all games in the catalog -->
        <div class="mb-4 flex items-center justify-between gap-3">
            <div>
                <p class="text-xs tracking-[0.35em] text-white/60">ALL GAMES</p>
                <h2 class="mt-1 text-2xl font-semibold tracking-tight">Live catalog</h2>
            </div>
        </div>

        <#-- adds a grid of all game cards -->
        <div class="grid gap-5 sm:grid-cols-2 xl:grid-cols-3">

            <#-- loops through all games in the backend -->
            <#list games as game>

                <#-- adds a single game card -->
                <article class="pcplus-reveal pcplus-card pcplus-shine pcplus-glass overflow-hidden rounded-3xl">

                    <#-- adds a link to the game details page within the game card -->
                    <a href="/pcplus/games/${game.id}" class="block">

                        <#-- adds the image for the game -->
                        <img
                                src="${game.coverImage!'https://placehold.co/900x1200/0b1324/e5edf7?text=PC%2B'}"
                                alt="${game.title}"
                                class="h-72 w-full object-cover"
                        />
                    </a>

                    <#-- adds the information for the game -->
                    <div class="p-5">

                        <#-- adds the title, publisher, and price for the game -->
                        <div class="flex items-start justify-between gap-3">

                            <#-- adds the title and publisher for the game -->
                            <div class="min-w-0">
                                <h3 class="pcplus-title-clamp text-xl font-semibold tracking-tight">${game.title}</h3>
                                <p class="mt-1 text-sm text-white/60">${game.publisher!"PC+"}</p>
                            </div>

                            <#-- adds the sale price for the game if the game is on sale, otherwise the normal price -->
                            <#if game.salePrice??>
                                <span class="shrink-0 rounded-xl bg-accent px-3 py-1 text-xs font-semibold text-white">
                                    $${game.salePrice}
                                </span>
                            <#else>
                                <span class="shrink-0 rounded-xl border border-white/10 bg-white/5 px-3 py-1 text-xs text-white/80">
                                    $${game.price}
                                </span>
                            </#if>
                        </div>

                        <#-- adds the game description if available -->
                        <p class="pcplus-text-clamp mt-3 text-sm leading-relaxed text-white/70">
                            ${game.description!"No description yet."}
                        </p>

                        <#-- adds a list of the game’s genres split from the text -->
                        <div class="mt-4 flex flex-wrap gap-2">
                            <#list (game.genres!'General')?split(',') as genre>
                                <span class="rounded-full border border-white/10 bg-white/5 px-3 py-1 text-xs text-white/70">
                                    ${genre?trim}
                                </span>
                            </#list>
                        </div>

                        <#-- adds a row of the game’s rating and action buttons -->
                        <div class="mt-5 flex items-center justify-between gap-3">

                            <#-- adds the game’s rating and number of reviews -->
                            <div class="text-xs text-white/60">
                                Rating ${game.avgRating} • Reviews ${game.reviewCount}
                            </div>

                            <#-- adds buttons to view the game details or add it to the cart -->
                            <div class="flex items-center gap-2">

                                <#-- adds the link to view the details for the game -->
                                <a href="/pcplus/games/${game.id}" class="rounded-2xl bg-accent px-4 py-2 text-sm font-semibold text-white hover:brightness-110 transition">
                                    Details
                                </a>

                                <#-- adds a link to the add to cart function for customers -->
                                <#if isCustomerLoggedIn>
                                    <form action="/pcplus/cart/add/${game.id}" method="post">
                                        <button class="rounded-2xl border border-white/10 bg-white/5 px-4 py-2 text-sm text-white/80 hover:bg-white/10 hover:text-white transition" type="submit">
                                            Add
                                        </button>
                                    </form>
                                </#if>
                            </div>
                        </div>
                    </div>
                </article>
            </#list>
        </div>
    </section>

    <#-- adds a section for the top sellers of games on the site -->
    <section class="mt-12">

        <#-- adds the title for the top sellers of games -->
        <div class="mb-4">
            <p class="text-xs tracking-[0.35em] text-white/60">TOP SELLERS</p>
            <h2 class="mt-1 text-2xl font-semibold tracking-tight">Most downloaded</h2>
        </div>

        <#-- adds a grid of the top selling games on the site -->
        <div class="grid gap-5 md:grid-cols-3">

            <#-- loops through the top seller games that are returned from the backend -->
            <#list topSellers as game>

                <#-- adds the individual top seller game cards -->
                <article class="pcplus-reveal pcplus-glass overflow-hidden rounded-3xl">

                    <#-- adds the cover image for the top selling game -->
                    <img
                            src="${game.coverImage!'https://placehold.co/900x1200/0b1324/e5edf7?text=PC%2B'}"
                            alt="${game.title}"
                            class="h-52 w-full object-cover"
                    />

                    <#-- adds the content of the top selling game -->
                    <div class="p-5">

                        <#-- adds the title of the top selling game -->
                        <h3 class="text-lg font-semibold">${game.title}</h3>

                        <#-- adds the name of the publisher of the top selling game -->
                        <p class="mt-1 text-sm text-white/60">${game.publisher!"PC+"}</p>

                        <div class="mt-4 flex items-center justify-between">

                            <#-- adds the number of times it has been downloaded -->
                            <span class="text-xs text-white/60">Downloads ${game.downloadCount}</span>

                            <#-- adds a link to view the details for the top selling game -->
                            <a href="/pcplus/games/${game.id}" class="rounded-2xl border border-white/10 bg-white/5 px-4 py-2 text-sm text-white/80 hover:bg-white/10 hover:text-white transition">
                                Details
                            </a>
                        </div>
                    </div>
                </article>
            </#list>
        </div>
    </section>

    <#-- adds a section for the newest and sale games on the site -->
    <section class="mt-12 grid gap-5 lg:grid-cols-2">

        <#-- adds the title for the newest games section -->
        <div class="pcplus-reveal pcplus-glass rounded-3xl p-6">

            <#-- adds the header for the newest games on the site -->
            <div class="mb-4">
                <p class="text-xs tracking-[0.35em] text-white/60">NEWEST</p>
                <h2 class="mt-1 text-2xl font-semibold tracking-tight">Latest additions</h2>
            </div>

            <#-- adds a list of the newest games on the site -->
            <div class="space-y-4">

                <#-- loops through the newest games that are returned from the backend -->
                <#list newest as game>

                    <#-- adds a list item that links to the details page for the game -->
                    <a href="/pcplus/games/${game.id}" class="flex items-center gap-4 rounded-2xl border border-white/10 bg-white/5 p-3 hover:bg-white/10 transition">

                        <#-- adds a small image of the newest game -->
                        <img
                                src="${game.coverImage!'https://placehold.co/400x500/0b1324/e5edf7?text=PC%2B'}"
                                alt="${game.title}"
                                class="h-24 w-20 rounded-2xl object-cover"
                        />

                        <#-- adds the text information of the newest game -->
                        <div class="min-w-0">

                            <#-- adds the title of the newest game -->
                            <p class="truncate text-base font-semibold">${game.title}</p>

                            <#-- adds the publisher of the newest game -->
                            <p class="mt-1 text-sm text-white/60">${game.publisher!"PC+"}</p>

                            <#-- adds a small helper text for the newest game -->
                            <p class="mt-2 text-xs text-white/60">Added recently to the live catalog</p>
                        </div>
                    </a>
                </#list>
            </div>
        </div>

        <#-- adds a section for the sale games on the site -->
        <div class="pcplus-reveal pcplus-glass rounded-3xl p-6">

            <#-- adds the title for the sale games section -->
            <div class="mb-4">
                <p class="text-xs tracking-[0.35em] text-white/60">ON SALE</p>
                <h2 class="mt-1 text-2xl font-semibold tracking-tight">Current deals</h2>
            </div>

            <#-- adds a list of the games on sale -->
            <div class="space-y-4">

                <#-- loops through the sale game information that is returned from the backend -->
                <#list onSale as game>

                    <#-- adds a link to the details page for the sale game -->
                    <a href="/pcplus/games/${game.id}" class="flex items-center justify-between gap-4 rounded-2xl border border-white/10 bg-white/5 p-4 hover:bg-white/10 transition">

                        <#-- adds the title and publisher for the sale game -->
                        <div class="min-w-0">
                            <p class="truncate text-base font-semibold">${game.title}</p>
                            <p class="mt-1 text-sm text-white/60">${game.publisher!"PC+"}</p>
                        </div>

                        <#-- adds the sale price for the game and the regular price -->
                        <div class="text-right">
                            <p class="text-xs text-white/50 line-through">$${game.price}</p>
                            <p class="text-sm font-semibold text-accent">$${game.salePrice}</p>
                        </div>
                    </a>
                </#list>
            </div>
        </div>
    </section>
</main>

<script>
    const glow = document.getElementById("cursorGlow");

    document.addEventListener("mousemove", function (event) {
        document.documentElement.style.setProperty("--mx", event.clientX + "px");
        document.documentElement.style.setProperty("--my", event.clientY + "px");

        if (glow) {
            glow.style.left = event.clientX + "px";
            glow.style.top = event.clientY + "px";
        }
    });

    // selects all elements that should be revealed when scrolling into view
    const revealItems = document.querySelectorAll(".pcplus-reveal");

    // creates an observer that detects when elements enter the viewport
    const revealObserver = new IntersectionObserver(function (entries) {
        entries.forEach(function (entry) {
            if (entry.isIntersecting) {
                entry.target.classList.add("in");
            }
        });
    }, { threshold: 0.08 });

    // adds an observer to all elements with the class reveal
    revealItems.forEach(function (item) {

        // adds an observer to this specific element
        revealObserver.observe(item);
    });
</script>

</body>
</html>