(function (w) {
  // ============================================================
  // PC+ Auth Layer
  // Tries the backend API first for login/signup.
  // Falls back to localStorage-only for demo accounts when
  // the backend is offline (prototype / offline mode).
  // ============================================================

  const SESSION_KEY = "pcplus_session";
  const NOTICE_KEY  = "pcplus_auth_notice";
  const AVATAR_KEY  = "pcplus_avatar_by_email";
  const USERS_KEY   = "pcplus_users_v1";
  const JWT_KEY     = "pcplus_jwt";

  const PIN_RE   = /^\d{4}$/;
  const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  const AVATAR_OPTIONS = {
    av1: "🦊", av2: "🐼", av3: "🐸", av4: "🐯", av5: "🐧"
  };

  // Demo accounts always available offline
  const DEMO_SEED = [
    { email: "customer",  password: "customer",  role: "customer",  pin: "0000" },
    { email: "publisher", password: "publisher", role: "publisher", pin: "0000" }
  ];

  const normalizeEmail = (e) => (e || "").trim().toLowerCase();
  const isRole  = (r) => r === "customer" || r === "publisher";
  const isValidEmail = (e) => EMAIL_RE.test(normalizeEmail(e));

  // ---- JWT helpers ----

  function getJwt() { return localStorage.getItem(JWT_KEY) || null; }
  function setJwt(t) {
    if (t) localStorage.setItem(JWT_KEY, t);
    else localStorage.removeItem(JWT_KEY);
  }

  function parseJwt(token) {
    try {
      const parts = (token || "").split(".");
      if (parts.length !== 3) return null;
      const payload = parts[1].replace(/-/g, "+").replace(/_/g, "/");
      return JSON.parse(atob(payload));
    } catch { return null; }
  }

  function jwtValid(token) {
    const p = parseJwt(token);
    if (!p) return false;
    if (p.exp && Date.now() / 1000 > p.exp) { setJwt(null); return false; }
    return true;
  }

  // ---- localStorage user store (offline / demo fallback) ----

  function readUsersRaw() {
    try { const r = JSON.parse(localStorage.getItem(USERS_KEY) || "[]"); return Array.isArray(r) ? r : []; }
    catch { return []; }
  }

  function normalizeUser(record) {
    const now = Date.now();
    const email = normalizeEmail(record && record.email);
    const role  = isRole(record && record.role) ? record.role : "customer";
    const src   = record && record.source === "demo" ? "demo" : "custom";
    return { email, password: String((record && record.password) || ""), role,
             pin: String((record && record.pin) || ""),
             createdAt: Number(record && record.createdAt) || now,
             updatedAt: Number(record && record.updatedAt) || now, source: src };
  }

  function seedUsersIfMissing() {
    const now = Date.now();
    const map = new Map();
    readUsersRaw().map(normalizeUser).forEach(u => { if (u.email && !map.has(u.email)) map.set(u.email, u); });
    DEMO_SEED.forEach(d => {
      const key = normalizeEmail(d.email);
      const ex  = map.get(key);
      map.set(key, { email: key, password: d.password, role: d.role, pin: d.pin,
                     createdAt: ex && ex.createdAt ? ex.createdAt : now,
                     updatedAt: now, source: "demo" });
    });
    const users = Array.from(map.values());
    localStorage.setItem(USERS_KEY, JSON.stringify(users));
    return users;
  }

  function loadUsers() { return seedUsersIfMissing(); }
  function saveUsers(users) { localStorage.setItem(USERS_KEY, JSON.stringify(Array.isArray(users) ? users : [])); }

  function findUserByEmail(email) {
    const key = normalizeEmail(email);
    const users = loadUsers();
    const index = users.findIndex(u => u.email === key);
    return { users, index, user: index >= 0 ? users[index] : null, key };
  }

  // ---- Session ----

  // getSession() is SYNCHRONOUS so every page can call it without async.
  // Priority: valid JWT → localStorage session
  function getSession() {
    // 1. Try JWT (set after successful backend login/signup)
    const token = getJwt();
    if (token && jwtValid(token)) {
      const payload = parseJwt(token);
      if (payload && payload.sub && isRole(payload.role)) {
        return { email: payload.sub, role: payload.role, ts: (payload.iat || 0) * 1000, source: "jwt" };
      }
    }

    // 2. Fall back to localStorage session (demo / offline accounts)
    try {
      const raw = JSON.parse(localStorage.getItem(SESSION_KEY) || "null");
      if (!raw || !raw.email) return null;
      const email = normalizeEmail(raw.email);
      const { user } = findUserByEmail(email);
      if (!user) return null;
      if (!isRole(raw.role) || raw.role !== user.role) return null;
      return { email, role: user.role, ts: Number(raw.ts) || Date.now(), source: "local" };
    } catch { return null; }
  }

  function setSession(session) { localStorage.setItem(SESSION_KEY, JSON.stringify(session)); }

  function getSessionRaw() {
    try {
      const raw = JSON.parse(localStorage.getItem(SESSION_KEY) || "null");
      if (!raw || !raw.email) return null;
      return { email: normalizeEmail(raw.email), role: isRole(raw.role) ? raw.role : "", ts: Number(raw.ts) || Date.now() };
    } catch { return null; }
  }

  // ---- Avatar ----

  function loadAvatarMap() { try { return JSON.parse(localStorage.getItem(AVATAR_KEY) || "{}"); } catch { return {}; } }
  function saveAvatarMap(m) { localStorage.setItem(AVATAR_KEY, JSON.stringify(m || {})); }

  function getAvatarId(email) {
    const s = getSession();
    const key = normalizeEmail(email || (s && s.email) || "");
    if (!key) return "av1";
    const map = loadAvatarMap();
    const cur = map[key];
    if (cur && AVATAR_OPTIONS[cur]) return cur;
    map[key] = "av1"; saveAvatarMap(map); return "av1";
  }

  function getAvatarEmoji(email) { return AVATAR_OPTIONS[getAvatarId(email)] || AVATAR_OPTIONS.av1; }

  function setAvatarId(email, avatarId) {
    const key = normalizeEmail(email);
    if (!key || !AVATAR_OPTIONS[avatarId]) return;
    const map = loadAvatarMap(); map[key] = avatarId; saveAvatarMap(map);
    // Also persist to backend (fire-and-forget)
    if (w.PCPlusAPI && getJwt()) w.PCPlusAPI.setAvatar(avatarId).catch(() => {});
  }

  // ---- Login ----
  // Returns a Promise<{ ok, error?, session? }>

  async function login(email, password) {
    const normalized = normalizeEmail(email);

    // Try backend first
    if (w.PCPlusAPI) {
      try {
        const res = await w.PCPlusAPI.login(normalized, password);
        if (res.ok && res.data && res.data.token) {
          setJwt(res.data.token);
          // Sync avatar id from backend
          if (res.data.avatarId && AVATAR_OPTIONS[res.data.avatarId]) {
            const map = loadAvatarMap();
            map[normalized] = res.data.avatarId;
            saveAvatarMap(map);
          }
          const session = { email: normalized, role: res.data.role, ts: Date.now() };
          setSession(session);
          return { ok: true, session };
        }
        if (res.status === 401 || res.status === 403) {
          return { ok: false, error: "invalid_credentials" };
        }
      } catch (_) {
        // Backend unreachable — fall through to offline mode
      }
    }

    // Offline fallback: demo accounts only
    const { user } = findUserByEmail(normalized);
    if (!user || user.password !== String(password || "")) {
      return { ok: false, error: "invalid_credentials" };
    }
    const session = { email: normalized, role: user.role, ts: Date.now() };
    setSession(session);
    return { ok: true, session };
  }

  // ---- Signup ----
  // Returns a Promise<{ ok, error?, session? }>

  async function signup(payload) {
    const email    = normalizeEmail(payload && payload.email);
    const password = String((payload && payload.password) || "");
    const role     = String((payload && payload.role) || "");
    const pin      = String((payload && payload.pin) || "");

    if (!isValidEmail(email))  return { ok: false, error: "invalid_email" };
    if (!password)             return { ok: false, error: "invalid_password" };
    if (!isRole(role))         return { ok: false, error: "invalid_role" };
    if (!PIN_RE.test(pin))     return { ok: false, error: "invalid_pin" };

    // Try backend first
    if (w.PCPlusAPI) {
      try {
        const res = await w.PCPlusAPI.signup(email, password, role, pin);
        if (res.ok && res.data && res.data.token) {
          setJwt(res.data.token);
          const map = loadAvatarMap();
          if (!map[email]) { map[email] = "av1"; saveAvatarMap(map); }
          const session = { email, role: res.data.role, ts: Date.now() };
          setSession(session);
          return { ok: true, session };
        }
        if (res.data && res.data.error === "email_taken") {
          return { ok: false, error: "email_taken" };
        }
        if (res.status !== 0) {
          // Backend responded with an error we don't understand
          return { ok: false, error: "server_error" };
        }
        // status 0 = network error → fall through to offline
      } catch (_) {
        // Backend unreachable
      }
    }

    // Offline fallback: save to localStorage only
    const { users, user } = findUserByEmail(email);
    if (user) return { ok: false, error: "email_taken" };

    const now = Date.now();
    users.push({ email, password, role, pin, createdAt: now, updatedAt: now, source: "custom" });
    saveUsers(users);

    const map = loadAvatarMap();
    if (!map[email]) { map[email] = "av1"; saveAvatarMap(map); }

    const session = { email, role, ts: now };
    setSession(session);
    return { ok: true, session };
  }

  // ---- Reset password ----
  // Returns a Promise<{ ok, error? }>

  async function resetPassword(payload) {
    const email       = normalizeEmail(payload && payload.email);
    const pin         = String((payload && payload.pin) || "");
    const newPassword = String((payload && payload.newPassword) || "");

    if (!email)             return { ok: false, error: "invalid_email" };
    if (!PIN_RE.test(pin))  return { ok: false, error: "invalid_pin" };
    if (!newPassword)       return { ok: false, error: "invalid_password" };

    // Try backend first
    if (w.PCPlusAPI) {
      try {
        const res = await w.PCPlusAPI.resetPassword(email, pin, newPassword);
        if (res.ok) return { ok: true };
        if (res.data && res.data.error) return { ok: false, error: res.data.error };
        if (res.status !== 0) return { ok: false, error: "server_error" };
      } catch (_) {}
    }

    // Offline fallback
    const { users, index, user } = findUserByEmail(email);
    if (!user) return { ok: false, error: "account_not_found" };
    if (String(user.pin) !== pin) return { ok: false, error: "pin_mismatch" };
    users[index] = { ...user, password: newPassword, updatedAt: Date.now() };
    saveUsers(users);
    return { ok: true };
  }

  // ---- Change email (local only) ----
  function changeEmail(payload) {
    const currentEmail = normalizeEmail(payload && payload.currentEmail);
    const pin          = String((payload && payload.pin) || "");
    const newEmail     = normalizeEmail(payload && payload.newEmail);

    if (!currentEmail)              return { ok: false, error: "invalid_email" };
    if (!PIN_RE.test(pin))          return { ok: false, error: "invalid_pin" };
    if (!isValidEmail(newEmail))    return { ok: false, error: "invalid_email" };

    const { users, index, user } = findUserByEmail(currentEmail);
    if (!user) return { ok: false, error: "account_not_found" };
    if (String(user.pin) !== pin) return { ok: false, error: "pin_mismatch" };

    if (newEmail !== currentEmail) {
      const { user: existing } = findUserByEmail(newEmail);
      if (existing) return { ok: false, error: "email_taken" };
    }

    users[index] = { ...user, email: newEmail, updatedAt: Date.now() };
    saveUsers(users);

    const avatarMap = loadAvatarMap();
    if (Object.prototype.hasOwnProperty.call(avatarMap, currentEmail)) {
      avatarMap[newEmail] = avatarMap[currentEmail];
      delete avatarMap[currentEmail];
      saveAvatarMap(avatarMap);
    }

    const rawSession = getSessionRaw();
    if (rawSession && rawSession.email === currentEmail) {
      const session = { email: newEmail, role: users[index].role, ts: Date.now() };
      setSession(session);
      return { ok: true, session };
    }
    return { ok: true };
  }

  // ---- Change role (local only) ----
  function changeRole(payload) {
    const email = normalizeEmail(payload && payload.email);
    const pin   = String((payload && payload.pin) || "");
    const role  = String((payload && payload.role) || "");

    if (!email)             return { ok: false, error: "invalid_email" };
    if (!PIN_RE.test(pin))  return { ok: false, error: "invalid_pin" };
    if (!isRole(role))      return { ok: false, error: "invalid_role" };

    const { users, index, user } = findUserByEmail(email);
    if (!user) return { ok: false, error: "account_not_found" };
    if (String(user.pin) !== pin) return { ok: false, error: "pin_mismatch" };

    users[index] = { ...user, role, updatedAt: Date.now() };
    saveUsers(users);

    const rawSession = getSessionRaw();
    if (rawSession && rawSession.email === email) {
      const session = { email, role, ts: Date.now() };
      setSession(session);
      return { ok: true, session };
    }
    return { ok: true };
  }

  function getAccount(email) {
    const { user } = findUserByEmail(email);
    if (!user) return null;
    return { email: user.email, role: user.role, source: user.source, createdAt: user.createdAt, updatedAt: user.updatedAt };
  }

  function logout() {
    setJwt(null);
    localStorage.removeItem(SESSION_KEY);
  }

  function setNotice(message) {
    if (!message) { localStorage.removeItem(NOTICE_KEY); return; }
    localStorage.setItem(NOTICE_KEY, String(message));
  }

  function consumeNotice() {
    const msg = localStorage.getItem(NOTICE_KEY);
    localStorage.removeItem(NOTICE_KEY);
    return msg || "";
  }

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

  function buildDemoUsersMap() {
    const demos = {};
    loadUsers().forEach(u => { if (u.source === "demo") demos[u.email] = { password: u.password, role: u.role }; });
    return demos;
  }

  // Initialize
  seedUsersIfMissing();

  w.PCPlusAuth = {
    getSession, login, signup, resetPassword,
    changeEmail, changeRole, getAccount, logout,
    requireRole, setNotice, consumeNotice,
    getAvatarId, getAvatarEmoji, setAvatarId,
    AVATAR_OPTIONS,
    get DEMO_USERS() { return buildDemoUsersMap(); }
  };
})(window);
