-- Storefront page links become free-form page content (text areas in catalog-admin)

ALTER TABLE product_store_setting ALTER COLUMN contact_us_link SET DATA TYPE CLOB;
ALTER TABLE product_store_setting ALTER COLUMN about_us_link SET DATA TYPE CLOB;
ALTER TABLE product_store_setting ALTER COLUMN shipping_policy_link SET DATA TYPE CLOB;
ALTER TABLE product_store_setting ALTER COLUMN returns_link SET DATA TYPE CLOB;
ALTER TABLE product_store_setting ALTER COLUMN privacy_policy_link SET DATA TYPE CLOB;
ALTER TABLE product_store_setting ALTER COLUMN terms_and_conditions_link SET DATA TYPE CLOB;

ALTER TABLE product_store_setting RENAME COLUMN contact_us_link TO contact_us_content;
ALTER TABLE product_store_setting RENAME COLUMN about_us_link TO about_us_content;
ALTER TABLE product_store_setting RENAME COLUMN shipping_policy_link TO shipping_policy_content;
ALTER TABLE product_store_setting RENAME COLUMN returns_link TO returns_content;
ALTER TABLE product_store_setting RENAME COLUMN privacy_policy_link TO privacy_policy_content;
ALTER TABLE product_store_setting RENAME COLUMN terms_and_conditions_link TO terms_and_conditions_content;

-- Clear legacy URL paths seeded before content-based settings
UPDATE product_store_setting SET contact_us_content = NULL WHERE contact_us_content LIKE '/%';
UPDATE product_store_setting SET about_us_content = NULL WHERE about_us_content LIKE '/%';
UPDATE product_store_setting SET shipping_policy_content = NULL WHERE shipping_policy_content LIKE '/%';
UPDATE product_store_setting SET returns_content = NULL WHERE returns_content LIKE '/%';
UPDATE product_store_setting SET privacy_policy_content = NULL WHERE privacy_policy_content LIKE '/%';
UPDATE product_store_setting SET terms_and_conditions_content = NULL WHERE terms_and_conditions_content LIKE '/%';
