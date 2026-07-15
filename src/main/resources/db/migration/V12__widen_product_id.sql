-- Bulk import uses descriptive product IDs (e.g. PROD-YONEX-ASTROX-99-PRO) longer than the original 20-char limit.

ALTER TABLE product ALTER COLUMN product_id VARCHAR(64);
ALTER TABLE product_category_member ALTER COLUMN product_id VARCHAR(64);
ALTER TABLE product_assoc ALTER COLUMN product_id VARCHAR(64);
ALTER TABLE product_assoc ALTER COLUMN product_id_to VARCHAR(64);
ALTER TABLE good_identification ALTER COLUMN product_id VARCHAR(64);
ALTER TABLE product_attribute ALTER COLUMN product_id VARCHAR(64);
ALTER TABLE product_feature_appl ALTER COLUMN product_id VARCHAR(64);
ALTER TABLE product_keyword ALTER COLUMN product_id VARCHAR(64);
