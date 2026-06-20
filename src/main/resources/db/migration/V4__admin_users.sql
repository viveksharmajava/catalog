-- Admin users for catalog-admin UI (dev credentials: admin / admin123)
-- {noop} prefix = plain text for local dev only; replace with bcrypt in production.

INSERT INTO users (username, password, role_id)
SELECT 'admin', '{noop}admin123', id FROM roles WHERE name = 'ADMIN';

INSERT INTO users (username, password, role_id)
SELECT 'catalog_mgr', '{noop}catalog123', id FROM roles WHERE name = 'CATALOG_MANAGER';

INSERT INTO users (username, password, role_id)
SELECT 'viewer', '{noop}viewer123', id FROM roles WHERE name = 'VIEWER';
