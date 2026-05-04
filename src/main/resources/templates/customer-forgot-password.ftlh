<#-- forgot password page for customer and publisher accounts -->

<!doctype html>

<#-- sets the language of the webpage to English -->
<html lang="en">

<head>
    <#-- specifies the character encoding for the page -->
    <meta charset="UTF-8" />

    <#-- ensures the page displays correctly on all device types -->
    <meta name="viewport" content="width=device-width,initial-scale=1" />

    <#-- the title to be displayed within the browser tab -->
    <title>PC+ | Reset Password</title>

    <#-- loads Tailwind CSS files from a CDN -->
    <script src="https://cdn.tailwindcss.com"></script>

    <script>
        // configures Tailwind to use the accent color of the PC Plus application
        tailwind.config = {
            theme: {
                extend: {
                    // colors specifically used on this page
                    colors: {
                        // orange color used for buttons and highlights on the page
                        accent: "#ff7a18"
                    }
                }
            }
        };
    </script>

    <#-- connects to Google Fonts to load fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com" />

    <#-- connects to the Google font file server -->
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />

    <#-- loads the Yuji Mai font used on the PC Plus application pages -->
    <link href="https://fonts.googleapis.com/css2?family=Yuji+Mai&display=swap" rel="stylesheet" />

    <#-- loads the shared stylesheet of the PC Plus MVC application -->
    <link rel="stylesheet" href="/css/pcplus-mvc.css" />
</head>

<#-- applies the shared styling to the body of the page -->
<body class="pcplus-body min-h-screen text-white">

<#-- element to make the cursor glow that is controlled by JavaScript -->
<div id="cursorGlow" class="pcplus-cursor-glow"></div>

<#-- navigation bar that is sticky to the top of the viewport -->
<header class="sticky top-0 z-50 border-b border-white/10 bg-black/20 backdrop-blur-xl">

    <#-- navigation content that is centered on the page -->
    <div class="mx-auto flex max-w-7xl items-center gap-4 px-5 py-4">

        <#-- logo for the PC Plus application -->
        <a class="font-semibold tracking-tight hover:text-white/90 transition" href="/pcplus/catalog">
            PC<span class="text-accent">+</span>
        </a>

        <#-- navigation links for users that are not logged in -->
        <nav class="ml-2 flex flex-1 items-center gap-1 text-sm text-white/80">

            <#-- link to the catalog for the application -->
            <a class="rounded-lg px-3 py-2 hover:bg-white/5 hover:text-white transition" href="/pcplus/catalog">Home</a>

            <#-- link to the login page -->
            <a class="rounded-lg px-3 py-2 hover:bg-white/5 hover:text-white transition" href="/pcplus/login">Login</a>

            <#-- link to the signup page -->
            <a class="rounded-lg px-3 py-2 hover:bg-white/5 hover:text-white transition" href="/pcplus/signup">Sign Up</a>
        </nav>
    </div>
</header>

