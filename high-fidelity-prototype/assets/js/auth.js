(function (w) {
  // Storage keys used by the prototype auth/session layer.
  const SESSION_KEY = "pcplus_session";
  const NOTICE_KEY = "pcplus_auth_notice";
  const AVATAR_KEY = "pcplus_avatar_by_email";

  // Allowed avatar choices shown in nav/login status.
  const AVATAR_OPTIONS = {
    av1: "🦊",
    av2: "🐼",
    av3: "🐸",
    av4: "🐯",
    av5: "🐧"
  };

  // Fixed demo accounts for this prototype.
  const DEMO_USERS = {
    "customer@example.com": { password: "customer123", role: "customer" },
    "publisher@example.com": { password: "publisher123", role: "publisher" }
  };

  // Email normalization keeps lookups consistent.
  const normalizeEmail = (email) => (email || "").trim().toLowerCase();

  // Reads and validates session data from localStorage.
  function getSession() {
    try {
      const raw = JSON.parse(localStorage.getItem(SESSION_KEY) || "null");
      if (!raw || !raw.email) return null;

      const normalized = normalizeEmail(raw.email);
      const known = DEMO_USERS[normalized];
      if (!known) return null;

      const role = (raw.role === "customer" || raw.role === "publisher")
        ? raw.role
        : known.role;
      if (role !== known.role) return null;

      return {
        email: normalized,
        role,
        ts: raw.ts || Date.now()
      };
    } catch {
      return null;
    }
  }

  // Writes current session object.
  function setSession(session) {
    localStorage.setItem(SESSION_KEY, JSON.stringify(session));
  }

  // Reads avatar map: { [email]: avatarId }.
  function loadAvatarMap() {
    try {
      return JSON.parse(localStorage.getItem(AVATAR_KEY) || "{}");
    } catch {
      return {};
    }
  }

  // Saves avatar map.
  function saveAvatarMap(map) {
    localStorage.setItem(AVATAR_KEY, JSON.stringify(map || {}));
  }

  // Gets avatar id for an email (or active session), defaulting to av1.
  function getAvatarId(email) {
    const s = getSession();
    const key = normalizeEmail(email || (s && s.email) || "");
    if (!key) return "av1";

    const map = loadAvatarMap();
    const current = map[key];
    if (current && AVATAR_OPTIONS[current]) return current;

    map[key] = "av1";
    saveAvatarMap(map);
    return "av1";
  }

  // Returns emoji for avatar id.
  function getAvatarEmoji(email) {
    return AVATAR_OPTIONS[getAvatarId(email)] || AVATAR_OPTIONS.av1;
  }

  // Sets avatar id for a given email.
  function setAvatarId(email, avatarId) {
    const key = normalizeEmail(email);
    if (!key || !AVATAR_OPTIONS[avatarId]) return;
    const map = loadAvatarMap();
    map[key] = avatarId;
    saveAvatarMap(map);
  }

  // Validates demo credentials and sets session.
  function login(email, password) {
    const normalized = normalizeEmail(email);
    const user = DEMO_USERS[normalized];
    if (!user || user.password !== (password || "")) {
      return { ok: false, error: "invalid_credentials" };
    }

    const session = { email: normalized, role: user.role, ts: Date.now() };
    setSession(session);
    return { ok: true, session };
  }

  // Clears auth session.
  function logout() {
    localStorage.removeItem(SESSION_KEY);
  }

  // Stores a one-time auth notice to show on login page.
  function setNotice(message) {
    if (!message) {
      localStorage.removeItem(NOTICE_KEY);
      return;
    }
    localStorage.setItem(NOTICE_KEY, String(message));
  }

  // Returns and clears the one-time auth notice.
  function consumeNotice() {
    const msg = localStorage.getItem(NOTICE_KEY);
    localStorage.removeItem(NOTICE_KEY);
    return msg || "";
  }

  // Guards route access by role and redirects when blocked.
  function requireRole(requiredRole, loginPath) {
    const next = loginPath || "customer/login.html";
    const s = getSession();

    if (!s || !s.email) {
      setNotice("Please log in to continue.");
      location.href = next;
      return false;
    }

    if (requiredRole && s.role !== requiredRole) {
      setNotice("Please log in with the correct account type for that page.");
      location.href = next;
      return false;
    }

    return true;
  }

  // Public auth helper used by customer and publisher pages.
  w.PCPlusAuth = {
    getSession,
    login,
    logout,
    requireRole,
    setNotice,
    consumeNotice,
    getAvatarId,
    getAvatarEmoji,
    setAvatarId,
    AVATAR_OPTIONS,
    DEMO_USERS
  };
})(window);
