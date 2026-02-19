// Publisher prototype data and page helpers.
// Uses localStorage as a lightweight in-browser backend.

// Storage and image defaults.
const STORAGE_KEY = "games_v1";
const FALLBACK_STORE_IMAGE = "../assets/img/sale.png";
const IMAGE_PLACEHOLDER = "../assets/img/your-image-here.jpg";

// Normalizes IDs so customer and publisher pages use the same key format.
function normalizeStoreKey(value) {
  return String(value || "")
    .toLowerCase()
    .replace(/[^a-z0-9]/g, "");
}

// Produces a compact title slug for store keys.
function slugifyTitle(value) {
  return String(value || "game")
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, "")
    .slice(0, 24);
}

// Builds stable store key for cross-page routing (home/details/cart/library).
function makeStoreKey(title, id, index) {
  const seed = normalizeStoreKey(id) || String(index || 0);
  return normalizeStoreKey(`pub${slugifyTitle(title)}${seed.slice(0, 8)}`) || `pubgame${Date.now()}`;
}

// Accepts filename/path/url and normalizes it to a store-safe image path.
function normalizePublisherImagePath(input) {
  const raw = String(input || "").trim();
  if (!raw) return IMAGE_PLACEHOLDER;

  const slashFixed = raw.replace(/\\/g, "/");
  if (/^https?:\/\//i.test(slashFixed)) return slashFixed;

  const stripped = slashFixed.replace(/^(?:\.\.\/|\.\/|\/)+/, "");
  const lower = stripped.toLowerCase();
  const marker = "assets/img/";

  let filePart = "";
  if (lower.includes(marker)) {
    const idx = lower.lastIndexOf(marker);
    filePart = stripped.slice(idx + marker.length);
  } else {
    const pieces = stripped.split("/").filter(Boolean);
    filePart = pieces[pieces.length - 1] || "";
  }

  filePart = filePart.trim();
  if (!filePart) return IMAGE_PLACEHOLDER;

  return "../assets/img/" + filePart;
}

// Detects placeholder/filler image values.
function isPlaceholderImagePath(path) {
  const raw = String(path || "").trim();
  if (!raw) return true;
  const lower = raw.toLowerCase();
  return (
    lower.includes("your-image-here") ||
    lower.includes("replace-with-image") ||
    lower.includes("placeholder")
  );
}

// Resolves image path to fallback when placeholder is used.
function resolveImagePath(path) {
  return isPlaceholderImagePath(path) ? FALLBACK_STORE_IMAGE : String(path).trim();
}

// Normalizes one game record to keep data shape consistent.
function normalizeGameRecord(record, index) {
  const next = { ...(record || {}) };
  next.id = String(next.id || crypto.randomUUID());
  next.title = String(next.title || "Untitled Game").trim();
  next.price = Number.isFinite(Number(next.price)) ? Number(next.price) : 0;
  next.genre = String(next.genre || "Action");
  next.status = String(next.status || "Live");
  next.requirements = String(next.requirements || "Requirements not listed.");
  next.description = String(next.description || "Publisher listing.");
  next.updatedAt = String(next.updatedAt || new Date().toISOString());
  next.publisherName = String(next.publisherName || "Publisher");
  next.storeKey = normalizeStoreKey(next.storeKey) || makeStoreKey(next.title, next.id, index);
  next.imagePath = normalizePublisherImagePath(next.imagePath || IMAGE_PLACEHOLDER);
  return next;
}

// Compatibility shim: no auto-seeding, but keeps old callsites safe.
function seedIfEmpty() {
  loadGames();
}

// Loads and normalizes all publisher games.
function loadGames() {
  let parsed = [];
  try {
    parsed = JSON.parse(localStorage.getItem(STORAGE_KEY)) ?? [];
  } catch {
    parsed = [];
  }
  if (!Array.isArray(parsed)) parsed = [];

  const normalized = parsed.map((record, index) => normalizeGameRecord(record, index));
  if (JSON.stringify(parsed) !== JSON.stringify(normalized)) {
    saveGames(normalized);
  }
  return normalized;
}

// Saves all publisher games.
function saveGames(games) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(games || []));
}

// Small toast helper used across publisher pages.
function toast(msg) {
  const el = document.querySelector(".toast");
  if (!el) return alert(msg);
  el.textContent = msg;
  el.classList.add("show");
  setTimeout(() => el.classList.remove("show"), 2000);
}

// Formats number as USD-like price string.
function fmtMoney(n) {
  return `$${Number(n).toFixed(2)}`;
}

