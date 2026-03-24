-- ============================================================
--  PC+ — Neon.tech Data Fix
--
--  Run this in the Neon SQL Editor:
--    console.neon.tech → SQL Editor → paste → Run
--
--  What this does:
--    1. Fixes the customer account (wrong password hash → correct BCrypt hash)
--    2. Inserts the publisher demo account if it's missing
--    3. Sets all required columns to their correct values
--
--  After running this, both demo accounts will work correctly
--  when the Spring Boot backend is connected and running.
--
--  Passwords:
--    customer  email: "customer"   password: "customer"   PIN: 0000
--    publisher email: "publisher"  password: "publisher"  PIN: 0000
-- ============================================================

-- ---- 1. Fix the customer account ----
-- The current row has password_hash = 'customer' (plain text).
-- Spring Security's BCryptPasswordEncoder cannot verify a plain-text hash.
-- This UPDATE replaces it with the correct BCrypt hash.

UPDATE users
SET
    password_hash = '$2b$10$R3ReUciAVMIq7kn9S6hQwOiI00KvgMW5KZ01wScd.67DhGIcNOCVe',
    avatar_id     = 'av1',
    display_name  = 'Demo Customer',
    active        = TRUE,
    updated_at    = NOW()
WHERE email = 'customer';

-- ---- 2. Insert publisher account if it doesn't exist ----
INSERT INTO users (email, password_hash, role, pin, avatar_id, display_name, active)
VALUES (
    'publisher',
    '$2b$10$Pko5NTR37f/3fG80NzsCq.mbZvvfusMsCYKQa3SrC2kPotehe2swy',
    'publisher',
    '0000',
    'av1',
    'Demo Publisher',
    TRUE
)
ON CONFLICT (email) DO UPDATE
    SET password_hash = '$2b$10$Pko5NTR37f/3fG80NzsCq.mbZvvfusMsCYKQa3SrC2kPotehe2swy',
        avatar_id     = 'av1',
        display_name  = 'Demo Publisher',
        active        = TRUE,
        updated_at    = NOW();

-- ---- 3. Verify both rows look correct ----
SELECT id, email, role, pin, avatar_id, display_name, active,
       LEFT(password_hash, 7) AS hash_prefix   -- should show '$2b$10$'
FROM users
WHERE email IN ('customer', 'publisher')
ORDER BY id;

-- ============================================================
-- Expected result:
--   id | email     | role      | pin  | avatar_id | display_name  | active | hash_prefix
--    1 | customer  | customer  | 0000 | av1       | Demo Customer | t      | $2b$10$
--    2 | publisher | publisher | 0000 | av1       | Demo Publisher| t      | $2b$10$
-- ============================================================
