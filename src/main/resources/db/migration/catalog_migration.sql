USE playdb;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- Consolidated MySQL migration for catalog
-- Source migrations: V1__init.sql, V2__categories.sql, V3__enterprise_product_model.sql, V4__admin_users.sql, V5__prod_catalog.sql, V6__prod_catalog_category.sql, V7__product_store.sql, V8__prod_catalog_cart_enabled.sql, V9__product_store_setting.sql, V10__product_store_setting_backfill.sql, V11__product_store_setting_content.sql, V12__widen_product_id.sql, V13__seed_storefront_page_content.sql, V14__product_store_payment_method.sql, V15__product_store_shipping_method.sql, V16__product_store_variant.sql, V17__product_variant_option.sql
-- Converted for Google Cloud SQL MySQL 8

-- ========== V1__init.sql ==========

-- Initial schema for playpro catalog (compatible with Postgres and H2)

CREATE TABLE IF NOT EXISTS roles (
    id bigint AUTO_INCREMENT PRIMARY KEY,
    name varchar(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
    id bigint AUTO_INCREMENT PRIMARY KEY,
    username varchar(100) NOT NULL UNIQUE,
    password varchar(255),
    role_id bigint,
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE IF NOT EXISTS products (
    id bigint AUTO_INCREMENT PRIMARY KEY,
    sku varchar(100) NOT NULL UNIQUE,
    name varchar(255) NOT NULL,
    description text,
    status varchar(50) DEFAULT 'DRAFT',
    version bigint DEFAULT 1,
    created_by varchar(100),
    created_at timestamp DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp DEFAULT CURRENT_TIMESTAMP
);

-- Seed roles
INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('MERCHANDISER');
INSERT INTO roles (name) VALUES ('CATALOG_MANAGER');
INSERT INTO roles (name) VALUES ('VIEWER');

-- Sample product
INSERT INTO products (sku, name, description, status, created_by) VALUES ('SKU-001', 'Sample Product 1', 'This is a seeded sample product', 'ACTIVE', 'system');


-- ========== V2__categories.sql ==========

-- Categories and product-category mapping

CREATE TABLE IF NOT EXISTS categories (
    id bigint AUTO_INCREMENT PRIMARY KEY,
    name varchar(255) NOT NULL,
    slug varchar(255) UNIQUE,
    description text,
    parent_id bigint,
    active boolean DEFAULT true,
    created_by varchar(100),
    created_at timestamp DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES categories(id)
);

CREATE TABLE IF NOT EXISTS product_category_map (
    id bigint AUTO_INCREMENT PRIMARY KEY,
    product_id bigint NOT NULL,
    category_id bigint NOT NULL,
    CONSTRAINT fk_pcm_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_pcm_category FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT uc_product_category UNIQUE (product_id, category_id)
);


-- ========== V3__enterprise_product_model.sql ==========

-- Enterprise product catalog schema (OFBiz product-entitymodel inspired)
-- Product-level entities only; pricing/inventory/promo belong in other services.

DROP TABLE IF EXISTS product_category_map;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS products;

-- Reference / type tables
CREATE TABLE IF NOT EXISTS product_type (
    product_type_id       VARCHAR(20)  NOT NULL PRIMARY KEY,
    parent_type_id        VARCHAR(20),
    is_physical           CHAR(1)      DEFAULT 'Y',
    is_digital            CHAR(1)      DEFAULT 'N',
    description           VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS product_category_type (
    product_category_type_id VARCHAR(20) NOT NULL PRIMARY KEY,
    parent_type_id           VARCHAR(20),
    description              VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS product_assoc_type (
    product_assoc_type_id VARCHAR(20) NOT NULL PRIMARY KEY,
    parent_type_id        VARCHAR(20),
    description           VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS good_identification_type (
    good_identification_type_id VARCHAR(20) NOT NULL PRIMARY KEY,
    parent_type_id              VARCHAR(20),
    description                 VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS product_feature_type (
    product_feature_type_id VARCHAR(20) NOT NULL PRIMARY KEY,
    parent_type_id          VARCHAR(20),
    description             VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS product_feature_appl_type (
    product_feature_appl_type_id VARCHAR(20) NOT NULL PRIMARY KEY,
    parent_type_id               VARCHAR(20),
    description                  VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS product_feature_category (
    product_feature_category_id VARCHAR(20) NOT NULL PRIMARY KEY,
    parent_category_id          VARCHAR(20),
    description                 VARCHAR(255)
);

-- Category hierarchy (OFBiz ProductCategory + ProductCategoryRollup)
CREATE TABLE IF NOT EXISTS product_category (
    product_category_id        VARCHAR(20)  NOT NULL PRIMARY KEY,
    product_category_type_id   VARCHAR(20)  NOT NULL,
    primary_parent_category_id VARCHAR(20),
    category_name              VARCHAR(100) NOT NULL,
    description                VARCHAR(255),
    long_description           LONGTEXT,
    category_image_url         VARCHAR(2000),
    show_in_select             CHAR(1)      DEFAULT 'Y',
    created_by_user_login      VARCHAR(250),
    created_date               TIMESTAMP,
    last_modified_by_user_login VARCHAR(250),
    last_modified_date         TIMESTAMP,
    CONSTRAINT fk_pcat_type FOREIGN KEY (product_category_type_id) REFERENCES product_category_type(product_category_type_id)
);

CREATE TABLE IF NOT EXISTS product_category_rollup (
    product_category_id        VARCHAR(20) NOT NULL,
    parent_product_category_id VARCHAR(20) NOT NULL,
    from_date                  TIMESTAMP   NOT NULL,
    thru_date                  TIMESTAMP,
    sequence_num               DECIMAL(20,0),
    PRIMARY KEY (product_category_id, parent_product_category_id, from_date),
    CONSTRAINT fk_pcr_child FOREIGN KEY (product_category_id) REFERENCES product_category(product_category_id),
    CONSTRAINT fk_pcr_parent FOREIGN KEY (parent_product_category_id) REFERENCES product_category(product_category_id)
);

CREATE INDEX idx_pcat_rollup_parent ON product_category_rollup(parent_product_category_id);

-- Core product (OFBiz Product entity, catalog-relevant fields)
CREATE TABLE IF NOT EXISTS product (
    product_id                   VARCHAR(20)  NOT NULL PRIMARY KEY,
    product_type_id              VARCHAR(20)  NOT NULL,
    primary_product_category_id    VARCHAR(20),
    status_id                    VARCHAR(20)  DEFAULT 'ACTIVE',
    internal_name                VARCHAR(255),
    brand_name                   VARCHAR(100),
    product_name                 VARCHAR(100) NOT NULL,
    description                  VARCHAR(255),
    long_description             LONGTEXT,
    comments                     LONGTEXT,
    small_image_url              VARCHAR(2000),
    medium_image_url             VARCHAR(2000),
    large_image_url              VARCHAR(2000),
    detail_image_url             VARCHAR(2000),
    introduction_date            TIMESTAMP,
    release_date                 TIMESTAMP,
    sales_discontinuation_date   TIMESTAMP,
    is_virtual                   CHAR(1)      DEFAULT 'N',
    is_variant                   CHAR(1)      DEFAULT 'N',
    returnable                   CHAR(1)      DEFAULT 'Y',
    taxable                      CHAR(1)      DEFAULT 'Y',
    charge_shipping              CHAR(1)      DEFAULT 'Y',
    require_inventory            CHAR(1)      DEFAULT 'Y',
    shipping_weight              DECIMAL(18,6),
    product_weight               DECIMAL(18,6),
    product_height               DECIMAL(18,6),
    product_width                DECIMAL(18,6),
    product_depth                DECIMAL(18,6),
    created_by_user_login        VARCHAR(250),
    created_date                 TIMESTAMP,
    last_modified_by_user_login  VARCHAR(250),
    last_modified_date           TIMESTAMP,
    version                      BIGINT       DEFAULT 0,
    CONSTRAINT fk_prod_type FOREIGN KEY (product_type_id) REFERENCES product_type(product_type_id),
    CONSTRAINT fk_prod_primary_cat FOREIGN KEY (primary_product_category_id) REFERENCES product_category(product_category_id)
);

CREATE INDEX idx_product_type ON product(product_type_id);
CREATE INDEX idx_product_status ON product(status_id);
CREATE INDEX idx_product_primary_cat ON product(primary_product_category_id);

CREATE TABLE IF NOT EXISTS product_category_member (
    product_category_id VARCHAR(20) NOT NULL,
    product_id          VARCHAR(20) NOT NULL,
    from_date           TIMESTAMP   NOT NULL,
    thru_date           TIMESTAMP,
    comments            LONGTEXT,
    sequence_num        DECIMAL(20,0),
    quantity            DECIMAL(18,6),
    PRIMARY KEY (product_category_id, product_id, from_date),
    CONSTRAINT fk_pcm_cat FOREIGN KEY (product_category_id) REFERENCES product_category(product_category_id),
    CONSTRAINT fk_pcm_prod FOREIGN KEY (product_id) REFERENCES product(product_id)
);

CREATE INDEX idx_pcm_product ON product_category_member(product_id);
CREATE INDEX idx_pcm_category ON product_category_member(product_category_id);

CREATE TABLE IF NOT EXISTS product_assoc (
    product_id            VARCHAR(20) NOT NULL,
    product_id_to         VARCHAR(20) NOT NULL,
    product_assoc_type_id VARCHAR(20) NOT NULL,
    from_date             TIMESTAMP   NOT NULL,
    thru_date             TIMESTAMP,
    sequence_num          DECIMAL(20,0),
    quantity              DECIMAL(18,6),
    reason                VARCHAR(255),
    PRIMARY KEY (product_id, product_id_to, product_assoc_type_id, from_date),
    CONSTRAINT fk_passoc_type FOREIGN KEY (product_assoc_type_id) REFERENCES product_assoc_type(product_assoc_type_id),
    CONSTRAINT fk_passoc_main FOREIGN KEY (product_id) REFERENCES product(product_id),
    CONSTRAINT fk_passoc_assoc FOREIGN KEY (product_id_to) REFERENCES product(product_id)
);

CREATE INDEX idx_passoc_to ON product_assoc(product_id_to);

CREATE TABLE IF NOT EXISTS good_identification (
    good_identification_type_id VARCHAR(20) NOT NULL,
    product_id                  VARCHAR(20) NOT NULL,
    id_value                    VARCHAR(255) NOT NULL,
    PRIMARY KEY (good_identification_type_id, product_id),
    CONSTRAINT fk_gid_type FOREIGN KEY (good_identification_type_id) REFERENCES good_identification_type(good_identification_type_id),
    CONSTRAINT fk_gid_prod FOREIGN KEY (product_id) REFERENCES product(product_id)
);

CREATE INDEX idx_gid_value ON good_identification(id_value);

CREATE TABLE IF NOT EXISTS product_attribute (
    product_id       VARCHAR(20) NOT NULL,
    attr_name        VARCHAR(60) NOT NULL,
    attr_value       VARCHAR(255),
    attr_type        VARCHAR(255),
    attr_description VARCHAR(255),
    PRIMARY KEY (product_id, attr_name),
    CONSTRAINT fk_pattr_prod FOREIGN KEY (product_id) REFERENCES product(product_id)
);

CREATE TABLE IF NOT EXISTS product_feature (
    product_feature_id          VARCHAR(20) NOT NULL PRIMARY KEY,
    product_feature_type_id     VARCHAR(20) NOT NULL,
    product_feature_category_id VARCHAR(20),
    description                 VARCHAR(255),
    abbrev                      VARCHAR(20),
    id_code                     VARCHAR(60),
    default_sequence_num        DECIMAL(20,0),
    CONSTRAINT fk_pfeat_type FOREIGN KEY (product_feature_type_id) REFERENCES product_feature_type(product_feature_type_id),
    CONSTRAINT fk_pfeat_cat FOREIGN KEY (product_feature_category_id) REFERENCES product_feature_category(product_feature_category_id)
);

CREATE TABLE IF NOT EXISTS product_feature_appl (
    product_id                   VARCHAR(20) NOT NULL,
    product_feature_id           VARCHAR(20) NOT NULL,
    product_feature_appl_type_id VARCHAR(20) NOT NULL,
    from_date                    TIMESTAMP   NOT NULL,
    thru_date                    TIMESTAMP,
    sequence_num                 DECIMAL(20,0),
    amount                       DECIMAL(18,2),
    PRIMARY KEY (product_id, product_feature_id, from_date),
    CONSTRAINT fk_pfappl_prod FOREIGN KEY (product_id) REFERENCES product(product_id),
    CONSTRAINT fk_pfappl_feat FOREIGN KEY (product_feature_id) REFERENCES product_feature(product_feature_id),
    CONSTRAINT fk_pfappl_type FOREIGN KEY (product_feature_appl_type_id) REFERENCES product_feature_appl_type(product_feature_appl_type_id)
);

CREATE TABLE IF NOT EXISTS product_keyword (
    product_id       VARCHAR(20) NOT NULL,
    keyword          VARCHAR(60) NOT NULL,
    keyword_type_id  VARCHAR(20) NOT NULL DEFAULT 'KWT_TAG',
    relevancy_weight DECIMAL(20,0) DEFAULT 5,
    status_id        VARCHAR(20) DEFAULT 'KW_APPROVED',
    PRIMARY KEY (product_id, keyword, keyword_type_id),
    CONSTRAINT fk_pkwd_prod FOREIGN KEY (product_id) REFERENCES product(product_id)
);

CREATE INDEX idx_pkwd_keyword ON product_keyword(keyword);

-- Seed reference data
INSERT INTO product_type (product_type_id, description, is_physical, is_digital) VALUES
    ('FINISHED_GOOD', 'Finished Good', 'Y', 'N'),
    ('DIGITAL_GOOD', 'Digital Good', 'N', 'Y'),
    ('SERVICE', 'Service', 'N', 'N'),
    ('MARKETING_PKG', 'Marketing Package', 'Y', 'N'),
    ('FINDIG_GOOD', 'Raw Material', 'Y', 'N');

INSERT INTO product_category_type (product_category_type_id, description) VALUES
    ('CATALOG_CATEGORY', 'Catalog Browse Category'),
    ('SEARCH_CATEGORY', 'Search Category'),
    ('INTERNAL_CATEGORY', 'Internal Merchandising Category');

INSERT INTO product_assoc_type (product_assoc_type_id, description) VALUES
    ('PRODUCT_VARIANT', 'Product Variant'),
    ('PRODUCT_ACCESSORY', 'Product Accessory'),
    ('ALTERNATE_PRODUCT', 'Alternate Product'),
    ('PRODUCT_COMPONENT', 'Product Component');

INSERT INTO good_identification_type (good_identification_type_id, description) VALUES
    ('SKU', 'Stock Keeping Unit'),
    ('UPC', 'Universal Product Code'),
    ('EAN', 'European Article Number'),
    ('ISBN', 'International Standard Book Number');

INSERT INTO product_feature_type (product_feature_type_id, description) VALUES
    ('SIZE', 'Size Feature'),
    ('COLOR', 'Color Feature'),
    ('STYLE', 'Style Feature'),
    ('MATERIAL', 'Material Feature');

INSERT INTO product_feature_appl_type (product_feature_appl_type_id, description) VALUES
    ('STANDARD_FEATURE', 'Standard Feature'),
    ('SELECTABLE_FEATURE', 'Selectable Feature'),
    ('REQUIRED_FEATURE', 'Required Feature'),
    ('DISTINGUISHING_FEAT', 'Distinguishing Feature');

INSERT INTO product_feature_category (product_feature_category_id, description) VALUES
    ('APPAREL', 'Apparel Features'),
    ('ELECTRONICS', 'Electronics Features'),
    ('GENERAL', 'General Features');

INSERT INTO product_category (product_category_id, product_category_type_id, category_name, description, created_by_user_login, created_date)
    VALUES ('CAT-ROOT', 'CATALOG_CATEGORY', 'Root Catalog', 'Root catalog category', 'system', CURRENT_TIMESTAMP);

INSERT INTO product_category (product_category_id, product_category_type_id, primary_parent_category_id, category_name, description, created_by_user_login, created_date)
    VALUES ('CAT-ELECTRONICS', 'CATALOG_CATEGORY', 'CAT-ROOT', 'Electronics', 'Electronics department', 'system', CURRENT_TIMESTAMP);

INSERT INTO product_category_rollup (product_category_id, parent_product_category_id, from_date, sequence_num)
    VALUES ('CAT-ELECTRONICS', 'CAT-ROOT', CURRENT_TIMESTAMP, 1);

INSERT INTO product (product_id, product_type_id, primary_product_category_id, status_id, internal_name, product_name, description, is_virtual, is_variant, created_by_user_login, created_date)
    VALUES ('PROD-001', 'FINISHED_GOOD', 'CAT-ELECTRONICS', 'ACTIVE', 'Sample Product 1', 'Sample Product 1', 'Seeded enterprise sample product', 'N', 'N', 'system', CURRENT_TIMESTAMP);

INSERT INTO good_identification (good_identification_type_id, product_id, id_value)
    VALUES ('SKU', 'PROD-001', 'SKU-001');

INSERT INTO product_category_member (product_category_id, product_id, from_date, sequence_num)
    VALUES ('CAT-ELECTRONICS', 'PROD-001', CURRENT_TIMESTAMP, 1);

INSERT INTO product_keyword (product_id, keyword, keyword_type_id, relevancy_weight)
    VALUES ('PROD-001', 'sample', 'KWT_TAG', 10);


-- ========== V4__admin_users.sql ==========

-- Admin users for catalog-admin UI (dev credentials: admin / admin123)
-- {noop} prefix = plain text for local dev only; replace with bcrypt in production.

INSERT INTO users (username, password, role_id)
SELECT 'admin', '{noop}admin123', id FROM roles WHERE name = 'ADMIN';

INSERT INTO users (username, password, role_id)
SELECT 'catalog_mgr', '{noop}catalog123', id FROM roles WHERE name = 'CATALOG_MANAGER';

INSERT INTO users (username, password, role_id)
SELECT 'viewer', '{noop}viewer123', id FROM roles WHERE name = 'VIEWER';


-- ========== V5__prod_catalog.sql ==========

-- OFBiz ProdCatalog entity (product-entitymodel.xml)

CREATE TABLE IF NOT EXISTS prod_catalog (
    prod_catalog_id          VARCHAR(20)  NOT NULL PRIMARY KEY,
    catalog_name             VARCHAR(100) NOT NULL,
    use_quick_add            CHAR(1)      DEFAULT 'Y',
    style_sheet              VARCHAR(250),
    header_logo              VARCHAR(250),
    content_path_prefix      VARCHAR(255),
    template_path_prefix     VARCHAR(255),
    view_allow_perm_reqd     CHAR(1)      DEFAULT 'N',
    purchase_allow_perm_reqd CHAR(1)      DEFAULT 'N'
);

INSERT INTO prod_catalog (prod_catalog_id, catalog_name, use_quick_add, view_allow_perm_reqd, purchase_allow_perm_reqd)
VALUES ('DEMO_CATALOG', 'Demo Store Catalog', 'Y', 'N', 'N');

INSERT INTO prod_catalog (prod_catalog_id, catalog_name, use_quick_add, view_allow_perm_reqd, purchase_allow_perm_reqd)
VALUES ('WHOLESALE', 'Wholesale Catalog', 'N', 'N', 'N');


-- ========== V6__prod_catalog_category.sql ==========

-- OFBiz ProdCatalogCategory + ProdCatalogCategoryType

CREATE TABLE IF NOT EXISTS prod_catalog_category_type (
    prod_catalog_category_type_id VARCHAR(20) NOT NULL PRIMARY KEY,
    parent_type_id                VARCHAR(20),
    description                   VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS prod_catalog_category (
    prod_catalog_id               VARCHAR(20) NOT NULL,
    product_category_id           VARCHAR(20) NOT NULL,
    prod_catalog_category_type_id VARCHAR(20) NOT NULL,
    from_date                     TIMESTAMP   NOT NULL,
    thru_date                     TIMESTAMP,
    sequence_num                  DECIMAL(20, 0),
    PRIMARY KEY (prod_catalog_id, product_category_id, prod_catalog_category_type_id, from_date),
    CONSTRAINT fk_pcc_catalog FOREIGN KEY (prod_catalog_id) REFERENCES prod_catalog(prod_catalog_id),
    CONSTRAINT fk_pcc_category FOREIGN KEY (product_category_id) REFERENCES product_category(product_category_id),
    CONSTRAINT fk_pcc_type FOREIGN KEY (prod_catalog_category_type_id) REFERENCES prod_catalog_category_type(prod_catalog_category_type_id)
);

INSERT INTO prod_catalog_category_type (prod_catalog_category_type_id, description) VALUES
    ('PCCT_BROWSE_ROOT', 'Browse Root (One)'),
    ('PCCT_SEARCH', 'Default Search (One)'),
    ('PCCT_OTHER_SEARCH', 'Other Search (Many)'),
    ('PCCT_QUICK_ADD', 'Quick Add (Many)'),
    ('PCCT_PROMOTIONS', 'Promotions (One)'),
    ('PCCT_MOST_POPULAR', 'Most Popular (One)'),
    ('PCCT_WHATS_NEW', 'What''s New (One)');


-- ========== V7__product_store.sql ==========

-- OFBiz ProductStore and ProductStoreCatalog (product-entitymodel.xml)

CREATE TABLE IF NOT EXISTS product_store (
    product_store_id              VARCHAR(20)  NOT NULL PRIMARY KEY,
    primary_store_group_id        VARCHAR(20),
    store_name                    VARCHAR(100) NOT NULL,
    company_name                  VARCHAR(100),
    title                         VARCHAR(100),
    subtitle                      VARCHAR(255),
    pay_to_party_id               VARCHAR(20),
    inventory_facility_id         VARCHAR(20),
    one_inventory_facility        CHAR(1)      DEFAULT 'N',
    check_inventory               CHAR(1)      DEFAULT 'Y',
    reserve_inventory             CHAR(1)      DEFAULT 'Y',
    require_inventory             CHAR(1)      DEFAULT 'N',
    show_out_of_stock_products    CHAR(1)      DEFAULT 'Y',
    is_immediately_fulfilled      CHAR(1)      DEFAULT 'N',
    is_demo_store                 CHAR(1)      DEFAULT 'N',
    default_locale_string         VARCHAR(10),
    default_currency_uom_id         VARCHAR(20),
    default_time_zone_string      VARCHAR(255),
    default_sales_channel_enum_id VARCHAR(20),
    order_number_prefix           VARCHAR(60),
    visual_theme_id               VARCHAR(20),
    manual_auth_is_capture        CHAR(1)      DEFAULT 'N',
    prorate_shipping              CHAR(1)      DEFAULT 'Y',
    prorate_taxes                 CHAR(1)      DEFAULT 'Y',
    view_cart_on_add              CHAR(1)      DEFAULT 'N',
    auto_save_cart                CHAR(1)      DEFAULT 'N',
    auto_approve_order            CHAR(1)      DEFAULT 'Y',
    auto_approve_invoice          CHAR(1)      DEFAULT 'Y',
    days_to_cancel_non_pay        DECIMAL(20, 0),
    retry_failed_auths            CHAR(1)      DEFAULT 'Y',
    allow_password                CHAR(1)      DEFAULT 'Y',
    default_password              VARCHAR(255),
    prod_search_exclude_variants  CHAR(1)      DEFAULT 'Y',
    show_prices_with_vat_tax      CHAR(1)      DEFAULT 'N'
);

CREATE TABLE IF NOT EXISTS product_store_catalog (
    product_store_id VARCHAR(20) NOT NULL,
    prod_catalog_id  VARCHAR(20) NOT NULL,
    from_date        TIMESTAMP   NOT NULL,
    thru_date        TIMESTAMP,
    sequence_num     DECIMAL(20, 0),
    PRIMARY KEY (product_store_id, prod_catalog_id, from_date),
    CONSTRAINT fk_psc_store FOREIGN KEY (product_store_id) REFERENCES product_store (product_store_id),
    CONSTRAINT fk_psc_catalog FOREIGN KEY (prod_catalog_id) REFERENCES prod_catalog (prod_catalog_id)
);

INSERT INTO product_store (
    product_store_id, store_name, title, subtitle, company_name,
    default_currency_uom_id, check_inventory, reserve_inventory, auto_approve_order
) VALUES (
    'OFBIZ_STORE', 'Demo Product Store', 'Demo Store', 'Your online demo store', 'Demo Company',
    'USD', 'Y', 'Y', 'Y'
);

INSERT INTO product_store_catalog (product_store_id, prod_catalog_id, from_date, sequence_num)
VALUES ('OFBIZ_STORE', 'DEMO_CATALOG', CURRENT_TIMESTAMP, 1);


-- ========== V8__prod_catalog_cart_enabled.sql ==========

-- Storefront visibility: when Y, catalog appears in eCart navigation and storefront catalog APIs.

ALTER TABLE prod_catalog ADD COLUMN is_cart_enabled CHAR(1) DEFAULT 'Y' NOT NULL;

UPDATE prod_catalog SET is_cart_enabled = 'Y' WHERE is_cart_enabled IS NULL;


-- ========== V9__product_store_setting.sql ==========

-- Store-level customer-facing page links and default storefront flag

CREATE TABLE IF NOT EXISTS product_store_setting (
    product_store_id            VARCHAR(20)   NOT NULL PRIMARY KEY,
    is_default_store            CHAR(1)       NOT NULL DEFAULT 'N',
    contact_us_link             VARCHAR(500),
    about_us_link               VARCHAR(500),
    shipping_policy_link        VARCHAR(500),
    returns_link                VARCHAR(500),
    privacy_policy_link         VARCHAR(500),
    terms_and_conditions_link   VARCHAR(500),
    last_modified_date          TIMESTAMP,
    CONSTRAINT fk_product_store_setting_store FOREIGN KEY (product_store_id) REFERENCES product_store (product_store_id),
    CONSTRAINT chk_product_store_setting_default CHECK (is_default_store IN ('Y', 'N'))
);

INSERT INTO product_store_setting (
    product_store_id,
    is_default_store,
    contact_us_link,
    about_us_link,
    shipping_policy_link,
    returns_link,
    privacy_policy_link,
    terms_and_conditions_link,
    last_modified_date
) VALUES (
    'OFBIZ_STORE',
    'Y',
    '/contact',
    '/pages/about-us',
    '/pages/shipping-policy',
    '/pages/refund-policy',
    '/pages/privacy-policy',
    '/pages/terms-and-conditions',
    CURRENT_TIMESTAMP
);


-- ========== V10__product_store_setting_backfill.sql ==========

-- Backfill storefront settings for product stores created before product_store_setting existed

INSERT INTO product_store_setting (
    product_store_id,
    is_default_store,
    contact_us_link,
    about_us_link,
    shipping_policy_link,
    returns_link,
    privacy_policy_link,
    terms_and_conditions_link,
    last_modified_date
)
SELECT
    ps.product_store_id,
    'N',
    '/contact',
    '/pages/about-us',
    '/pages/shipping-policy',
    '/pages/refund-policy',
    '/pages/privacy-policy',
    '/pages/terms-and-conditions',
    CURRENT_TIMESTAMP
FROM product_store ps
LEFT JOIN product_store_setting pss ON ps.product_store_id = pss.product_store_id
WHERE pss.product_store_id IS NULL;


-- ========== V11__product_store_setting_content.sql ==========

-- Storefront page links become free-form page content (text areas in catalog-admin)

ALTER TABLE product_store_setting MODIFY COLUMN contact_us_link LONGTEXT;
ALTER TABLE product_store_setting MODIFY COLUMN about_us_link LONGTEXT;
ALTER TABLE product_store_setting MODIFY COLUMN shipping_policy_link LONGTEXT;
ALTER TABLE product_store_setting MODIFY COLUMN returns_link LONGTEXT;
ALTER TABLE product_store_setting MODIFY COLUMN privacy_policy_link LONGTEXT;
ALTER TABLE product_store_setting MODIFY COLUMN terms_and_conditions_link LONGTEXT;

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


-- ========== V12__widen_product_id.sql ==========

-- Bulk import uses descriptive product IDs (e.g. PROD-YONEX-ASTROX-99-PRO) longer than the original 20-char limit.

ALTER TABLE product MODIFY COLUMN product_id VARCHAR(64);
ALTER TABLE product_category_member MODIFY COLUMN product_id VARCHAR(64);
ALTER TABLE product_assoc MODIFY COLUMN product_id VARCHAR(64);
ALTER TABLE product_assoc MODIFY COLUMN product_id_to VARCHAR(64);
ALTER TABLE good_identification MODIFY COLUMN product_id VARCHAR(64);
ALTER TABLE product_attribute MODIFY COLUMN product_id VARCHAR(64);
ALTER TABLE product_feature_appl MODIFY COLUMN product_id VARCHAR(64);
ALTER TABLE product_keyword MODIFY COLUMN product_id VARCHAR(64);


-- ========== V13__seed_storefront_page_content.sql ==========

-- Seed customer-facing storefront page content for the default product store (OFBIZ_STORE).
-- Content is served to ecart via GET /catalog/storefront/settings and shown on footer pages.

UPDATE product_store_setting SET
    contact_us_content =
'Contact Us

We would love to hear from you. Reach the PlayPro support team using the details below.

Email: playprosportz@gmail.com
Phone: 8431776905

Support hours: Mondayâ€“Saturday, 10:00 AM â€“ 6:00 PM IST

You can also use the contact form on this page to send us a message. We typically respond within 1â€“2 business days.',

    about_us_content =
'About Us

PlayPro delivers performance sports gear to athletes at every level. From weekend players to competitive athletes, we source authentic equipment from leading brands so you can focus on your game.

Our Mission
We make high-quality sports products easy to discover, buy, and receive â€” with transparent pricing and reliable delivery across India.

Why PlayPro
â€¢ 100% authentic products
â€¢ Competitive pricing
â€¢ Fast and dependable shipping
â€¢ Dedicated customer support

Built by sports enthusiasts, for sports enthusiasts.',

    shipping_policy_content =
'Shipping Policy

Delivery timelines
â€¢ Standard delivery: 3â€“7 business days after dispatch
â€¢ Express delivery: available in select cities (1â€“3 business days)

Shipping charges
â€¢ Free standard shipping on eligible orders over â‚¹999
â€¢ Express shipping charges (if selected) are shown at checkout

Order processing
Orders are typically processed within 1â€“2 business days. You will receive updates once your order is packed and handed over to the courier.

Delivery notes
â€¢ Please ensure your shipping address and phone number are accurate
â€¢ Someone should be available to receive the package
â€¢ Delivery timelines may vary for remote locations or peak seasons

For shipping questions, contact us at playprosportz@gmail.com or 8431776905.',

    returns_content =
'Return & Replacement Policy

Important: Sports goods sold on PlayPro are not returnable.

You may raise a replacement request only under the following conditions:
1. Color you don''t like
2. Item delivered in damaged condition

Request window
Replacement requests must be submitted within 7 days of order delivery.

How to raise a replacement request
1. Go to My Account â†’ Orders and open the delivered order
2. Select the item and choose Replacement
3. Share clear photos (for damaged items) and a short description of the issue
4. Our support team will review and confirm next steps

Please note
â€¢ Products must be unused and in original packaging wherever applicable
â€¢ Replacement is subject to stock availability for the same or equivalent item
â€¢ Requests submitted after 7 days of delivery will not be accepted

Need help? Email playprosportz@gmail.com or call 8431776905.',

    privacy_policy_content =
'Privacy Policy

PlayPro ("we", "us") respects your privacy and is committed to protecting your personal information.

Information we collect
We collect information you provide during registration, checkout, and support interactions. This may include your name, email address, phone number, shipping address, and order history.

How we use your information
â€¢ To process and deliver your orders
â€¢ To provide customer support
â€¢ To improve our website and services
â€¢ To send order updates and important account communications

We do not sell your personal information to third parties.

Data security
We use reasonable technical and organizational measures to protect your data. Access to personal information is limited to authorized personnel who need it to fulfill their duties.

Your choices
You may request updates to your account details or contact us for privacy-related questions at playprosportz@gmail.com.

Contact
Email: playprosportz@gmail.com
Phone: 8431776905',

    terms_and_conditions_content =
'Terms & Conditions

By using the PlayPro website and placing an order, you agree to these Terms & Conditions.

Eligibility
You must be 18 years or older (or have guardian consent) to make purchases on PlayPro.

Products and pricing
â€¢ Product availability and pricing are subject to change without prior notice
â€¢ We strive to display accurate product information; minor variations may occur
â€¢ All prices are in INR unless otherwise stated

Orders
â€¢ Placing an order constitutes an offer to purchase
â€¢ We reserve the right to cancel orders in case of pricing errors, stock unavailability, or suspected fraud
â€¢ An order confirmation email/SMS does not guarantee fulfillment until the order is dispatched

Shipping and delivery
Delivery timelines and charges are described in our Shipping Policy.

Returns and replacements
Sports goods are not returnable. Replacement requests are allowed only as described in our Return & Replacement Policy, and must be submitted within 7 days of delivery.

Limitation of liability
To the maximum extent permitted by law, PlayPro is not liable for indirect or consequential damages arising from use of the website or purchase of products.

Contact
For questions about these terms, contact playprosportz@gmail.com or 8431776905.',

    last_modified_date = CURRENT_TIMESTAMP
WHERE product_store_id = 'OFBIZ_STORE';


-- ========== V14__product_store_payment_method.sql ==========

-- Configurable payment methods / gateways per product store

CREATE TABLE IF NOT EXISTS product_store_payment_method (
    payment_method_id   VARCHAR(40)   NOT NULL PRIMARY KEY,
    product_store_id    VARCHAR(20)   NOT NULL,
    payment_type        VARCHAR(40)   NOT NULL,
    display_name        VARCHAR(100)  NOT NULL,
    enabled             CHAR(1)       NOT NULL DEFAULT 'N',
    sequence_num        INT           NOT NULL DEFAULT 0,
    gateway_provider    VARCHAR(60),
    api_key             VARCHAR(500),
    api_secret          VARCHAR(500),
    merchant_id         VARCHAR(200),
    gateway_url         VARCHAR(500),
    redirect_url        VARCHAR(500),
    webhook_url         VARCHAR(500),
    access_token        VARCHAR(1000),
    publishable_key     VARCHAR(500),
    extra_config        LONGTEXT,
    created_date        TIMESTAMP,
    last_modified_date  TIMESTAMP,
    CONSTRAINT fk_pspm_store FOREIGN KEY (product_store_id) REFERENCES product_store (product_store_id),
    CONSTRAINT chk_pspm_enabled CHECK (enabled IN ('Y', 'N')),
    CONSTRAINT chk_pspm_type CHECK (payment_type IN ('COD', 'CARD', 'UPI', 'NET_BANKING', 'WALLET', 'CUSTOM'))
);

CREATE INDEX idx_pspm_store ON product_store_payment_method (product_store_id);
CREATE INDEX idx_pspm_store_enabled ON product_store_payment_method (product_store_id, enabled);

-- Default COD for seeded store so storefront checkout keeps working
INSERT INTO product_store_payment_method (
    payment_method_id, product_store_id, payment_type, display_name, enabled, sequence_num,
    gateway_provider, created_date, last_modified_date
) VALUES (
    'PSM-COD-DEFAULT', 'OFBIZ_STORE', 'COD', 'Cash on Delivery', 'Y', 10,
    NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);


-- ========== V15__product_store_shipping_method.sql ==========

-- Configurable shipping / carrier integrations per product store

CREATE TABLE IF NOT EXISTS product_store_shipping_method (
    shipping_method_id  VARCHAR(40)   NOT NULL PRIMARY KEY,
    product_store_id    VARCHAR(20)   NOT NULL,
    shipping_type       VARCHAR(40)   NOT NULL,
    display_name        VARCHAR(100)  NOT NULL,
    enabled             CHAR(1)       NOT NULL DEFAULT 'N',
    sequence_num        INT           NOT NULL DEFAULT 0,
    carrier_provider    VARCHAR(60),
    api_key             VARCHAR(500),
    api_secret          VARCHAR(500),
    account_id          VARCHAR(200),
    api_base_url        VARCHAR(500),
    track_order_url     VARCHAR(500),
    create_shipment_url VARCHAR(500),
    webhook_url         VARCHAR(500),
    access_token        VARCHAR(1000),
    default_service_code VARCHAR(100),
    flat_rate_amount    DECIMAL(18, 2),
    extra_config        LONGTEXT,
    created_date        TIMESTAMP,
    last_modified_date  TIMESTAMP,
    CONSTRAINT fk_pssm_store FOREIGN KEY (product_store_id) REFERENCES product_store (product_store_id),
    CONSTRAINT chk_pssm_enabled CHECK (enabled IN ('Y', 'N')),
    CONSTRAINT chk_pssm_type CHECK (shipping_type IN (
        'FLAT_RATE', 'FREE_SHIPPING', 'CARRIER', 'PICKUP', 'CUSTOM'
    ))
);

CREATE INDEX idx_pssm_store ON product_store_shipping_method (product_store_id);
CREATE INDEX idx_pssm_store_enabled ON product_store_shipping_method (product_store_id, enabled);

-- Default flat-rate option for seeded store
INSERT INTO product_store_shipping_method (
    shipping_method_id, product_store_id, shipping_type, display_name, enabled, sequence_num,
    carrier_provider, flat_rate_amount, created_date, last_modified_date
) VALUES (
    'SSM-FLAT-DEFAULT', 'OFBIZ_STORE', 'FLAT_RATE', 'Standard Shipping', 'Y', 10,
    NULL, 50.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);


-- ========== V16__product_store_variant.sql ==========

-- Store-scoped product variant types (e.g. Shoe Size, Color) and their values

CREATE TABLE IF NOT EXISTS product_store_variant_type (
    variant_type_id     VARCHAR(40)   NOT NULL PRIMARY KEY,
    product_store_id    VARCHAR(20)   NOT NULL,
    name                VARCHAR(100) NOT NULL,
    code                VARCHAR(60),
    description         VARCHAR(255),
    sequence_num        INT           NOT NULL DEFAULT 0,
    enabled             CHAR(1)       NOT NULL DEFAULT 'Y',
    created_date        TIMESTAMP,
    last_modified_date  TIMESTAMP,
    CONSTRAINT fk_psvt_store FOREIGN KEY (product_store_id) REFERENCES product_store (product_store_id),
    CONSTRAINT chk_psvt_enabled CHECK (enabled IN ('Y', 'N')),
    CONSTRAINT uq_psvt_store_name UNIQUE (product_store_id, name)
);

CREATE INDEX idx_psvt_store ON product_store_variant_type (product_store_id);

CREATE TABLE IF NOT EXISTS product_store_variant_value (
    variant_value_id   VARCHAR(40)   NOT NULL PRIMARY KEY,
    variant_type_id    VARCHAR(40)   NOT NULL,
    product_store_id   VARCHAR(20)   NOT NULL,
    display_value     VARCHAR(100) NOT NULL,
    abbreviation       VARCHAR(20),
    sequence_num       INT           NOT NULL DEFAULT 0,
    enabled            CHAR(1)       NOT NULL DEFAULT 'Y',
    created_date       TIMESTAMP,
    last_modified_date TIMESTAMP,
    CONSTRAINT fk_psvv_type FOREIGN KEY (variant_type_id) REFERENCES product_store_variant_type (variant_type_id),
    CONSTRAINT fk_psvv_store FOREIGN KEY (product_store_id) REFERENCES product_store (product_store_id),
    CONSTRAINT chk_psvv_enabled CHECK (enabled IN ('Y', 'N')),
    CONSTRAINT uq_psvv_type_value UNIQUE (variant_type_id, display_value)
);

CREATE INDEX idx_psvv_type ON product_store_variant_value (variant_type_id);
CREATE INDEX idx_psvv_store ON product_store_variant_value (product_store_id);


-- ========== V17__product_variant_option.sql ==========

-- Product-level variant type/value assignment (virtual parent â†’ generate child SKUs)

CREATE TABLE IF NOT EXISTS product_variant_option (
    product_id       VARCHAR(20)  NOT NULL,
    variant_type_id  VARCHAR(40)  NOT NULL,
    product_store_id VARCHAR(20)  NOT NULL,
    sequence_num     INT           NOT NULL DEFAULT 0,
    created_date     TIMESTAMP,
    CONSTRAINT pk_pvo PRIMARY KEY (product_id, variant_type_id),
    CONSTRAINT fk_pvo_product FOREIGN KEY (product_id) REFERENCES product (product_id),
    CONSTRAINT fk_pvo_type FOREIGN KEY (variant_type_id) REFERENCES product_store_variant_type (variant_type_id),
    CONSTRAINT fk_pvo_store FOREIGN KEY (product_store_id) REFERENCES product_store (product_store_id)
);

CREATE INDEX idx_pvo_store ON product_variant_option (product_store_id);

CREATE TABLE IF NOT EXISTS product_variant_option_value (
    product_id        VARCHAR(20)  NOT NULL,
    variant_type_id   VARCHAR(40)  NOT NULL,
    variant_value_id  VARCHAR(40)  NOT NULL,
    CONSTRAINT pk_pvov PRIMARY KEY (product_id, variant_type_id, variant_value_id),
    CONSTRAINT fk_pvov_option FOREIGN KEY (product_id, variant_type_id)
        REFERENCES product_variant_option (product_id, variant_type_id),
    CONSTRAINT fk_pvov_value FOREIGN KEY (variant_value_id) REFERENCES product_store_variant_value (variant_value_id)
);

CREATE INDEX idx_pvov_type ON product_variant_option_value (variant_type_id);


SET FOREIGN_KEY_CHECKS = 1;
