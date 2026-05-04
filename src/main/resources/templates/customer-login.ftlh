<#-- login page with shared top nav -->

<!doctype html>

<#-- sets the language of the page to English -->
<html lang="en">

<head>
    <#-- sets the character encoding for the page -->
    <meta charset="UTF-8" />

    <#-- helps the page display correctly on phones, tablets, and laptops -->
    <meta name="viewport" content="width=device-width,initial-scale=1" />

    <#-- title shown in the browser tab -->
    <title>PC+ | Login</title>

    <#-- loads Tailwind CSS from the CDN so the page can use utility classes -->
    <script src="https://cdn.tailwindcss.com"></script>

    <script>
        // configures Tailwind with the PC Plus accent color
        tailwind.config = {
            theme: {
                extend: {
                    // colors defined and used on this page
                    colors: {
                        // orange accent color used on page for buttons and highlights
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

    <#-- loads the Yuji Mai font for use on the page -->
    <link href="https://fonts.googleapis.com/css2?family=Yuji+Mai&display=swap" rel="stylesheet" />

    <#-- loads the shared stylesheet for the site’s pages -->
    <link rel="stylesheet" href="/css/pcplus-mvc.css" />
</head>

<#-- applies the shared body and text color styling to the login page -->
<body class="pcplus-body min-h-screen text-white">

<#-- an element to glow when the cursor passes over it, defined in the CSS -->
<div id="cursorGlow" class="pcplus-cursor-glow"></div>

<#-- navigation bar element that remains visible on the screen -->
<header class="sticky top-0 z-50 border-b border-white/10 bg-black/20 backdrop-blur-xl">

    <#-- navigation bar content that is centered within the bar -->
    <div class="mx-auto flex max-w-7xl items-center gap-4 px-5 py-4">

        <#-- link to the PC Plus logo and the catalog -->
        <a class="font-semibold tracking-tight hover:text-white/90 transition" href="/pcplus/catalog">

            <#-- plus sign element that uses the orange accent color for visual interest -->
            PC<span class="text-accent">+</span>
        </a>

        <#-- links to the main pages of the login page -->
        <nav class="ml-2 flex flex-1 items-center gap-1 text-sm text-white/80">

            <#-- link to the catalog -->
            <a class="rounded-lg px-3 py-2 hover:bg-white/5 hover:text-white transition" href="/pcplus/catalog">Home</a>

            <#-- link to the login page -->
            <a class="rounded-lg px-3 py-2 bg-white/10 text-white transition" href="/pcplus/login">Login</a>

            <#-- link to the signup page -->
            <a class="rounded-lg px-3 py-2 hover:bg-white/5 hover:text-white transition" href="/pcplus/signup">Sign Up</a>
        </nav>
    </div>
</header>

<#-- main content area for the login page that vertically centers the page elements -->
<main class="mx-auto flex min-h-[calc(100vh-76px)] max-w-6xl items-center px-5 py-12">

    <#-- sets up a two column layout within the login page -->
    <div class="grid w-full gap-5 lg:grid-cols-2">

        <#-- panel containing text that welcomes the user to PC Plus -->
        <section class="pcplus-reveal pcplus-glass rounded-3xl p-8">

            <#-- small label that identifies the text as welcoming the user to PC Plus -->
            <p class="text-xs tracking-[0.35em] text-white/60">PC+</p>

            <#-- the main heading within the welcome panel -->
            <h1 class="mt-3 text-4xl font-semibold tracking-tight">Welcome back</h1>

            <#-- text that explains to the user what the login page will allow them to do -->
            <p class="mt-3 max-w-xl text-sm leading-relaxed text-white/70">
                This login page now uses the same pc plus look while the form submits to the real Spring Boot backend.
            </p>

            <#-- information cards that describe each of the features of the website -->
            <div class="mt-8 grid gap-4 sm:grid-cols-2">

                <#-- information card describing the catalog feature -->
                <div class="rounded-2xl border border-white/10 bg-white/5 p-5">

                    <#-- label for the catalog information card -->
                    <p class="text-xs tracking-[0.35em] text-white/60">CATALOG</p>

                    <#-- states that the catalog feature will use data from the database -->
                    <p class="mt-2 text-sm text-white/80">Browse live game records from the database.</p>
                </div>

                <#-- information card describing the reviews feature -->
                <div class="rounded-2xl border border-white/10 bg-white/5 p-5">

                    <#-- label for the reviews information card -->
                    <p class="text-xs tracking-[0.35em] text-white/60">REVIEWS</p>

                    <#-- states that the reviews will be stored and displayed through the website backend -->
                    <p class="mt-2 text-sm text-white/80">Submit and display persistent reviews.</p>
                </div>
            </div>

            <#-- link to the guest access link -->
            <div class="mt-8">

                <#-- allows browsing of the catalog without logging in -->
                <a href="/pcplus/catalog" class="rounded-2xl border border-white/10 bg-white/5 px-4 py-3 text-sm text-white/80 hover:bg-white/10 hover:text-white transition">
                    Continue as guest
                </a>
            </div>
        </section>

        <#-- right panel containing the login form -->
        <section class="pcplus-reveal pcplus-shine pcplus-glass rounded-3xl p-8">

            <#-- small label for the login form -->
            <p class="text-xs tracking-[0.35em] text-white/60">ACCOUNT LOGIN</p>

            <#-- heading for the login form -->
            <h2 class="mt-2 text-2xl font-semibold tracking-tight">Sign in</h2>

            <#-- display message from backend if successful logging in -->
            <#if successMessage??>

                <#-- green message box that displays success messages from the backend -->
                <div class="mt-5 rounded-2xl border border-emerald-400/20 bg-emerald-500/15 px-4 py-3 text-sm text-emerald-100">
                    ${successMessage}
                </div>
            </#if>

            <#-- display message from backend if login failed -->
            <#if errorMessage??>

                <#-- red message box that displays error messages from the backend -->
                <div class="mt-5 rounded-2xl border border-red-400/20 bg-red-500/15 px-4 py-3 text-sm text-red-100">
                    ${errorMessage}
                </div>
            </#if>

            <#-- form will submit to backend with login information -->
            <form class="mt-6 space-y-4" action="/pcplus/login" method="post">

                <#-- field that allows the user to select if they are logging in as a customer or a publisher -->
                <div>
                    <label class="text-sm text-white/70" for="accountType">Login as</label>

                    <#-- dropdown menu for the account type -->
                    <select
                            id="accountType"
                            name="accountType"
                            class="mt-2 w-full rounded-2xl border border-white/10 bg-white/5 px-4 py-3 text-sm text-white outline-none"
                            required
                    >
                        <#-- customer login -->
                        <option class="bg-[#102c27] text-white" value="customer">Customer</option>

                        <#-- publisher login -->
                        <option class="bg-[#3b0b0b] text-white" value="publisher">Publisher</option>
                    </select>
                </div>

                <#-- field for the user to enter their email address -->
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

                <#-- field for the user to enter their password -->
                <div>
                    <label class="text-sm text-white/70" for="password">Password</label>

                    <input
                            id="password"
                            name="password"
                            type="password"
                            class="mt-2 w-full rounded-2xl border border-white/10 bg-white/5 px-4 py-3 text-sm text-white outline-none placeholder:text-white/40"
                            placeholder="Enter your password"
                            required
                    />
                </div>

                <#-- button that submits the login form -->
                <button class="w-full rounded-2xl bg-accent px-4 py-3 text-sm font-semibold text-white hover:brightness-110 transition" type="submit">
                    Sign In
                </button>
            </form>

            <#-- links to the signup and password reset pages for those interested in creating an account -->
            <div class="mt-5 flex flex-col gap-2 text-sm text-white/70 sm:flex-row sm:items-center sm:justify-between">
                <p>
                    No account yet?

                    <#-- link to create a new account -->
                    <a href="/pcplus/signup" class="text-accent hover:brightness-110">Create one here</a>
                </p>

                <#-- link to the password reset page -->
                <a href="/pcplus/forgot-password" class="text-accent hover:brightness-110">Forgot password?</a>
            </div>
        </section>
    </div>
</main>

<script>
    // gets access to the cursor glow element from the page
    const glow = document.getElementById("cursorGlow");

    // listens for movement of the mouse over the page
    document.addEventListener("mousemove", function (event) {

        // stores the x position of the mouse into a css variable
        document.documentElement.style.setProperty("--mx", event.clientX + "px");

        // stores the y position of the mouse into a css variable
        document.documentElement.style.setProperty("--my", event.clientY + "px");

        // only moves the cursor glow if the glow element exists in the page
        if (glow) {

            // moves the glow element to match the x position of the cursor
            glow.style.left = event.clientX + "px";

            // moves the glow element to match the y position of the cursor
            glow.style.top = event.clientY + "px";
        }
    });

    // selects every element that should be revealed when it enters into the screen
    const revealItems = document.querySelectorAll(".pcplus-reveal");

    // creates an observer for elements that should be revealed when they enter into the screen
    const revealObserver = new IntersectionObserver(function (entries) {

        // loops through every element in the page that should be revealed when it enters into the screen
        entries.forEach(function (entry) {

            // checks if each element is in view on the screen when scrolling
            if (entry.isIntersecting) {

                // adds a class to the elements so that it will reveal upon being in view
                entry.target.classList.add("in");
            }
        });
    }, { threshold: 0.08 });

    // begins observing every element that should be revealed when it enters into the screen
    revealItems.forEach(function (item) {

        // tells the observer to begin to watch for changes in the element
        revealObserver.observe(item);
    });
</script>

</body>
</html>