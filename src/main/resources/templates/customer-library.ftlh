<#-- this page restyles the mvc library page to match the original pc plus library page using real backend data -->
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8" />

    <#-- ensures the page scales appropriately on different devices -->
    <meta name="viewport" content="width=device-width,initial-scale=1" />

    <title>PC+ | Library</title>

    <#-- loads tailwind from the cdn to utilize utility classes -->
    <script src="https://cdn.tailwindcss.com"></script>
    <script>
        // tailwind config ensures the same customer theme is used with only changing the accent color
        tailwind.config = {
            theme: {
                extend: {
                    colors: {
                        accent: "#ff7a18"
                    }
                }
            }
        };
    </script>

    <#-- loads the font used in the original pc plus library -->
    <link rel="preconnect" href="https://fonts.googleapis.com" />
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
    <link href="https://fonts.googleapis.com/css2?family=Yuji+Mai&display=swap" rel="stylesheet" />

    <#-- stylesheet shared with other library pages to ensure consistent glow and animations -->
    <link rel="stylesheet" href="/css/pcplus-mvc.css" />
</head>

<#-- applies common styling elements to the page including background and text color -->
<body class="pcplus-body min-h-screen text-white">

<#-- element used to display glow that follows the cursor -->
<div id="cursorGlow" class="pcplus-cursor-glow"></div>

<#-- navigation bar that is always visible at the top of the screen -->
<header class="sticky top-0 z-50 border-b border-white/10 bg-black/20 backdrop-blur-xl">
    <div class="mx-auto flex max-w-7xl items-center gap-4 px-5 py-4">

        <#-- logo that returns the user to the main library catalog -->
        <a class="font-semibold tracking-tight hover:text-white/90 transition" href="/pcplus/catalog">
            PC<span class="text-accent">+</span>
        </a>

        <#-- links to the different customer pages -->
        <nav class="ml-2 flex flex-1 items-center gap-1 text-sm text-white/80">

            <#-- link to the home catalog -->
            <a class="rounded-lg px-3 py-2 hover:bg-white/5 hover:text-white transition" href="/pcplus/catalog">Home</a>

            <#-- link to the current page to highlight it -->
            <a class="rounded-lg px-3 py-2 bg-white/10 text-white transition" href="/pcplus/library">Library</a>

            <#-- link to the cart page and display the cart count when it is greater than zero -->
            <a class="rounded-lg px-3 py-2 hover:bg-white/5 hover:text-white transition" href="/pcplus/cart">Cart <#if currentCartCount gt 0>(${currentCartCount})</#if></a>

            <#-- link to the user’s profile page -->
            <a class="rounded-lg px-3 py-2 hover:bg-white/5 hover:text-white transition" href="/pcplus/profile">Profile</a>

            <#-- logout link to log the user out of the website -->
            <a class="rounded-lg px-3 py-2 hover:bg-white/5 hover:text-white transition" href="/pcplus/logout">Logout</a>

            <#-- displays the user’s avatar and their name -->
            <a href="/pcplus/profile" class="pcplus-nav-user ml-auto hidden sm:inline-flex">
                <img src="${currentAvatarUrl}" alt="Current avatar" class="pcplus-nav-avatar" />
                <span class="max-w-[140px] truncate text-sm text-white/85">
                    ${currentUser.displayName!currentUser.email}
                </span>
            </a>
        </nav>
    </div>
</header>