// Enforces exact publisher nav order across pages.
function enforcePublisherNavOrder() {
  const nav = document.querySelector(".nav");
  if (!nav) return;

  const home = nav.querySelector('a[href="../customer/customer-home.html"]');
  const dashboard = nav.querySelector('a[href="dashboard.html"]');
  const manage = nav.querySelector('a[href="manage-games.html"]');
  const upload = nav.querySelector('a[href="upload-game.html"]');
  const profile = nav.querySelector('a[href="publisher-profile.html"]');
  const state = nav.querySelector(".nav-login-state");

  [home, dashboard, manage, upload, profile, state].forEach((el) => {
    if (el) nav.appendChild(el);
  });
}

// Marks the current page link as active.
function setActiveNav() {
  const path = location.pathname.split("/").pop();
  document.querySelectorAll(".nav a").forEach((a) => {
    const href = a.getAttribute("href");
    if (href && href.endsWith(path)) a.classList.add("active");
  });
}

// Blocks access unless session is publisher role.
function requirePublisherSession() {
  const auth = window.PCPlusAuth;
  if (!auth) return true;
  return auth.requireRole("publisher", "../customer/login.html");
}

// Topbar search Enter key redirects to customer home search.
function bindTopbarSearchRedirect() {
  const input = document.querySelector(".search-box input");
  if (!input) return;

  input.addEventListener("keydown", (e) => {
    if (e.key !== "Enter") return;
    e.preventDefault();

    const q = String(input.value || "").trim();
    const base = "../customer/customer-home.html";
    window.location.href = q ? `${base}?q=${encodeURIComponent(q)}` : base;
  });
}

// Injects "Logged in <avatar>" status entry into publisher nav.
function renderPublisherStatus() {
  const nav = document.querySelector(".nav");
  if (!nav) return;

  nav.querySelectorAll(".nav-login-state").forEach((el) => el.remove());

  const s = window.PCPlusAuth ? window.PCPlusAuth.getSession() : null;
  if (!(s && s.email)) return;

  const avatar = window.PCPlusAuth ? window.PCPlusAuth.getAvatarEmoji(s.email) : "🦊";
  const stateLink = document.createElement("a");
  stateLink.className = "nav-login-state";
  stateLink.href = "../customer/login.html";
  stateLink.textContent = "Logged in " + avatar;
  nav.appendChild(stateLink);
}

// Renders Manage Games table from current games_v1 data.
function renderManageGames() {
  seedIfEmpty();
  const tbody = document.querySelector("#gamesBody");
  const empty = document.querySelector("#emptyState");
  if (!tbody) return;

  const games = loadGames();
  tbody.innerHTML = "";

  if (games.length === 0) {
    empty?.removeAttribute("hidden");
    return;
  }
  empty?.setAttribute("hidden", "true");

  for (const g of games) {
    const tr = document.createElement("tr");
    tr.dataset.id = g.id;

    tr.innerHTML = `
      <td>
        <strong>${g.title}</strong>
        <div class="small">${g.genre} • <span class="muted">${g.publisherName ?? "Publisher"}</span></div>
      </td>
      <td>${fmtMoney(g.price)}</td>
      <td><span class="pill live">${g.status}</span></td>
      <td class="muted">${new Date(g.updatedAt).toLocaleString()}</td>
      <td>
        <div class="actions">
          <a class="btn" href="edit-game.html?id=${encodeURIComponent(g.id)}">Edit</a>
          <button class="btn danger" data-action="delete">Delete</button>
        </div>
      </td>
    `;
    tbody.appendChild(tr);
  }

  tbody.addEventListener(
    "click",
    (e) => {
      const btn = e.target.closest("button[data-action='delete']");
      if (!btn) return;

      const row = btn.closest("tr");
      const id = row?.dataset?.id;
      if (!id) return;

      const updated = loadGames().filter((x) => x.id !== id);
      saveGames(updated);

      row.remove();
      toast("Deleted listing (storefront will update too).");

      if (updated.length === 0) {
        empty?.removeAttribute("hidden");
      }
    },
    { once: true }
  );
}

// Handles Upload form submit and saves a new listing.
function handleUploadForm() {
  seedIfEmpty();
  const form = document.querySelector("#uploadForm");
  if (!form) return;

  form.addEventListener("submit", (e) => {
    e.preventDefault();

    const title = form.title.value.trim();
    const price = Number(form.price.value);
    const genre = form.genre.value;
    const requirements = form.requirements.value.trim();
    const description = form.description.value.trim();
    const imagePath = normalizePublisherImagePath(form.imagePath?.value || "");

    if (!title || Number.isNaN(price) || price < 0) {
      toast("Enter a valid title + price.");
      return;
    }

    if (!requirements || !description) {
      toast("System requirements and description are required.");
      return;
    }

    const id = crypto.randomUUID();
    const games = loadGames();
    games.unshift(
      normalizeGameRecord(
        {
          id,
          title,
          price,
          genre,
          status: "Live",
          requirements,
          description,
          updatedAt: new Date().toISOString(),
          publisherName: "Eddy Studios",
          storeKey: makeStoreKey(title, id, games.length + 1),
          imagePath
        },
        games.length + 1
      )
    );
    saveGames(games);

    toast("Game published (visible on storefront).");
    setTimeout(() => (window.location.href = "manage-games.html"), 600);
  });
}

