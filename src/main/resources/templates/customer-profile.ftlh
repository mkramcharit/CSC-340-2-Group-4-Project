<#-- this page restyles the mvc profile page to match the original pc plus profile page layout, but uses real data from the backend -->
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8" />

    <#-- ensures that the page will render correctly on all device sizes -->
    <meta name="viewport" content="width=device-width,initial-scale=1" />

    <title>PC+ | Profile</title>

    <#-- loads Tailwind CSS from the CDN to enable the utility classes -->
    <script src="https://cdn.tailwindcss.com"></script>
    <script>
        // this Tailwind CSS config file defines the shared customer theme for the site
        // only the accent color is defined in this file; all other themes are defined in the stylesheet for this page
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

    <#-- loads the font that is used in the original pc plus design -->
    <link rel="preconnect" href="https://fonts.googleapis.com" />
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
    <link href="https://fonts.googleapis.com/css2?family=Yuji+Mai&display=swap" rel="stylesheet" />

    <#-- shared stylesheet that defines glow and glass effects and page animations -->
    <link rel="stylesheet" href="/css/pcplus-mvc.css" />
</head>

<#-- applies shared styling to page elements including background and text color -->
<body class="pcplus-body min-h-screen text-white">

<#-- element used to display glow effect that follows the cursor -->
<div id="cursorGlow" class="pcplus-cursor-glow"></div>

<#-- navigation bar that remains visible at the top of the screen -->
<header class="sticky top-0 z-50 border-b border-white/10 bg-black/20 backdrop-blur-xl">
    <div class="mx-auto flex max-w-7xl items-center gap-4 px-5 py-4">

        <#-- link to logo that takes the user back to the main catalog -->
        <a class="font-semibold tracking-tight hover:text-white/90 transition" href="/pcplus/catalog">
            PC<span class="text-accent">+</span>
        </a>

        <#-- navigation links to the user's web pages -->
        <nav class="ml-2 flex flex-1 items-center gap-1 text-sm text-white/80">

            <#-- link to the home catalog -->
            <a class="rounded-lg px-3 py-2 hover:bg-white/5 hover:text-white transition" href="/pcplus/catalog">Home</a>

            <#-- customer only links -->
            <#if isCustomerLoggedIn>
                <a class="rounded-lg px-3 py-2 hover:bg-white/5 hover:text-white transition" href="/pcplus/library">Library</a>
                <a class="rounded-lg px-3 py-2 hover:bg-white/5 hover:text-white transition" href="/pcplus/cart">Cart <#if currentCartCount gt 0>(${currentCartCount})</#if></a>
            </#if>

            <#-- link to the profile page to highlight it -->
            <a class="rounded-lg px-3 py-2 bg-white/10 text-white transition" href="/pcplus/profile">Profile</a>

            <#-- publisher only link back to the publisher dashboard -->
            <#if isPublisherLoggedIn>
                <a class="rounded-lg px-3 py-2 bg-orange-500/20 text-orange-300 hover:bg-orange-500/30 transition" href="/pcplus/publisher/dashboard">Publisher Portal</a>
            </#if>

            <#-- logout link that returns the user to the login page -->
            <a class="rounded-lg px-3 py-2 hover:bg-white/5 hover:text-white transition" href="/pcplus/logout">Logout</a>

            <#-- element that displays the user's avatar and name -->
            <a href="/pcplus/profile" class="pcplus-nav-user ml-auto hidden sm:inline-flex">
                <img src="${currentAvatarUrl}" alt="Current avatar" class="pcplus-nav-avatar" />
                <span class="max-w-[140px] truncate text-sm text-white/85">
                    ${currentUser.displayName!currentUser.email}
                </span>
            </a>
        </nav>
    </div>
</header>

