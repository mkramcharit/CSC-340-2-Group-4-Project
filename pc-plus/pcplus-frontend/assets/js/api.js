// ============================================================
// PC+ Backend API Client (api.js)
// ============================================================
// CONFIGURATION: Change this URL to your Spring Boot server.
// If running locally: http://localhost:8080
// If deployed: https://your-server.com
// ============================================================
const PCPLUS_API_BASE = "http://localhost:8080";

(function (w) {
  "use strict";

  const JWT_KEY = "pcplus_jwt";

  // ---- Token Helpers ----

  function getToken() {
    return localStorage.getItem(JWT_KEY) || null;
  }

  function setToken(token) {
    if (token) localStorage.setItem(JWT_KEY, token);
    else localStorage.removeItem(JWT_KEY);
  }

  // Decode JWT payload without a library (base64url decode)
  function parseJwt(token) {
    try {
      const parts = (token || "").split(".");
      if (parts.length !== 3) return null;
      const payload = parts[1].replace(/-/g, "+").replace(/_/g, "/");
      return JSON.parse(atob(payload));
    } catch {
      return null;
    }
  }

  // Returns true if the stored JWT is present and not expired
  function isTokenValid() {
    const token = getToken();
    if (!token) return false;
    const payload = parseJwt(token);
    if (!payload) return false;
    if (payload.exp && Date.now() / 1000 > payload.exp) {
      setToken(null); // auto-clear expired token
      return false;
    }
    return true;
  }

  // ---- Core fetch wrapper ----
  // Returns { ok: bool, status: number, data: any, error: string|null }

  async function apiFetch(path, opts) {
    opts = opts || {};
    const token = getToken();
    const headers = Object.assign({ "Content-Type": "application/json" }, opts.headers || {});
    if (token) headers["Authorization"] = "Bearer " + token;

    try {
      const controller = new AbortController();
      const timeout = setTimeout(() => controller.abort(), 8000);

      const res = await fetch(PCPLUS_API_BASE + path, {
        method: opts.method || "GET",
        headers: headers,
        body: opts.body || undefined,
        signal: controller.signal
      });
      clearTimeout(timeout);

      let data = null;
      const ct = res.headers.get("content-type") || "";
      if (ct.includes("application/json")) {
        data = await res.json().catch(() => null);
      }

      return { ok: res.ok, status: res.status, data: data, error: null };
    } catch (err) {
      // Network error or timeout — backend is likely offline.
      // This console warning helps developers see why nothing is saving to Neon.
      if (err && err.name === "AbortError") {
        console.warn("[PC+ API] Request timed out:", PCPLUS_API_BASE + path,
          "\n→ Is the Spring Boot backend running? Run: cd pcplus-backend && mvn spring-boot:run");
      } else {
        console.warn("[PC+ API] Backend unreachable:", PCPLUS_API_BASE + path,
          "\n→ Is the Spring Boot backend running? Run: cd pcplus-backend && mvn spring-boot:run",
          err && err.message);
      }
      return { ok: false, status: 0, data: null, error: "network_error" };
    }
  }

  // Quick check if the backend is reachable (non-blocking)
  async function ping() {
    try {
      const controller = new AbortController();
      setTimeout(() => controller.abort(), 3000);
      const res = await fetch(PCPLUS_API_BASE + "/api/games/top-sellers", {
        signal: controller.signal
      });
      return res.ok || res.status === 401 || res.status === 403;
    } catch {
      return false;
    }
  }

  // ---- Auth API ----

  function signup(email, password, role, pin) {
    return apiFetch("/api/auth/signup", {
      method: "POST",
      body: JSON.stringify({ email: email, password: password, role: role, pin: pin })
    });
  }

  function login(email, password) {
    return apiFetch("/api/auth/login", {
      method: "POST",
      body: JSON.stringify({ email: email, password: password })
    });
  }

  function resetPassword(email, pin, newPassword) {
    return apiFetch("/api/auth/reset-password", {
      method: "POST",
      body: JSON.stringify({ email: email, pin: pin, newPassword: newPassword })
    });
  }

  function getMe() {
    return apiFetch("/api/auth/me");
  }

  function setAvatar(avatarId) {
    return apiFetch("/api/auth/avatar", {
      method: "PATCH",
      body: JSON.stringify({ avatarId: avatarId })
    });
  }

  // ---- Games API (public) ----

  function getGames() {
    return apiFetch("/api/games");
  }

  function getGame(id) {
    return apiFetch("/api/games/" + id);
  }

  function getTopSellers() {
    return apiFetch("/api/games/top-sellers");
  }

  function getNewest() {
    return apiFetch("/api/games/newest");
  }

  function getOnSale() {
    return apiFetch("/api/games/on-sale");
  }

  function searchGames(q) {
    return apiFetch("/api/games/search?q=" + encodeURIComponent(q || ""));
  }

  // ---- Cart API (requires auth) ----

  function getCart() {
    return apiFetch("/api/cart");
  }

  function addToCart(gameId) {
    return apiFetch("/api/cart", {
      method: "POST",
      body: JSON.stringify({ gameId: gameId })
    });
  }

  function removeFromCart(gameId) {
    return apiFetch("/api/cart/" + gameId, { method: "DELETE" });
  }

  function clearCart() {
    return apiFetch("/api/cart", { method: "DELETE" });
  }

  // ---- Library / Purchases API ----

  function getLibrary() {
    return apiFetch("/api/library");
  }

  function ownsGame(gameId) {
    return apiFetch("/api/library/owns/" + gameId);
  }

  function checkout() {
    return apiFetch("/api/library/checkout", { method: "POST" });
  }

  function buyNow(gameId) {
    return apiFetch("/api/library/buy/" + gameId, { method: "POST" });
  }

  // ---- Publisher API (requires ROLE_PUBLISHER) ----

  function getMyGames() {
    return apiFetch("/api/publisher/games");
  }

  function createGame(gameData) {
    return apiFetch("/api/publisher/games", {
      method: "POST",
      body: JSON.stringify(gameData)
    });
  }

  function updateGame(id, gameData) {
    return apiFetch("/api/publisher/games/" + id, {
      method: "PUT",
      body: JSON.stringify(gameData)
    });
  }

  function deleteGame(id) {
    return apiFetch("/api/publisher/games/" + id, { method: "DELETE" });
  }

  function getDashboard() {
    return apiFetch("/api/publisher/dashboard");
  }

  function getMyReviews() {
    return apiFetch("/api/publisher/reviews");
  }

  function getPublisherProfile() {
    return apiFetch("/api/publisher/profile");
  }

  function updatePublisherProfile(profileData) {
    return apiFetch("/api/publisher/profile", {
      method: "PATCH",
      body: JSON.stringify(profileData || {})
    });
  }

  // ---- Reviews API ----

  function getReviews(gameId) {
    return apiFetch("/api/games/" + gameId + "/reviews");
  }

  function submitReview(gameId, rating, body) {
    return apiFetch("/api/games/" + gameId + "/reviews", {
      method: "POST",
      body: JSON.stringify({ rating: rating, body: body })
    });
  }

  // ---- Public API Surface ----

  w.PCPlusAPI = {
    // Config
    BASE: PCPLUS_API_BASE,

    // Token management
    getToken: getToken,
    setToken: setToken,
    parseJwt: parseJwt,
    isTokenValid: isTokenValid,

    // Connection check
    ping: ping,

    // Auth
    signup: signup,
    login: login,
    resetPassword: resetPassword,
    getMe: getMe,
    setAvatar: setAvatar,

    // Games
    getGames: getGames,
    getGame: getGame,
    getTopSellers: getTopSellers,
    getNewest: getNewest,
    getOnSale: getOnSale,
    searchGames: searchGames,

    // Cart
    getCart: getCart,
    addToCart: addToCart,
    removeFromCart: removeFromCart,
    clearCart: clearCart,

    // Library
    getLibrary: getLibrary,
    ownsGame: ownsGame,
    checkout: checkout,
    buyNow: buyNow,

    // Publisher
    getMyGames: getMyGames,
    createGame: createGame,
    updateGame: updateGame,
    deleteGame: deleteGame,
    getDashboard: getDashboard,
    getMyReviews: getMyReviews,
    getPublisherProfile: getPublisherProfile,
    updatePublisherProfile: updatePublisherProfile,

    // Reviews
    getReviews: getReviews,
    submitReview: submitReview
  };

})(window);
