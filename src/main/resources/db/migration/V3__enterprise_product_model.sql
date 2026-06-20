-- Enterprise product catalog schema (OFBiz product-entitymodel inspired)
-- Product-level entities only; pricing/inventory/promo belong in other services.

DROP TABLE IF EXISTS product_category_map;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS products;

-- Reference / type tables
CREATE TABLE product_type (
    product_type_id       VARCHAR(20)  NOT NULL PRIMARY KEY,
    parent_type_id        VARCHAR(20),
    is_physical           CHAR(1)      DEFAULT 'Y',
    is_digital            CHAR(1)      DEFAULT 'N',
    description           VARCHAR(255)
);

CREATE TABLE product_category_type (
    product_category_type_id VARCHAR(20) NOT NULL PRIMARY KEY,
    parent_type_id           VARCHAR(20),
    description              VARCHAR(255)
);

CREATE TABLE product_assoc_type (
    product_assoc_type_id VARCHAR(20) NOT NULL PRIMARY KEY,
    parent_type_id        VARCHAR(20),
    description           VARCHAR(255)
);

CREATE TABLE good_identification_type (
    good_identification_type_id VARCHAR(20) NOT NULL PRIMARY KEY,
    parent_type_id              VARCHAR(20),
    description                 VARCHAR(255)
);

CREATE TABLE product_feature_type (
    product_feature_type_id VARCHAR(20) NOT NULL PRIMARY KEY,
    parent_type_id          VARCHAR(20),
    description             VARCHAR(255)
);

CREATE TABLE product_feature_appl_type (
    product_feature_appl_type_id VARCHAR(20) NOT NULL PRIMARY KEY,
    parent_type_id               VARCHAR(20),
    description                  VARCHAR(255)
);

CREATE TABLE product_feature_category (
    product_feature_category_id VARCHAR(20) NOT NULL PRIMARY KEY,
    parent_category_id          VARCHAR(20),
    description                 VARCHAR(255)
);

-- Category hierarchy (OFBiz ProductCategory + ProductCategoryRollup)
CREATE TABLE product_category (
    product_category_id        VARCHAR(20)  NOT NULL PRIMARY KEY,
    product_category_type_id   VARCHAR(20)  NOT NULL,
    primary_parent_category_id VARCHAR(20),
    category_name              VARCHAR(100) NOT NULL,
    description                VARCHAR(255),
    long_description           CLOB,
    category_image_url         VARCHAR(2000),
    show_in_select             CHAR(1)      DEFAULT 'Y',
    created_by_user_login      VARCHAR(250),
    created_date               TIMESTAMP,
    last_modified_by_user_login VARCHAR(250),
    last_modified_date         TIMESTAMP,
    CONSTRAINT fk_pcat_type FOREIGN KEY (product_category_type_id) REFERENCES product_category_type(product_category_type_id)
);

CREATE TABLE product_category_rollup (
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
CREATE TABLE product (
    product_id                   VARCHAR(20)  NOT NULL PRIMARY KEY,
    product_type_id              VARCHAR(20)  NOT NULL,
    primary_product_category_id    VARCHAR(20),
    status_id                    VARCHAR(20)  DEFAULT 'ACTIVE',
    internal_name                VARCHAR(255),
    brand_name                   VARCHAR(100),
    product_name                 VARCHAR(100) NOT NULL,
    description                  VARCHAR(255),
    long_description             CLOB,
    comments                     CLOB,
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

CREATE TABLE product_category_member (
    product_category_id VARCHAR(20) NOT NULL,
    product_id          VARCHAR(20) NOT NULL,
    from_date           TIMESTAMP   NOT NULL,
    thru_date           TIMESTAMP,
    comments            CLOB,
    sequence_num        DECIMAL(20,0),
    quantity            DECIMAL(18,6),
    PRIMARY KEY (product_category_id, product_id, from_date),
    CONSTRAINT fk_pcm_cat FOREIGN KEY (product_category_id) REFERENCES product_category(product_category_id),
    CONSTRAINT fk_pcm_prod FOREIGN KEY (product_id) REFERENCES product(product_id)
);

CREATE INDEX idx_pcm_product ON product_category_member(product_id);
CREATE INDEX idx_pcm_category ON product_category_member(product_category_id);

CREATE TABLE product_assoc (
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

CREATE TABLE good_identification (
    good_identification_type_id VARCHAR(20) NOT NULL,
    product_id                  VARCHAR(20) NOT NULL,
    id_value                    VARCHAR(255) NOT NULL,
    PRIMARY KEY (good_identification_type_id, product_id),
    CONSTRAINT fk_gid_type FOREIGN KEY (good_identification_type_id) REFERENCES good_identification_type(good_identification_type_id),
    CONSTRAINT fk_gid_prod FOREIGN KEY (product_id) REFERENCES product(product_id)
);

CREATE INDEX idx_gid_value ON good_identification(id_value);

CREATE TABLE product_attribute (
    product_id       VARCHAR(20) NOT NULL,
    attr_name        VARCHAR(60) NOT NULL,
    attr_value       VARCHAR(255),
    attr_type        VARCHAR(255),
    attr_description VARCHAR(255),
    PRIMARY KEY (product_id, attr_name),
    CONSTRAINT fk_pattr_prod FOREIGN KEY (product_id) REFERENCES product(product_id)
);

CREATE TABLE product_feature (
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

CREATE TABLE product_feature_appl (
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

CREATE TABLE product_keyword (
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
