-- ============================================================
--  PC+ Game Store – PostgreSQL Schema
--  Run this once against your Neon.tech database to set up tables.
--
--  How to run:
--    1. Go to https://console.neon.tech
--    2. Open your project → SQL Editor
--    3. Paste this entire file and click Run
--
--  NOTE: Spring Boot (ddl-auto=update) will also create/update
--  these tables automatically on startup. This file is provided
--  for manual setup and initial seed data.
-- ============================================================

-- Drop order (children before parents)
DROP TABLE IF EXISTS cart_items  CASCADE;
DROP TABLE IF EXISTS purchases   CASCADE;
DROP TABLE IF EXISTS reviews     CASCADE;
DROP TABLE IF EXISTS games       CASCADE;
DROP TABLE IF EXISTS users       CASCADE;

-- ============================================================
-- USERS
-- ============================================================
CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(20)  NOT NULL CHECK (role IN ('customer','publisher','admin')),
    pin           VARCHAR(4)   NOT NULL,
    avatar_id     VARCHAR(10)  NOT NULL DEFAULT 'av1',
    display_name  VARCHAR(100),
    payout_method VARCHAR(32),
    support_contact VARCHAR(255),
    active        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_email ON users(email);

-- ============================================================
-- GAMES
-- ============================================================
CREATE TABLE games (
    id             BIGSERIAL PRIMARY KEY,
    title          VARCHAR(150)   NOT NULL,
    publisher      VARCHAR(150),
    owner_id       BIGINT         REFERENCES users(id) ON DELETE SET NULL,
    description    TEXT,
    price          NUMERIC(10,2)  NOT NULL DEFAULT 0.00,
    sale_price     NUMERIC(10,2),
    genres         VARCHAR(300),
    cover_image    VARCHAR(255),
    screenshots    TEXT,
    req_os         VARCHAR(100),
    req_cpu        VARCHAR(100),
    req_ram        VARCHAR(100),
    req_gpu        VARCHAR(100),
    req_storage    VARCHAR(100),
    status         VARCHAR(20)    NOT NULL DEFAULT 'live',
    download_count INT            NOT NULL DEFAULT 0,
    avg_rating     NUMERIC(3,2)   DEFAULT 0.00,
    review_count   INT            NOT NULL DEFAULT 0,
    playable       BOOLEAN        NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_games_status          ON games(status);
CREATE INDEX idx_games_download_count  ON games(download_count DESC);
CREATE INDEX idx_games_owner           ON games(owner_id);

-- ============================================================
-- REVIEWS
-- ============================================================
CREATE TABLE reviews (
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    game_id          BIGINT       NOT NULL REFERENCES games(id) ON DELETE CASCADE,
    rating           SMALLINT     NOT NULL CHECK (rating BETWEEN 1 AND 5),
    body             TEXT,
    publisher_reply  TEXT,
    replied_at       TIMESTAMPTZ,
    removed          BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, game_id)
);

CREATE INDEX idx_reviews_game_id ON reviews(game_id);
CREATE INDEX idx_reviews_user_id ON reviews(user_id);

-- ============================================================
-- PURCHASES  (= Library)
-- ============================================================
CREATE TABLE purchases (
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    game_id      BIGINT       NOT NULL REFERENCES games(id) ON DELETE CASCADE,
    price_paid   NUMERIC(10,2) NOT NULL DEFAULT 0.00,
    purchased_at TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, game_id)
);

CREATE INDEX idx_purchases_user_id ON purchases(user_id);

-- ============================================================
-- CART ITEMS
-- ============================================================
CREATE TABLE cart_items (
    id       BIGSERIAL PRIMARY KEY,
    user_id  BIGINT      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    game_id  BIGINT      NOT NULL REFERENCES games(id) ON DELETE CASCADE,
    added_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, game_id)
);

CREATE INDEX idx_cart_user_id ON cart_items(user_id);

-- ============================================================
-- DEMO ACCOUNTS
-- ============================================================
-- Passwords:
--   customer  → password is "customer",  PIN is 0000
--   publisher → password is "publisher", PIN is 0000
--
-- Hashes below are BCrypt cost-10 and compatible with
-- Spring Security BCryptPasswordEncoder ($2b$ = $2a$ compatible).
--
-- The Spring app also auto-seeds these on startup via
-- DataInitializer.java, so these INSERTs are a safety backup.
-- ============================================================

INSERT INTO users (email, password_hash, role, pin, avatar_id, display_name, active)
VALUES
  ('customer',
   '$2b$10$saLHyN46kIQLGxdN2Hks7.d.JII3TY.ikS.gplRAk4HuPkw6yH2SG',
   'customer', '0000', 'av1', 'Demo Customer', TRUE),

  ('publisher',
   '$2b$10$VjnXO3svVR.Kkee5/hT31u12ks5MtwgegduPEHZKOBgkbYz7jdQEW',
   'publisher', '0000', 'av1', 'Demo Publisher', TRUE)

ON CONFLICT (email) DO NOTHING;

-- ============================================================
-- Helpful queries for development
-- ============================================================

-- See all users:
-- SELECT id, email, role, active, created_at FROM users;

-- See all live games:
-- SELECT id, title, price, download_count, avg_rating FROM games WHERE status = 'live';

-- See purchases by user:
-- SELECT u.email, g.title, p.price_paid, p.purchased_at
--   FROM purchases p
--   JOIN users u ON u.id = p.user_id
--   JOIN games g ON g.id = p.game_id
--   ORDER BY p.purchased_at DESC;
