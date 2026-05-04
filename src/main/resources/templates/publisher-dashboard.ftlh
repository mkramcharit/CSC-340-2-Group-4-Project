<!DOCTYPE html>

<#-- sets the language of the page to English -->
<html lang="en">

<head>
    <#-- sets the character encoding for the page -->
    <meta charset="UTF-8">

    <#-- helps the page display correctly on phones, tablets, and laptops -->
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <#-- title that is displayed in the browser tab -->
    <title>PC+ Publisher Dashboard</title>

    <#-- loads Tailwind CSS from the CDN -->
    <script src="https://cdn.tailwindcss.com"></script>

    <#-- loads the shared stylesheet for the PC Plus MVC pages -->
    <link rel="stylesheet" href="/css/pcplus-mvc.css">

    <style>
        /* applies the color theme for the publisher to the page */
        body { background: radial-gradient(circle at top left, #3b0b0b, #120913 45%, #08070c); color: #fff7ed; }

        /* glass style card that is used for the publisher's dashboard panels */
        .pub-glass { background: rgba(28, 13, 16, 0.78); border: 1px solid rgba(248, 113, 22, 0.35); box-shadow: 0 24px 80px rgba(239, 68, 68, 0.16); }

        /* button with an orange and red gradient that is used on the publisher dashboard */
        .pub-btn { background: linear-gradient(135deg, #f97316, #ef4444); color: white; }

        /* buttons for the publisher are slightly brightened when hovered over */
        .pub-btn:hover { filter: brightness(1.12); }

        /* input element with shared style that is used within the publisher's dashboard forms */
        .pub-input { background: rgba(255,255,255,0.08); border: 1px solid rgba(251,146,60,0.35); color: white; }

        /* styles the container for the publisher's avatar and display name within the navigation bar */
        .pub-nav-user { display: inline-flex; align-items: center; gap: 0.6rem; border: 1px solid rgba(251,146,60,0.35); background: rgba(255,255,255,0.08); border-radius: 999px; padding: 0.35rem 0.75rem 0.35rem 0.35rem; }

        /* styles the publisher's avatar image within the navigation bar */
        .pub-nav-avatar { height: 2rem; width: 2rem; border-radius: 999px; background: rgba(255,255,255,0.12); object-fit: cover; }
    </style>
</head>

<#-- body that contains the content for the publisher's dashboard -->
<body class="min-h-screen">

<#-- navigation bar for the publisher that remains visible on the screen -->
<nav class="sticky top-0 z-40 border-b border-orange-500/30 bg-black/50 backdrop-blur-xl">

    <#-- navigation bar content that centers the content and separates the logo from the links -->
    <div class="mx-auto flex max-w-7xl items-center justify-between px-6 py-4">

        <#-- link to the publisher's logo -->
        <a href="/pcplus/publisher/dashboard" class="text-2xl font-black tracking-wide text-orange-300">PC+ Publisher</a>

        <#-- links to the actions available to the publisher -->
        <div class="flex items-center gap-4 text-sm">

            <#-- link to the catalog that is visible to the customers -->
            <a href="/pcplus/catalog" class="text-orange-100/80 hover:text-orange-300">Customer Catalog</a>

            <#-- link to the form for the publisher to create a new game -->
            <a href="/pcplus/publisher/games/new" class="rounded-full pub-btn px-4 py-2 font-bold">Add Game</a>

            <#-- link to the publisher's profile -->
            <a href="/pcplus/profile" class="pub-nav-user">

                <#-- displays the publisher's avatar -->
                <img src="${currentAvatarUrl}" alt="Publisher avatar" class="pub-nav-avatar">

                <#-- displays the publisher's display name or email address -->
                <span class="max-w-[150px] truncate text-orange-100">${currentUser.displayName!currentUser.email}</span>
            </a>

            <#-- link to log the publisher out of the publisher's account -->
            <a href="/pcplus/publisher/logout" class="text-red-200 hover:text-red-100">Logout</a>
        </div>
    </div>
</nav>

<#-- container for the main content of the publisher's dashboard -->
<main class="mx-auto max-w-7xl px-6 py-10">

    <#-- displays a success message from the backend if there is one -->
    <#if successMessage??>
        <div class="mb-6 rounded-2xl border border-orange-400/40 bg-orange-500/15 px-5 py-4 text-orange-100">${successMessage}</div>
    </#if>

    <#-- displays an error message from the backend if there is one -->
    <#if errorMessage??>
        <div class="mb-6 rounded-2xl border border-red-400/40 bg-red-500/15 px-5 py-4 text-red-100">${errorMessage}</div>
    </#if>

    <#-- introduction card to the publisher's main dashboard -->
    <section class="pub-glass rounded-3xl p-8">

        <#-- heading for the publisher's main dashboard -->
        <h1 class="text-4xl font-black">${currentUser.displayName!currentUser.email}'s Dashboard</h1>

        <#-- description of the publisher's main dashboard -->
        <p class="mt-3 max-w-3xl text-orange-100/75">Manage only the games owned by this publisher account. All game changes, review removals, and sales totals use the live Neon database.</p>
    </section>

    <#-- cards that display the publisher's key statistics -->
    <section class="mt-8 grid gap-5 md:grid-cols-5">

        <#-- total number of games that are owned by this publisher -->
        <div class="pub-glass rounded-3xl p-5"><p class="text-orange-200/70">Games</p><p class="mt-2 text-3xl font-black">${stats.totalGames}</p></div>

        <#-- total number of sales that have been made for games published by this publisher -->
        <div class="pub-glass rounded-3xl p-5"><p class="text-orange-200/70">Sales</p><p class="mt-2 text-3xl font-black">${stats.totalSales}</p></div>

        <#-- total amount of revenue that has been earned by this publisher -->
        <div class="pub-glass rounded-3xl p-5"><p class="text-orange-200/70">Revenue</p><p class="mt-2 text-3xl font-black">$${stats.totalRevenue}</p></div>

        <#-- total number of reviews for the games that are published by this publisher -->
        <div class="pub-glass rounded-3xl p-5"><p class="text-orange-200/70">Reviews</p><p class="mt-2 text-3xl font-black">${stats.totalReviews}</p></div>

        <#-- average rating of the games that are published by this publisher -->
        <div class="pub-glass rounded-3xl p-5"><p class="text-orange-200/70">Avg Rating</p><p class="mt-2 text-3xl font-black">${stats.avgRating}</p></div>
    </section>

    <#-- section that lists all of the games that are owned by the publisher -->
    <section class="mt-10">

        <#-- header row for the section that lists all of the games that are published by the publisher -->
        <div class="mb-4 flex items-center justify-between">
            <h2 class="text-2xl font-black">My Published Games</h2>

            <#-- shortcut link to add a new game to the publisher’s catalog -->
            <a href="/pcplus/publisher/games/new" class="rounded-full pub-btn px-5 py-2 font-bold">Add New Game</a>
        </div>

        <#-- container for the game cards that are published by the publisher -->
        <div class="grid gap-5">

            <#-- message displayed if the publisher has not created any games -->
            <#if games?size == 0>
                <div class="pub-glass rounded-3xl p-6 text-orange-100/75">No games yet. Add your first game to start selling.</div>
            </#if>

            <#-- loops through all of the games that are owned by this publisher -->
            <#list games as game>

                <#-- individual game card that is published by the publisher -->
                <article class="pub-glass grid gap-5 rounded-3xl p-5 md:grid-cols-[160px_1fr_auto]">

                    <#-- image for the game that is published or a placeholder image if there is no cover image for this game -->
                    <img src="${game.coverImage!'https://placehold.co/400x520/3b0b0b/f97316?text=PC%2B'}" class="h-52 w-full rounded-2xl object-cover md:h-40" alt="${game.title}">

                    <#-- information about the published game -->
                    <div>

                        <#-- status for the game that displays as live if there is no status for the game -->
                        <p class="text-sm uppercase tracking-[0.25em] text-orange-300">${game.status!'live'}</p>

                        <#-- title for the game -->
                        <h3 class="mt-1 text-2xl font-black">${game.title}</h3>

                        <#-- description for the game or a message if there is no description for the game -->
                        <p class="mt-2 text-orange-100/70">${game.description!'No description yet.'}</p>

                        <#-- genres for the game -->
                        <p class="mt-3 text-sm text-orange-200/80">Genres: ${game.genres!''}</p>

                        <#-- regular price and sale price for the game if the game is on sale -->
                        <p class="mt-1 text-sm text-orange-200/80">Price: $${game.price} <#if game.salePrice??> Sale: $${game.salePrice}</#if></p>
                    </div>

                    <#-- buttons for editing or removing the published game -->
                    <div class="flex flex-col gap-3 md:w-36">

                        <#-- link to edit the published game -->
                        <a href="/pcplus/publisher/games/${game.id}/edit" class="rounded-full pub-btn px-4 py-2 text-center font-bold">Edit</a>

                        <#-- form for removing the published game from the publisher’s dashboard -->
                        <form method="post" action="/pcplus/publisher/games/${game.id}/delete">

                            <#-- button for removing the published game from the publisher’s dashboard -->
                            <button class="w-full rounded-full border border-red-400/50 px-4 py-2 font-bold text-red-100 hover:bg-red-500/20" type="submit">Remove</button>
                        </form>
                    </div>
                </article>
            </#list>
        </div>
    </section>

    <#-- section that displays the total sales for each of the publisher’s games -->
    <section class="mt-10">

        <#-- heading for the section that displays the sales for each of the publisher’s games -->
        <h2 class="mb-4 text-2xl font-black">Sales By Game</h2>

        <#-- wrapper for a table that lists the sales of each game that is published by the publisher -->
        <div class="overflow-hidden rounded-3xl border border-orange-500/30">

            <#-- table that lists each game that is published by the publisher and the total sales for each game -->
            <table class="w-full bg-black/30 text-left text-sm">

                <#-- table header row -->
                <thead class="bg-orange-500/15 text-orange-200"><tr><th class="p-4">Game</th><th class="p-4">Sold</th><th class="p-4">Revenue</th></tr></thead>

                <tbody>
                <#-- rows for each game within the publisher’s catalog -->
                <#list salesRows as row>

                    <#-- row for the sales information for each game -->
                    <tr class="border-t border-orange-500/20"><td class="p-4 font-bold">${row.gameTitle}</td><td class="p-4">${row.salesCount}</td><td class="p-4">$${row.revenue}</td></tr>
                </#list>
                </tbody>
            </table>
        </div>
    </section>

    <#-- section for the reviews for each game that is published by the publisher -->
    <section id="reviews" class="mt-10">

        <#-- heading for the section dedicated to the reviews of the games that are published by the publisher -->
        <h2 class="mb-4 text-2xl font-black">Reviews For My Games</h2>

        <#-- container for the individual review cards -->
        <div class="grid gap-5">

            <#-- message displayed if none of the publisher’s games have any reviews yet -->
            <#if reviews?size == 0>
                <div class="pub-glass rounded-3xl p-6 text-orange-100/75">No active reviews yet.</div>
            </#if>

            <#-- loops through each of the review for the games that are published by the publisher -->
            <#list reviews as review>

                <#-- individual review card for the games that are published by the publisher -->
                <article class="pub-glass rounded-3xl p-5">

                    <#-- information about the review and button to delete the review -->
                    <div class="flex flex-wrap items-start justify-between gap-4">

                        <#-- the text of the review and information for the game that the review is for -->
                        <div>

                            <#-- the title of the game that the review is for -->
                            <p class="text-sm uppercase tracking-[0.25em] text-orange-300">${review.gameTitle}</p>

                            <#-- the rating of the game and the email address of the person that wrote the review -->
                            <h3 class="mt-1 text-xl font-black">${review.rating}/5 from ${review.userEmail}</h3>

                            <#-- the body of the review -->
                            <p class="mt-3 text-orange-100/75">${review.body}</p>

                            <#-- the reply to the review if one exists -->
                            <#if review.publisherReply?has_content><p class="mt-3 rounded-2xl bg-orange-500/10 p-3 text-orange-100">Reply: ${review.publisherReply}</p></#if>
                        </div>

                        <#-- form for deleting the selected review -->
                        <form method="post" action="/pcplus/publisher/reviews/${review.id}/delete">

                            <#-- button to delete the selected review -->
                            <button class="rounded-full border border-red-400/50 px-4 py-2 font-bold text-red-100 hover:bg-red-500/20" type="submit">Delete Review</button>
                        </form>
                    </div>

                    <#-- form to create or edit the publisher’s reply to the selected review -->
                    <form method="post" action="/pcplus/publisher/reviews/${review.id}/reply" class="mt-4 flex gap-3">

                        <#-- input field for the publisher to write their reply to the game review -->
                        <input class="pub-input flex-1 rounded-full px-4 py-2" name="reply" placeholder="Write or update publisher reply" value="${review.publisherReply!''}">

                        <#-- button to save the publisher’s reply to the game review -->
                        <button class="rounded-full pub-btn px-5 py-2 font-bold" type="submit">Save Reply</button>
                    </form>
                </article>
            </#list>
        </div>
    </section>
</main>
</body>
</html>