<!DOCTYPE html>

<#-- sets the language of the page to English -->
<html lang="en">

<head>
    <#-- sets the character encoding for the page -->
    <meta charset="UTF-8">

    <#-- helps the page display correctly on phones, tablets, and laptops -->
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <#-- title that is shown in the browser tab -->
    <title>${pageTitle} - PC+ Publisher</title>

    <#-- loads Tailwind CSS from the CDN -->
    <script src="https://cdn.tailwindcss.com"></script>

    <#-- loads the shared stylesheet for the PC Plus MVC pages -->
    <link rel="stylesheet" href="/css/pcplus-mvc.css">

    <style>
        /* applies the color theme of the publisher to the page */
        body { background: radial-gradient(circle at top left, #3b0b0b, #120913 45%, #08070c); color: #fff7ed; }

        /* glass style card that contains the publisher form panel */
        .pub-glass { background: rgba(28, 13, 16, 0.78); border: 1px solid rgba(248, 113, 22, 0.35); box-shadow: 0 24px 80px rgba(239, 68, 68, 0.16); }

        /* button with orange and red gradient that is used to save the publisher */
        .pub-btn { background: linear-gradient(135deg, #f97316, #ef4444); color: white; }

        /* shared input style that is used for all of the text fields */
        .pub-input { background: rgba(255,255,255,0.08); border: 1px solid rgba(251,146,60,0.35); color: white; }

        /* dropdown menu options match the theme of the publisher dashboard */
        .pub-input option { background: #1c0d10; color: #fff7ed; }

        /* all form labels are styled to be easy to read */
        label { color: rgba(254, 215, 170, 0.88); font-weight: 700; }
    </style>
</head>

<#-- the body contains the page that contains the publisher game form -->
<body class="min-h-screen">

<#-- sticky navigation bar that contains the publisher navigation links -->
<nav class="sticky top-0 z-40 border-b border-orange-500/30 bg-black/50 backdrop-blur-xl">

    <#-- navigation bar content is centered and contains the logo and link back to the publisher dashboard -->
    <div class="mx-auto flex max-w-7xl items-center justify-between px-6 py-4">

        <#-- link that displays the brand name of the publisher and returns to the publisher dashboard -->
        <a href="/pcplus/publisher/dashboard" class="text-2xl font-black tracking-wide text-orange-300">PC+ Publisher</a>

        <#-- link that returns to the publisher dashboard without saving the form -->
        <a href="/pcplus/publisher/dashboard" class="text-orange-100/80 hover:text-orange-300">Back to Dashboard</a>
    </div>
</nav>

<#-- content area that contains the publisher game form -->
<main class="mx-auto max-w-5xl px-6 py-10">

    <#-- glass panel that contains the game form -->
    <section class="pub-glass rounded-3xl p-8">

        <#-- small label that explains the form will save to the live Neon database -->
        <p class="text-sm font-bold uppercase tracking-[0.35em] text-orange-300">Live Neon Database Form</p>

        <#-- main heading that displays the page title passed into the page from the backend -->
        <h1 class="mt-3 text-4xl font-black">${pageTitle}</h1>

        <#-- form that submits the game information to the route passed into the page from the backend -->
        <form method="post" action="${formAction}" class="mt-8 grid gap-5 md:grid-cols-2">

            <#-- text field for entering the name of the game -->
            <div>
                <label>Title</label>
                <input class="pub-input mt-2 w-full rounded-2xl px-4 py-3" name="title" required value="${game.title!''}">
            </div>

            <#-- publisher name field that is displayed to customers in the game detail page -->
            <div>
                <label>Publisher Name</label>
                <input class="pub-input mt-2 w-full rounded-2xl px-4 py-3" name="publisher" value="${game.publisher!''}">
            </div>

            <#-- field for entering the regular price of the game -->
            <div>
                <label>Price</label>
                <input class="pub-input mt-2 w-full rounded-2xl px-4 py-3" name="price" type="number" step="0.01" min="0" value="${(game.price!0)?string('0.00')}">
            </div>

            <#-- optional field for entering the sale price of the game -->
            <div>
                <label>Sale Price</label>
                <input class="pub-input mt-2 w-full rounded-2xl px-4 py-3" name="salePrice" type="number" step="0.01" min="0" value="<#if game.salePrice??>${game.salePrice?string('0.00')}</#if>">
            </div>

            <#-- field for entering the genre of the game -->
            <div>
                <label>Genres</label>
                <input class="pub-input mt-2 w-full rounded-2xl px-4 py-3" name="genres" value="${game.genres!''}">
            </div>

            <#-- dropdown field that determines the status of the game as live, draft, or removed from the store -->
            <div>
                <label>Status</label>
                <select class="pub-input mt-2 w-full rounded-2xl px-4 py-3" name="status">
                    <#-- status is live which makes the game visible in the store -->
                    <option value="live" <#if (game.status!'live') == 'live'>selected</#if>>Live</option>

                    <#-- status is draft which indicates game is not yet live -->
                    <option value="draft" <#if (game.status!'') == 'draft'>selected</#if>>Draft</option>

                    <#-- status is removed which indicates game is no longer available in the store -->
                    <option value="removed" <#if (game.status!'') == 'removed'>selected</#if>>Removed</option>
                </select>
            </div>

            <#-- field for entering the url of the game cover image -->
            <div class="md:col-span-2">
                <label>Cover Image URL</label>
                <input class="pub-input mt-2 w-full rounded-2xl px-4 py-3" name="coverImage" value="${game.coverImage!''}">
            </div>

            <#-- field that contains a comma separated list of urls for the game screenshots -->
            <div class="md:col-span-2">
                <label>Screenshots URLs separated by commas</label>
                <textarea class="pub-input mt-2 h-24 w-full rounded-2xl px-4 py-3" name="screenshots">${game.screenshots!''}</textarea>
            </div>

            <#-- field for entering the details of the game -->
            <div class="md:col-span-2">
                <label>Description</label>
                <textarea class="pub-input mt-2 h-32 w-full rounded-2xl px-4 py-3" name="description">${game.description!''}</textarea>
            </div>

            <#-- field for entering the requirements of the operating system for running the game -->
            <div>
                <label>Required OS</label>
                <input class="pub-input mt-2 w-full rounded-2xl px-4 py-3" name="reqOs" value="${game.reqOs!''}">
            </div>

            <#-- field for entering the requirements for the cpu to play the game -->
            <div>
                <label>Required CPU</label>
                <input class="pub-input mt-2 w-full rounded-2xl px-4 py-3" name="reqCpu" value="${game.reqCpu!''}">
            </div>

            <#-- field for entering the amount of ram that is required to play the game -->
            <div>
                <label>Required RAM</label>
                <input class="pub-input mt-2 w-full rounded-2xl px-4 py-3" name="reqRam" value="${game.reqRam!''}">
            </div>

            <#-- field for entering the requirements for the gpu to play the game -->
            <div>
                <label>Required GPU</label>
                <input class="pub-input mt-2 w-full rounded-2xl px-4 py-3" name="reqGpu" value="${game.reqGpu!''}">
            </div>

            <#-- field for entering the amount of storage space that is required to play the game -->
            <div>
                <label>Required Storage</label>
                <input class="pub-input mt-2 w-full rounded-2xl px-4 py-3" name="reqStorage" value="${game.reqStorage!''}">
            </div>

            <#-- checkbox that indicates whether the game has a playable demo -->
            <div class="flex items-center gap-3 pt-8">
                <input type="checkbox" name="playable" value="true" <#if game.playable?? && game.playable>checked</#if>>
                <label>Playable demo available</label>
            </div>

            <#-- action row at the bottom of the form that contains the cancel and save links -->
            <div class="md:col-span-2 flex justify-end gap-3 pt-4">

                <#-- link that returns to the publisher dashboard without saving the game -->
                <a href="/pcplus/publisher/dashboard" class="rounded-full border border-orange-400/40 px-6 py-3 font-bold text-orange-100">Cancel</a>

                <#-- save button that saves the game information to the database through the backend -->
                <button class="rounded-full pub-btn px-7 py-3 font-black" type="submit">Save Game</button>
            </div>
        </form>
    </section>
</main>
</body>
</html>