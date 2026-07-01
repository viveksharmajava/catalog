-- Storefront visibility: when Y, catalog appears in eCart navigation and storefront catalog APIs.

ALTER TABLE prod_catalog ADD COLUMN is_cart_enabled CHAR(1) DEFAULT 'Y' NOT NULL;

UPDATE prod_catalog SET is_cart_enabled = 'Y' WHERE is_cart_enabled IS NULL;