<#-- wrapper element used to center and provide space around the contents of the page -->
<main class="relative z-10 mx-auto max-w-7xl px-5 pb-16 pt-10">

    <#-- success message displayed to the user from the backend -->
    <#if successMessage??>
        <div class="mb-5 rounded-2xl border border-emerald-400/20 bg-emerald-500/15 px-4 py-3 text-sm text-emerald-100">
            ${successMessage}
        </div>
    </#if>

    <#-- error message displayed to the user from the backend -->
    <#if errorMessage??>
        <div class="mb-5 rounded-2xl border border-red-400/20 bg-red-500/15 px-4 py-3 text-sm text-red-100">
            ${errorMessage}
        </div>
    </#if>

    <#-- main section that displays the user’s game library -->
    <section class="pcplus-reveal pcplus-glass rounded-3xl p-8">

        <#-- section label for the game library -->
        <p class="text-xs tracking-[0.35em] text-white/60">CUSTOMER LIBRARY</p>

        <#-- heading row that contains the title of the section and the total number of games that the user owns -->
        <div class="mt-2 flex items-center justify-between gap-3">
            <h1 class="text-3xl font-semibold tracking-tight">Your Games</h1>

            <#-- displays the total number of games that the user owns -->
            <span class="rounded-full border border-white/10 bg-white/5 px-3 py-1 text-xs text-white/70">
                ${libraryGames?size} owned
            </span>
        </div>

        <#-- if the user owns no games, displays a message stating that -->
        <#if libraryGames?size == 0>
            <div class="mt-6 rounded-2xl border border-white/10 bg-white/5 p-6 text-sm text-white/70">
                Your library is empty right now. Buy a game from the catalog and it will appear here.
            </div>
        <#else>

            <#-- grid that displays each of the user’s owned games -->
            <div class="mt-6 grid gap-5 sm:grid-cols-2 xl:grid-cols-3">

                <#-- loops through each game that the user owns -->
                <#list libraryGames as item>
                    <article class="pcplus-card pcplus-shine overflow-hidden rounded-3xl border border-white/10 bg-white/5">

                        <#-- image of the game cover with a fallback image if the game does not have a cover art image -->
                        <img
                                src="${item.coverImage!'https://placehold.co/900x1200/0b1324/e5edf7?text=PC%2B'}"
                                alt="${item.title}"
                                class="h-72 w-full object-cover"
                        />

                        <div class="p-5">
                            <#-- the title of the game -->
                            <h2 class="text-xl font-semibold tracking-tight">${item.title}</h2>

                            <#-- the price at which the user purchased the game -->
                            <p class="mt-2 text-sm text-white/60">Purchased for $${item.pricePaid}</p>

                            <#-- the date on which the user purchased the game -->
                            <p class="mt-1 text-xs text-white/50">${item.purchasedAt!''}</p>

                            <#-- row at the bottom of each game card that contains the status of the game and a button to view the details of the game -->
                            <div class="mt-5 flex items-center justify-between gap-3">

                                <#-- if the game is playable in the browser displays a badge to reflect this -->
                                <#if item.playable>
                                    <span class="rounded-full border border-emerald-400/20 bg-emerald-500/15 px-3 py-1 text-xs text-emerald-100">
                                        Playable
                                    </span>
                                <#else>
                                    <#-- fallback div if the game is not playable in the browser -->
                                    <span class="rounded-full border border-white/10 bg-white/5 px-3 py-1 text-xs text-white/70">
                                        Installed externally
                                    </span>
                                </#if>

                                <#-- button that displays to view the details of the game when clicked -->
                                <a href="/pcplus/games/${item.gameId}?from=library" class="rounded-2xl bg-accent px-4 py-2 text-sm font-semibold text-white hover:brightness-110 transition">
                                    View
                                </a>
                            </div>
                        </div>
                    </article>
                </#list>
            </div>
        </#if>
    </section>
</main>

<script>
    // script that restores the glow and reveal animations shared with the other library pages

    // gets a reference to the glow element
    const glow = document.getElementById("cursorGlow");

    // updates the position of the glow element as the user moves the mouse
    document.addEventListener("mousemove", function (event) {
        document.documentElement.style.setProperty("--mx", event.clientX + "px");
        document.documentElement.style.setProperty("--my", event.clientY + "px");

        if (glow) {
            glow.style.left = event.clientX + "px";
            glow.style.top = event.clientY + "px";
        }
    });

    // selects all the elements that should animate into view
    const revealItems = document.querySelectorAll(".pcplus-reveal");

    // observer used to fade in the elements that are selected
    const revealObserver = new IntersectionObserver(function (entries) {
        entries.forEach(function (entry) {
            if (entry.isIntersecting) {
                entry.target.classList.add("in");
            }
        });
    }, { threshold: 0.08 });

    // starts observing all the elements with the reveal class applied to them
    revealItems.forEach(function (item) {
        revealObserver.observe(item);
    });
</script>

</body>
</html>