<#-- this page displays the cart and checkout process for the shop’s products -->

<!doctype html>

<#-- sets the language of the webpage to English -->
<html lang="en">

<head>

    <meta charset="UTF-8" />

    <#-- ensures the website displays correctly on mobile devices and laptops -->

    <meta name="viewport" content="width=device-width,initial-scale=1" />

    <#-- the page’s title that is displayed in the browser tab -->
    <title>PC+ | Cart</title>

    <#-- loads tailwind css from the CDN -->

    <script src="https://cdn.tailwindcss.com"></script>

    <script>

        // maintains the same theme as the project

        tailwind.config = {

            theme: {

                extend: {

                    // colors defined by the PC Plus pages
                    colors: {

                        // darkest teal color used for the background of the website
                        teal0: "#041a1a",

                        // dark teal color used for the pages of the website
                        teal1: "#062626",

                        // medium teal color used for backgrounds of elements
                        teal2: "#083535",

                        // brighter teal color used for accents on the website
                        teal3: "#0b4747",

                        // transparent color of panels on the website
                        panel: "rgba(255,255,255,.06)",

                        // transparent color of borders of elements on the website
                        stroke: "rgba(255,255,255,.10)",

                        // orange color used for buttons on the website
                        accent: "#ff7a18"

                    }

                }

            }

        };

    </script>

    <#-- loads the font that will be used on the PC Plus pages -->

    <link rel="preconnect" href="https://fonts.googleapis.com" />

    <#-- early connection to the Google font servers -->
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />

    <#-- loads the Yuji Mai font that will be used on the website -->
    <link href="https://fonts.googleapis.com/css2?family=Yuji+Mai&display=swap" rel="stylesheet" />

    <#-- shared visual styles between the pages of the MVC version of PC Plus -->

    <link rel="stylesheet" href="/css/pcplus-mvc.css" />

</head>

<#-- adds the shared styling from the custom stylesheet to the website -->

<body class="pcplus-body min-h-screen text-white selection:bg-accent/30 selection:text-white">

<#-- adds the element that displays the glow for the cursor element on the screen -->

<div id="cursorGlow" class="pcplus-cursor-glow"></div>

<#-- creates the sticky navigation bar at the top of the screen -->

<header class="sticky top-0 z-50 border-b border-white/10 bg-black/20 backdrop-blur-xl">

    <#-- centers the navigation bar and its elements -->
    <div class="mx-auto flex max-w-7xl items-center gap-4 px-5 py-4">

        <#-- creates the logo that takes the customer back to the main shop catalog -->

        <a class="font-semibold tracking-tight hover:text-white/90 transition" href="/pcplus/catalog">

            PC<span class="text-accent">+</span>

        </a>

        <#-- links to the main shop pages and the customer’s avatar -->

        <nav class="ml-2 flex flex-1 items-center gap-1 text-sm text-white/80">

            <#-- link to the customer catalog -->
            <a class="rounded-lg px-3 py-2 hover:bg-white/5 hover:text-white transition" href="/pcplus/catalog">Home</a>

            <#-- link to the customer library -->
            <a class="rounded-lg px-3 py-2 hover:bg-white/5 hover:text-white transition" href="/pcplus/library">Library</a>

            <#-- link to the cart page and displays the number of items in the cart -->
            <a class="rounded-lg px-3 py-2 bg-white/10 text-white transition" href="/pcplus/cart">Cart <#if currentCartCount gt 0>(${currentCartCount})</#if></a>

            <#-- link to the customer profile page -->
            <a class="rounded-lg px-3 py-2 hover:bg-white/5 hover:text-white transition" href="/pcplus/profile">Profile</a>

            <#-- link that will log the customer out of the website -->
            <a class="rounded-lg px-3 py-2 hover:bg-white/5 hover:text-white transition" href="/pcplus/logout">Logout</a>

            <#-- avatar and display name for the customer that links back to their profile -->
            <a href="/pcplus/profile" class="pcplus-nav-user ml-auto hidden sm:inline-flex">

                <#-- display avatar chosen by the customer -->
                <img src="${currentAvatarUrl}" alt="Current avatar" class="pcplus-nav-avatar" />

                <#-- displays the customer’s display name or their email -->
                <span class="max-w-[140px] truncate text-sm text-white/85">${currentUser.displayName!currentUser.email}</span>

            </a>

        </nav>

    </div>

</header>

<#-- displays the content of the page in the center of the screen -->

