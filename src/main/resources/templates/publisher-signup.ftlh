<!DOCTYPE html>

<#-- sets the language of the page to English -->
<html lang="en">

<head>
    <#-- sets the character encoding for the page -->
    <meta charset="UTF-8">

    <#-- helps the page display correctly on phones, tablets, and laptops -->
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <#-- title that is displayed in the browser tab -->
    <title>Publisher Signup - PC+</title>

    <#-- loads Tailwind CSS from the CDN -->
    <script src="https://cdn.tailwindcss.com"></script>

    <#-- loads the shared stylesheet for the PC Plus MVC pages -->
    <link rel="stylesheet" href="/css/pcplus-mvc.css">

    <style>
        /* applies the publisher color theme to the page */
        body { background: radial-gradient(circle at top left, #3b0b0b, #120913 45%, #08070c); color: #fff7ed; }

        /* glass style card used for the publisher signup panel */
        .pub-glass { background: rgba(28, 13, 16, 0.78); border: 1px solid rgba(248, 113, 22, 0.35); box-shadow: 0 24px 80px rgba(239, 68, 68, 0.16); }

        /* gradient button style for the publisher signup form */
        .pub-btn { background: linear-gradient(135deg, #f97316, #ef4444); color: white; }

        /* input field style for the publisher signup form */
        .pub-input { background: rgba(255,255,255,0.08); border: 1px solid rgba(251,146,60,0.35); color: white; }
    </style>
</head>

<#-- centers the signup form on the page -->
<body class="min-h-screen grid place-items-center px-6">

    <#-- main glass panel for the publisher signup form -->
    <main class="pub-glass w-full max-w-lg rounded-3xl p-8">

        <#-- small label indicating the purpose of this page -->
        <p class="text-sm font-bold uppercase tracking-[0.35em] text-orange-300">Provider Access</p>

        <#-- main heading for the publisher signup form -->
        <h1 class="mt-3 text-4xl font-black">Create Publisher Account</h1>

        <#-- displays an error message from the backend -->
        <#if errorMessage??>

            <#-- red alert box for publisher signup errors -->
            <div class="mt-5 rounded-2xl border border-red-400/40 bg-red-500/15 px-5 py-4 text-red-100">${errorMessage}</div>
        </#if>

        <#-- submits the signup information from the publisher to the backend -->
        <form method="post" action="/pcplus/publisher/signup" class="mt-7 grid gap-4">

            <#-- email field for the publisher login email -->
            <#-- required because the publisher will need to use this email to log in to the platform at a later time -->
            <input class="pub-input rounded-2xl px-4 py-3" type="email" name="email" placeholder="Publisher email" required>

            <#-- display name field for the publisher account -->
            <#-- this name will be used in the platform instead of the publisher’s email address -->
            <input class="pub-input rounded-2xl px-4 py-3" type="text" name="displayName" placeholder="Publisher display name">

            <#-- support contact email for the publisher -->
            <#-- allows the publisher to have a separate email to contact the company in the event that they experience any issues -->
            <input class="pub-input rounded-2xl px-4 py-3" type="email" name="supportContact" placeholder="Support contact email">

            <#-- password field for the publisher account -->
            <#-- required to help protect the publisher’s account -->
            <input class="pub-input rounded-2xl px-4 py-3" type="password" name="password" placeholder="Password" required>

            <#-- four digit pin to reset the publisher’s password -->
            <#-- limits the pin to exactly four digits -->
            <input class="pub-input rounded-2xl px-4 py-3" type="text" name="pin" maxlength="4" pattern="\d{4}" placeholder="4 digit reset PIN" required>

            <#-- submit button to create the publisher’s account -->
            <#-- creates the publisher’s account on the platform -->
            <button class="rounded-full pub-btn px-7 py-3 font-black" type="submit">Create Publisher</button>
        </form>

        <#-- link to direct existing users to the login page -->
        <p class="mt-5 text-sm text-orange-100/70">
            Already have an account?
            <a href="/pcplus/login" class="font-bold text-orange-300">Use the main login page</a>.
        </p>
    </main>
</body>
</html>