<#-- wrapper element used to center the content of the page -->
<main class="relative z-10 mx-auto max-w-5xl px-5 pb-16 pt-10">

    <#-- display success message from the backend after a profile change -->
    <#if successMessage??>
        <div class="mb-5 rounded-2xl border border-emerald-400/20 bg-emerald-500/15 px-4 py-3 text-sm text-emerald-100">
            ${successMessage}
        </div>
    </#if>

    <#-- display error message from the backend -->
    <#if errorMessage??>
        <div class="mb-5 rounded-2xl border border-red-400/20 bg-red-500/15 px-4 py-3 text-sm text-red-100">
            ${errorMessage}
        </div>
    </#if>

    <#-- layout of the page splits into profile and edit sections -->
    <div class="grid gap-5 lg:grid-cols-[.9fr_1.1fr]">

        <#-- left side of the page displays the user's profile -->
        <section class="pcplus-reveal pcplus-glass rounded-3xl p-8">

            <#-- section label -->
            <p class="text-xs tracking-[0.35em] text-white/60">ACCOUNT</p>

            <#-- avatar and information about the user -->
            <div class="mt-4 flex items-center gap-4">
                <img src="${profileAvatarUrl}" alt="Profile avatar"
                     class="h-20 w-20 rounded-3xl border border-white/10 bg-white/5 object-cover" />

                <div>
                    <h1 class="text-3xl font-semibold tracking-tight">
                        ${profile.displayName!profile.email}
                    </h1>

                    <#-- email address of the user -->
                    <p class="mt-2 text-sm text-white/70">${profile.email}</p>
                </div>
            </div>

            <#-- additional account information -->
            <div class="mt-6 space-y-3">

                <#-- role of the user within the site -->
                <div class="rounded-2xl border border-white/10 bg-white/5 p-4">
                    <p class="text-xs tracking-[0.35em] text-white/60">ROLE</p>
                    <p class="mt-1 text-sm text-white/80">${profile.role}</p>
                </div>

                <#-- currently selected avatar for the user -->
                <div class="rounded-2xl border border-white/10 bg-white/5 p-4">
                    <p class="text-xs tracking-[0.35em] text-white/60">CURRENT AVATAR</p>
                    <p class="mt-1 text-sm text-white/80">${profile.avatarId!"av1"}</p>
                </div>

                <#if isPublisherLoggedIn>
                    <div class="rounded-2xl border border-orange-400/20 bg-orange-500/10 p-4">
                        <p class="text-xs tracking-[0.35em] text-orange-200/80">PUBLISHER ACCESS</p>
                        <a href="/pcplus/publisher/dashboard" class="mt-3 inline-flex rounded-2xl bg-accent px-4 py-2 text-sm font-semibold text-white hover:brightness-110 transition">
                            Return to Publisher Portal
                        </a>
                    </div>
                </#if>
            </div>
        </section>

        <#-- right side of the page allows the user to edit their profile -->
        <section class="pcplus-reveal pcplus-shine pcplus-glass rounded-3xl p-8">

            <#-- section label -->
            <p class="text-xs tracking-[0.35em] text-white/60">EDIT PROFILE</p>

            <#-- heading for the edit form -->
            <h2 class="mt-2 text-2xl font-semibold tracking-tight">Update your profile</h2>

            <#-- form that allows the user to change their display name and avatar -->
            <form class="mt-6 space-y-5" action="/pcplus/profile" method="post">

                <#-- field to enter a new display name for the user -->
                <div>
                    <label class="text-sm text-white/70" for="displayName">Display name</label>
                    <input
                            id="displayName"
                            name="displayName"
                            type="text"
                            value="${profile.displayName!''}"
                            class="mt-2 w-full rounded-2xl border border-white/10 bg-white/5 px-4 py-3 text-sm text-white outline-none"
                    />
                </div>

                <#-- grid that displays all of the available avatars for the user -->
                <div>
                    <label class="text-sm text-white/70">Avatar</label>

                    <#-- grid of avatars for the user -->
                    <div class="mt-3 pcplus-avatar-grid">

                        <#-- each avatar represented as an option -->
                        <#list avatarChoices as avatar>
                            <div class="relative">

                                <#-- radio input that allows the user to select a new avatar -->
                                <input
                                        class="pcplus-avatar-input"
                                        type="radio"
                                        name="avatarId"
                                        id="avatar-${avatar.id}"
                                        value="${avatar.id}"
                                        <#if (profile.avatarId!'av1') == avatar.id>checked</#if>
                                />

                                <#-- label that displays the selected avatar and its name -->
                                <label class="pcplus-avatar-card" for="avatar-${avatar.id}">
                                    <img src="${avatar.imageUrl}" alt="${avatar.name}" class="pcplus-avatar-image" />
                                    <div class="pcplus-avatar-label">${avatar.name}</div>
                                </label>
                            </div>
                        </#list>
                    </div>
                </div>

                <#-- button that saves the changes made to the user's profile -->
                <button class="w-full rounded-2xl bg-accent px-4 py-3 text-sm font-semibold text-white hover:brightness-110 transition" type="submit">
                    Save Profile
                </button>
            </form>
        </section>
    </div>
</main>

<script>
    // javascript file that restores the shared animations for the site

    // script that obtains a reference to the glow element
    const glow = document.getElementById("cursorGlow");

    // updates the glow element's position based on the position of the cursor
    document.addEventListener("mousemove", function (event) {
        document.documentElement.style.setProperty("--mx", event.clientX + "px");
        document.documentElement.style.setProperty("--my", event.clientY + "px");

        if (glow) {
            glow.style.left = event.clientX + "px";
            glow.style.top = event.clientY + "px";
        }
    });

    // selects all of the elements that should animate into view on the screen
    const revealItems = document.querySelectorAll(".pcplus-reveal");

    // observer that will trigger the reveal animations when the page scrolls to these elements
    const revealObserver = new IntersectionObserver(function (entries) {
        entries.forEach(function (entry) {
            if (entry.isIntersecting) {
                entry.target.classList.add("in");
            }
        });
    }, { threshold: 0.08 });

    // begin observing all of the elements that should reveal on scroll
    revealItems.forEach(function (item) {
        revealObserver.observe(item);
    });
</script>

</body>
</html>