<main class="relative z-10 mx-auto max-w-7xl px-5 pb-16 pt-10">

    <#-- displays a message if the customer successfully completed some action -->

    <#if successMessage??>

        <#-- green message displays success messages from the backend -->
        <div class="mb-5 rounded-2xl border border-emerald-400/20 bg-emerald-500/15 px-4 py-3 text-sm text-emerald-100">

            ${successMessage}

        </div>

    </#if>

    <#-- red message displays error messages from the backend -->

    <#if errorMessage??>

        <div class="mb-5 rounded-2xl border border-red-400/20 bg-red-500/15 px-4 py-3 text-sm text-red-100">

            ${errorMessage}

        </div>

    </#if>

    <#-- creates the two column layout for the cart and checkout -->
    <section class="grid gap-5 xl:grid-cols-[minmax(0,1.3fr)_360px]">

        <#-- left side of the two column layout displays the items in the cart -->
        <div class="pcplus-reveal pcplus-glass rounded-3xl p-6">

            <#-- section label for the cart items -->
            <p class="text-xs tracking-[0.35em] text-white/60">CART</p>

            <#-- cart items title and item count -->
            <div class="mt-2 flex items-center justify-between gap-3">

                <h1 class="text-3xl font-semibold tracking-tight">Your Cart</h1>

                <#-- displays the number of items in the cart -->
                <span class="rounded-full border border-white/10 bg-white/5 px-3 py-1 text-xs text-white/70">

                    ${cartItems?size} item<#if cartItems?size != 1>s</#if>

                </span>

            </div>

            <#-- checks if there are items in the cart -->
            <#if cartItems?size == 0>

                <#-- message displayed if there are no items in the cart -->
                <div class="mt-6 rounded-2xl border border-white/10 bg-white/5 p-6 text-sm text-white/70">

                    Your cart is empty. Add a game from the catalog to continue.

                </div>

            <#else>

                <#-- displays the items in the cart if there are items -->
                <div class="mt-6 space-y-4">

                    <#-- loops through each item in the cart -->
                    <#list cartItems as item>

                        <#-- card for each item in the cart -->
                        <article class="flex gap-4 rounded-2xl border border-white/10 bg-white/5 p-4">

                            <#-- image of the game in the cart or a placeholder image -->
                            <img

                                    src="${item.coverImage!'https://placehold.co/400x500/0b1324/e5edf7?text=PC%2B'}"

                                    alt="${item.title}"

                                    class="h-28 w-24 rounded-2xl object-cover"

                            />

                            <#-- title, checkout status, and price of the game in the cart -->
                            <div class="min-w-0 flex-1">

                                <#-- title of the game in the cart -->
                                <h2 class="truncate text-lg font-semibold">${item.title}</h2>

                                <#-- status text indicating that the game can be bought -->
                                <p class="mt-2 text-sm text-white/60">Ready for checkout</p>

                                <#-- price of the game in cart -->
                                <p class="mt-3 text-sm font-semibold text-white">$${item.salePrice}</p>

                            </div>

                            <#-- form used to remove one game from the cart -->
                            <form action="/pcplus/cart/remove/${item.gameId}" method="post">

                                <#-- button used to remove game from cart -->
                                <button class="rounded-2xl border border-white/10 bg-white/5 px-4 py-2 text-sm text-white/80 hover:bg-white/10 hover:text-white transition" type="submit">

                                    Remove

                                </button>

                            </form>

                        </article>

                    </#list>

                </div>

            </#if>

        </div>

        <#-- right side of two column layout displays checkout information and button -->
        <div class="pcplus-reveal pcplus-glass rounded-3xl p-6">

            <#-- section label for checkout content -->
            <p class="text-xs tracking-[0.35em] text-white/60">CHECKOUT</p>

            <#-- checkout title and information for checkout -->
            <h2 class="mt-2 text-2xl font-semibold tracking-tight">Order Summary</h2>

            <#-- displays the item and cart total -->
            <div class="mt-6 space-y-4">

                <#-- item and item count -->
                <div class="flex items-center justify-between text-sm text-white/70">

                    <span>Items</span>

                    <span>${cartItems?size}</span>

                </div>

                <#-- cart total -->
                <div class="flex items-center justify-between text-sm text-white/70">

                    <span>Total</span>

                    <span>$${cartTotal}</span>

                </div>

            </div>

            <#-- explains what happens during checkout on the database -->
            <div class="mt-6 rounded-2xl border border-white/10 bg-white/5 p-4 text-sm text-white/70">

                Checkout moves all cart items into the library and updates the purchase records in the database.

            </div>

            <#-- only allows checkout if there is at least one item in the cart -->
            <#if cartItems?size gt 0>

                <#-- form used to complete the checkout process for the cart -->
                <form class="mt-6" action="/pcplus/cart/checkout" method="post">

                    <#-- checkout button that is active when cart has items -->
                    <button class="w-full rounded-2xl bg-accent px-4 py-3 text-sm font-semibold text-white hover:brightness-110 transition" type="submit">

                        Checkout

                    </button>

                </form>

            <#else>

                <#-- disabled checkout button if there are no items in cart -->
                <button class="mt-6 w-full rounded-2xl border border-white/10 bg-white/5 px-4 py-3 text-sm text-white/40" type="button" disabled>

                    Checkout

                </button>

            </#if>

        </div>

    </section>

</main>

<script>

    // loads the script that will restore the glow and reveal effects for the website

    // gets the glow element for the cursor
    const glow = document.getElementById("cursorGlow");

    // listens for mouse movements anywhere on the page
    document.addEventListener("mousemove", function (event) {

        // stores the current x position of the mouse
        document.documentElement.style.setProperty("--mx", event.clientX + "px");

        // stores the current y position of the mouse
        document.documentElement.style.setProperty("--my", event.clientY + "px");

        // only if the glow element is on the page
        if (glow) {

            // moves the cursor glow element to the current x position of the mouse
            glow.style.left = event.clientX + "px";

            // updates the y position of the glow element to that of the cursor
            glow.style.top = event.clientY + "px";

        }

    });

    // selects all elements that should have the reveal effect
    const revealItems = document.querySelectorAll(".pcplus-reveal");

    // adds the reveal class to the elements when they are visible on screen
    const revealObserver = new IntersectionObserver(function (entries) {

        // loops through each element
        entries.forEach(function (entry) {

            // checks if each element is visible on screen
            if (entry.isIntersecting) {

                // adds in class so that the element gains the reveal styling
                entry.target.classList.add("in");

            }

        });

    }, { threshold: 0.08 });

    // begins to observe each element on the page that has the reveal class
    revealItems.forEach(function (item) {

        // tells the observer to watch the specific item in question
        revealObserver.observe(item);

    });

</script>

</body>

</html>