// Loads selected listing into Edit form and saves updates.
function handleEditForm() {
  seedIfEmpty();
  const form = document.querySelector("#editForm");
  if (!form) return;

  const params = new URLSearchParams(location.search);
  const id = params.get("id");
  if (!id) return toast("Missing game id.");

  const games = loadGames();
  const game = games.find((g) => g.id === id);
  if (!game) return toast("Game not found.");

  form.title.value = game.title;
  form.price.value = game.price;
  form.genre.value = game.genre;
  form.requirements.value = game.requirements;
  form.description.value = game.description;
  if (form.imagePath) form.imagePath.value = game.imagePath || IMAGE_PLACEHOLDER;

  form.addEventListener("submit", (e) => {
    e.preventDefault();

    game.title = form.title.value.trim();
    game.price = Number(form.price.value);
    game.genre = form.genre.value;
    game.requirements = form.requirements.value.trim();
    game.description = form.description.value.trim();
    game.imagePath = normalizePublisherImagePath(form.imagePath?.value || "");

    if (!game.title || Number.isNaN(game.price) || game.price < 0) {
      toast("Enter a valid title + price.");
      return;
    }
    if (!game.requirements || !game.description) {
      toast("System requirements and description are required.");
      return;
    }

    game.updatedAt = new Date().toISOString();
    game.storeKey = normalizeStoreKey(game.storeKey) || makeStoreKey(game.title, game.id, 0);

    saveGames(games.map((entry, index) => normalizeGameRecord(entry, index)));

    toast("Changes saved (storefront will update too).");
    setTimeout(() => (window.location.href = "manage-games.html"), 600);
  });
}

// Renders dashboard KPIs and recent activity list.
function renderDashboard() {
  seedIfEmpty();
  const games = loadGames();

  const totalGames = document.querySelector("#kpiTotalGames");
  const totalSales = document.querySelector("#kpiTotalSales");
  const lastUpdated = document.querySelector("#kpiLastUpdated");

  if (totalGames) totalGames.textContent = games.length;

  // Revenue estimate is derived from owned library entries matching publisher store keys.
  let estimatedRevenue = 0;
  try {
    const owned = JSON.parse(localStorage.getItem("pcplus_library") || "[]");
    const salesPriceMap = new Map();

    games.forEach((g) => {
      const key = normalizeStoreKey(g.storeKey);
      if (!key) return;
      salesPriceMap.set(key, Number.isFinite(Number(g.price)) ? Number(g.price) : 0);
    });

    if (Array.isArray(owned)) {
      estimatedRevenue = owned.reduce((sum, id) => {
        const key = normalizeStoreKey(id);
        return sum + (salesPriceMap.get(key) || 0);
      }, 0);
    }
  } catch {
    estimatedRevenue = 0;
  }

  if (totalSales) totalSales.textContent = fmtMoney(estimatedRevenue);

  const latest = games[0]?.updatedAt;
  if (lastUpdated) lastUpdated.textContent = latest ? new Date(latest).toLocaleString() : "—";

  const list = document.querySelector("#recentList");
  if (list) {
    list.innerHTML = "";
    games.slice(0, 3).forEach((g) => {
      const li = document.createElement("li");
      li.innerHTML = `<strong>${g.title}</strong> <span class="muted">updated ${new Date(g.updatedAt).toLocaleString()}</span>`;
      list.appendChild(li);
    });
  }
}

// Public data helpers consumed by customer pages.
window.PCPublisherData = {
  STORAGE_KEY,
  FALLBACK_STORE_IMAGE,
  IMAGE_PLACEHOLDER,
  loadGames,
  saveGames,
  normalizeStoreKey,
  isPlaceholderImagePath,
  resolveImagePath,
  normalizePublisherImagePath
};

// Shared bootstrap: run only the handlers relevant to current page markup.
document.addEventListener("DOMContentLoaded", () => {
  if (!requirePublisherSession()) return;
  bindTopbarSearchRedirect();
  renderPublisherStatus();
  enforcePublisherNavOrder();
  setActiveNav();
  renderManageGames();
  handleUploadForm();
  handleEditForm();
  renderDashboard();
});