<#-- content area for the reset password page that is centered on the page -->
<main class="mx-auto flex min-h-[calc(100vh-76px)] max-w-6xl items-center px-5 py-12">

    <#-- main layout for the recovery information and form panels -->
    <div class="grid w-full gap-5 lg:grid-cols-2">

        <#-- information panel explaining the password recovery process -->
        <section class="pcplus-reveal pcplus-glass rounded-3xl p-8">

            <#-- label for the account recovery section -->
            <p class="text-xs tracking-[0.35em] text-white/60">ACCOUNT RECOVERY</p>

            <#-- main heading for the recovery page -->
            <h1 class="mt-3 text-4xl font-semibold tracking-tight">Reset your password</h1>

            <#-- short text that explains what information is required for the form -->
            <p class="mt-3 max-w-xl text-sm leading-relaxed text-white/70">
                Enter the email for your account, your four digit account pin, and the new password you want to use.
            </p>

            <#-- grid that displays information regarding the ability for both customer and publisher accounts to recover their passwords -->
            <div class="mt-8 grid gap-4 sm:grid-cols-2">

                <#-- information regarding recovery for customer accounts -->
                <div class="rounded-2xl border border-white/10 bg-white/5 p-5">
                    <p class="text-xs tracking-[0.35em] text-white/60">CUSTOMERS</p>
                    <p class="mt-2 text-sm text-white/80">Reset access to your catalog, cart, reviews, and library.</p>
                </div>

                <#-- information regarding recovery for publisher accounts -->
                <div class="rounded-2xl border border-white/10 bg-white/5 p-5">
                    <p class="text-xs tracking-[0.35em] text-white/60">PUBLISHERS</p>
                    <p class="mt-2 text-sm text-white/80">Reset access to your publisher dashboard and game tools.</p>
                </div>
            </div>

            <div class="mt-8">

                <#-- link that takes the user back to the login page -->
                <a href="/pcplus/login" class="rounded-2xl border border-white/10 bg-white/5 px-4 py-3 text-sm text-white/80 hover:bg-white/10 hover:text-white transition">
                    Back to login
                </a>
            </div>
        </section>

        <#-- form panel that contains the form for the user to enter their information for password recovery -->
        <section class="pcplus-reveal pcplus-shine pcplus-glass rounded-3xl p-8">

            <#-- label for the form section -->
            <p class="text-xs tracking-[0.35em] text-white/60">PASSWORD RESET</p>

            <#-- heading for the password recovery form -->
            <h2 class="mt-2 text-2xl font-semibold tracking-tight">Create a new password</h2>

            <#-- text that displays a message from the backend if the password was reset successfully -->
            <#if successMessage??>
                <div class="mt-5 rounded-2xl border border-emerald-400/20 bg-emerald-500/15 px-4 py-3 text-sm text-emerald-100">
                    ${successMessage}
                </div>
            </#if>

            <#-- text that displays an error message from the backend if the password could not be reset -->
            <#if errorMessage??>
                <div class="mt-5 rounded-2xl border border-red-400/20 bg-red-500/15 px-4 py-3 text-sm text-red-100">
                    ${errorMessage}
                </div>
            </#if>

            <#-- form that sends the user’s information to the forgot password route -->
            <form class="mt-6 space-y-4" action="/pcplus/forgot-password" method="post">

                <#-- field that determines the type of account that is attempting to recover its password -->
                <div>
                    <label class="text-sm text-white/70" for="accountType">Reset account type</label>

                    <#-- dropdown field for the account type -->
                    <select
                            id="accountType"
                            name="accountType"
                            class="mt-2 w-full rounded-2xl border border-white/10 bg-white/5 px-4 py-3 text-sm text-white outline-none"
                            required
                    >
                        <#-- customer account type selected by default if the backend indicates that the account type is a customer -->
                        <option class="bg-[#102c27] text-white" value="customer" <#if accountType?? && accountType == "customer">selected</#if>>Customer</option>

                        <#-- publisher account type selected by default if the backend indicates that the account type is a publisher -->
                        <option class="bg-[#3b0b0b] text-white" value="publisher" <#if accountType?? && accountType == "publisher">selected</#if>>Publisher</option>
                    </select>
                </div>

                <#-- field for the user to enter their email address -->
                <div>
                    <label class="text-sm text-white/70" for="email">Email</label>

                    <#-- email field retains the entered text if the page is loaded with an error message -->
                    <input
                            id="email"
                            name="email"
                            type="email"
                            value="${email!''}"
                            class="mt-2 w-full rounded-2xl border border-white/10 bg-white/5 px-4 py-3 text-sm text-white outline-none placeholder:text-white/40"
                            placeholder="you@example.com"
                            required
                    />
                </div>

                <#-- field for the user to enter their pin number to verify that they own the account -->
                <div>
                    <label class="text-sm text-white/70" for="pin">Four digit pin</label>

                    <#-- pin field accepts only four digits -->
                    <input
                            id="pin"
                            name="pin"
                            type="password"
                            inputmode="numeric"
                            pattern="\d{4}"
                            maxlength="4"
                            class="mt-2 w-full rounded-2xl border border-white/10 bg-white/5 px-4 py-3 text-sm text-white outline-none placeholder:text-white/40"
                            placeholder="Enter your 4 digit pin"
                            required
                    />
                </div>

                <#-- field for the user to enter their new password -->
                <div>
                    <label class="text-sm text-white/70" for="newPassword">New password</label>

                    <#-- field for the user to type their new password -->
                    <input
                            id="newPassword"
                            name="newPassword"
                            type="password"
                            class="mt-2 w-full rounded-2xl border border-white/10 bg-white/5 px-4 py-3 text-sm text-white outline-none placeholder:text-white/40"
                            placeholder="Enter a new password"
                            required
                    />
                </div>

                <#-- field for the user to confirm their new password -->
                <div>
                    <label class="text-sm text-white/70" for="confirmPassword">Confirm new password</label>

                    <input
                            id="confirmPassword"
                            name="confirmPassword"
                            type="password"
                            class="mt-2 w-full rounded-2xl border border-white/10 bg-white/5 px-4 py-3 text-sm text-white outline-none placeholder:text-white/40"
                            placeholder="Re-enter the new password"
                            required
                    />
                </div>

                <#-- button that submits the password recovery form -->
                <button class="w-full rounded-2xl bg-accent px-4 py-3 text-sm font-semibold text-white hover:brightness-110 transition" type="submit">
                    Reset Password
                </button>
            </form>

            <#-- alternative link to the sign in page for users that remember their password -->
            <p class="mt-5 text-sm text-white/70">
                Remembered your password?
                <a href="/pcplus/login" class="text-accent hover:brightness-110">Sign in here</a>
            </p>
        </section>
    </div>
</main>

<script>
    // gets the glow element from the page
    const glow = document.getElementById("cursorGlow");

    // listens for the user’s mouse movement within the page
    document.addEventListener("mousemove", function (event) {

        // stores the x position of the user’s mouse cursor in a css variable
        document.documentElement.style.setProperty("--mx", event.clientX + "px");

        // stores the y position of the user’s mouse cursor in a css variable
        document.documentElement.style.setProperty("--my", event.clientY + "px");

        // only applies movement to the glow element if it exists in the page
        if (glow) {

            // moves the glow element horizontally with the user’s cursor movement
            glow.style.left = event.clientX + "px";

            // moves the glow element vertically with the user’s cursor movement
            glow.style.top = event.clientY + "px";
        }
    });

    // selects elements that should be revealed when they enter the screen
    const revealItems = document.querySelectorAll(".pcplus-reveal");

    // creates an observer to determine when reveal elements enter the viewport
    const revealObserver = new IntersectionObserver(function (entries) {

        // loops through each reveal element
        entries.forEach(function (entry) {

            // checks if the element is currently in the viewport
            if (entry.isIntersecting) {

                // adds the in class to the element so that its styling will activate
                entry.target.classList.add("in");
            }
        });
    }, { threshold: 0.08 });

    // begins observing each reveal element
    revealItems.forEach(function (item) {

        // tells the observer to begin watching the item specified
        revealObserver.observe(item);
    });
</script>

</body>
</html>