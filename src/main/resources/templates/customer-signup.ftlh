<#-- signup page with shared top nav -->

<!doctype html>

<#-- sets the language of the page to English -->
<html lang="en">

<head>
    <#-- sets the character encoding for the page -->
    <meta charset="UTF-8" />

    <#-- helps the page display correctly on phones, tablets, and laptops -->
    <meta name="viewport" content="width=device-width,initial-scale=1" />

    <#-- title shown in the browser tab -->
    <title>PC+ | Sign Up</title>

    <#-- loads Tailwind CSS from the CDN so the page can use utility classes -->
    <script src="https://cdn.tailwindcss.com"></script>

    <script>
        // configures Tailwind with the PC Plus accent color
        tailwind.config = {
            theme: {
                extend: {
                    // custom colors used by this page
                    colors: {
                        // orange accent color used for buttons and highlights
                        accent: "#ff7a18"
                    }
                }
            }
        };
    </script>

    <#-- connects to the Google Fonts server -->
    <link rel="preconnect" href="https://fonts.googleapis.com" />

    <#-- connects to the Google font file server -->
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />

    <#-- loads the Yuji Mai font that is used in the PC Plus styling -->
    <link href="https://fonts.googleapis.com/css2?family=Yuji+Mai&display=swap" rel="stylesheet" />

    <#-- loads the stylesheet that is shared between all the pages of the MVC application -->
    <link rel="stylesheet" href="/css/pcplus-mvc.css" />
</head>

<#-- applies the body styling and white text color that is shared between all the pages of the MVC application -->
<body class="pcplus-body min-h-screen text-white">

<#-- creates the element that will glow when the cursor is placed somewhere on the page -->
<div id="cursorGlow" class="pcplus-cursor-glow"></div>

<#-- creates the sticky navigation bar that is shown at the top of the signup page -->
<header class="sticky top-0 z-50 border-b border-white/10 bg-black/20 backdrop-blur-xl">

    <#-- applies the centering and spacing styles to the navigation bar -->
    <div class="mx-auto flex max-w-7xl items-center gap-4 px-5 py-4">

        <#-- adds the logo and links to the catalog to the navigation bar -->
        <a class="font-semibold tracking-tight hover:text-white/90 transition" href="/pcplus/catalog">

            <#-- ensures that the plus sign uses the orange accent color -->
            PC<span class="text-accent">+</span>
        </a>

        <#-- adds the navigation links to the navigation bar -->
        <nav class="ml-2 flex flex-1 items-center gap-1 text-sm text-white/80">

            <#-- creates the links to return to the catalog and login page -->
            <a class="rounded-lg px-3 py-2 hover:bg-white/5 hover:text-white transition" href="/pcplus/catalog">Home</a>

            <a class="rounded-lg px-3 py-2 hover:bg-white/5 hover:text-white transition" href="/pcplus/login">Login</a>

            <#-- creates the link to the current page -->
            <a class="rounded-lg px-3 py-2 bg-white/10 text-white transition" href="/pcplus/signup">Sign Up</a>
        </nav>
    </div>
</header>

<#-- creates the content that will be displayed in the main part of the page -->
<main class="mx-auto flex min-h-[calc(100vh-76px)] max-w-6xl items-center px-5 py-12">

    <#-- creates the two column layout for the page -->
    <div class="grid w-full gap-5 lg:grid-cols-2">

        <#-- creates the left panel that will contain information about the signup process -->
        <section class="pcplus-reveal pcplus-glass rounded-3xl p-8">

            <#-- adds the label to the left panel for identifying the section -->
            <p class="text-xs tracking-[0.35em] text-white/60">CREATE ACCOUNT</p>

            <#-- adds the heading and information to the left panel describing the signup process -->
            <h1 class="mt-3 text-4xl font-semibold tracking-tight">Join PC+</h1>

            <p class="mt-3 max-w-xl text-sm leading-relaxed text-white/70">
                This version keeps the original frontend feel while the form now creates a real customer record in the database.
            </p>

            <#-- creates the information cards that will explain the signup process -->
            <div class="mt-8 rounded-2xl border border-white/10 bg-white/5 p-5">

                <#-- adds the label to the information card that describes the information that follows -->
                <p class="text-xs tracking-[0.35em] text-white/60">WHAT HAPPENS NEXT</p>

                <#-- explains that the backend will store the account and sign the user in -->
                <p class="mt-2 text-sm text-white/80">
                    After signup the backend stores your account, signs you in, and redirects you to the live catalog.
                </p>
            </div>

            <#-- creates the information card that describes the available account types -->
            <div class="mt-5 rounded-2xl border border-accent/30 bg-accent/10 p-5">

                <#-- adds the label to the information card that describes the information that follows -->
                <p class="text-xs tracking-[0.35em] text-white/60">CUSTOMER OR PUBLISHER</p>

                <#-- explains the effect that the account type will have on the new account -->
                <p class="mt-2 text-sm text-white/80">
                    Use the account type dropdown on the signup form to choose whether this account buys games or publishes games.
                </p>
            </div>
        </section>

        <#-- creates the right panel that will contain the signup form -->
        <section class="pcplus-reveal pcplus-shine pcplus-glass rounded-3xl p-8">

            <#-- adds the label to the right panel for identifying the section -->
            <p class="text-xs tracking-[0.35em] text-white/60">ACCOUNT SIGN UP</p>

            <#-- adds the heading and success and error message sections to the right panel -->
            <h2 class="mt-2 text-2xl font-semibold tracking-tight">Create your account</h2>

            <#-- adds a success message to the signup form to display a message from the backend -->
            <#if successMessage??>
                <div class="mt-5 rounded-2xl border border-emerald-400/20 bg-emerald-500/15 px-4 py-3 text-sm text-emerald-100">
                    ${successMessage}
                </div>
            </#if>

            <#-- adds an error message to the signup form to display an error message from the backend -->
            <#if errorMessage??>
                <div class="mt-5 rounded-2xl border border-red-400/20 bg-red-500/15 px-4 py-3 text-sm text-red-100">
                    ${errorMessage}
                </div>
            </#if>

            <#-- adds the signup form to the right panel -->
            <#-- creates the form with fields to enter the signup information -->
            <form class="mt-6 space-y-4" action="/pcplus/signup" method="post">

                <#-- creates the account type drop down menu to select the type of account to create -->
                <div>
                    <label class="text-sm text-white/70" for="accountType">Account type</label>

                    <select
                            id="accountType"
                            name="accountType"
                            class="mt-2 w-full rounded-2xl border border-white/10 bg-white/5 px-4 py-3 text-sm text-white outline-none"
                            required
                    >
                        <#-- adds the customer account option for users who want to buy games -->
                        <option class="bg-[#102c27] text-white" value="customer">Customer, buy games</option>

                        <#-- adds the publisher account option for users who want to sell games -->
                        <option class="bg-[#3b0b0b] text-white" value="publisher">Publisher, sell games</option>
                    </select>

                    <#-- adds helper text to explain the publisher account option -->
                    <p class="mt-2 text-xs text-white/50">Choose Publisher here to create a provider account and go straight to the publisher dashboard.</p>
                </div>

                <#-- creates the field for the user to enter their display name for use in the interface -->
                <div>
                    <label class="text-sm text-white/70" for="displayName">Display name</label>

                    <#-- makes the display name field optional -->
                    <input
                            id="displayName"
                            name="displayName"
                            type="text"
                            class="mt-2 w-full rounded-2xl border border-white/10 bg-white/5 px-4 py-3 text-sm text-white outline-none placeholder:text-white/40"
                            placeholder="Choose a display name"
                    />
                </div>

                <#-- creates the field for the user to enter their email for login to the system -->
                <div>
                    <label class="text-sm text-white/70" for="email">Email</label>

                    <input
                            id="email"
                            name="email"
                            type="email"
                            class="mt-2 w-full rounded-2xl border border-white/10 bg-white/5 px-4 py-3 text-sm text-white outline-none placeholder:text-white/40"
                            placeholder="you@example.com"
                            required
                    />
                </div>

                <#-- creates the field for the user to enter their password for the account -->
                <div>
                    <label class="text-sm text-white/70" for="password">Password</label>

                    <input
                            id="password"
                            name="password"
                            type="password"
                            class="mt-2 w-full rounded-2xl border border-white/10 bg-white/5 px-4 py-3 text-sm text-white outline-none placeholder:text-white/40"
                            placeholder="Create a password"
                            required
                    />
                </div>

                <#-- creates the field for the user to enter their four digit numeric pin for account recovery -->
                <div>
                    <label class="text-sm text-white/70" for="pin">Pin</label>

                    <input
                            id="pin"
                            name="pin"
                            type="text"
                            maxlength="4" pattern="\d{4}"
                            class="mt-2 w-full rounded-2xl border border-white/10 bg-white/5 px-4 py-3 text-sm text-white outline-none placeholder:text-white/40"
                            placeholder="Create a 4 digit pin"
                            required
                    />
                </div>

                <#-- creates the submit button for the form -->
                <button class="w-full rounded-2xl bg-accent px-4 py-3 text-sm font-semibold text-white hover:brightness-110 transition" type="submit">
                    Create Account
                </button>
            </form>

            <#-- adds a link area to the page for users that already have an account -->
            <p class="mt-5 text-sm text-white/70">
                Already have an account?

                <#-- creates a link to the login page -->
                <a href="/pcplus/login" class="text-accent hover:brightness-110">Sign in</a>

            </p>
        </section>
    </div>
</main>

<script>
    // accesses the glow element that was created for the page
    const glow = document.getElementById("cursorGlow");

    // adds an event listener to the glow element to listen for movement of the mouse
    document.addEventListener("mousemove", function (event) {

        // stores the x position of the mouse in a css variable
        document.documentElement.style.setProperty("--mx", event.clientX + "px");

        // stores the y position of the mouse in a css variable
        document.documentElement.style.setProperty("--my", event.clientY + "px");

        // moves the glow element to the same x and y position as the cursor if the glow element was created for this page
        if (glow) {
            glow.style.left = event.clientX + "px";
            glow.style.top = event.clientY + "px";
        }
    });

    // adds a class to the elements to make them reveal when they enter into the screen
    const revealItems = document.querySelectorAll(".pcplus-reveal");

    // creates an observer for elements that should be revealed when they enter into the screen
    const revealObserver = new IntersectionObserver(function (entries) {

        // loops through each element that should be revealed when they enter into the screen
        entries.forEach(function (entry) {

            // checks if the element is in view on the screen
            if (entry.isIntersecting) {

                // adds the class to the element to reveal the element when it is in view
                entry.target.classList.add("in");
            }
        });
    }, { threshold: 0.08 });

    // begins observing each element that should be revealed when they enter into the screen
    revealItems.forEach(function (item) {

        // tells the observer to begin to watch for changes in the element
        revealObserver.observe(item);
    });
</script>

</body>
</html>