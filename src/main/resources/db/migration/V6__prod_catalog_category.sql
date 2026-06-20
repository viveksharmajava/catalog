-- OFBiz ProdCatalogCategory + ProdCatalogCategoryType

CREATE TABLE prod_catalog_category_type (
    prod_catalog_category_type_id VARCHAR(20) NOT NULL PRIMARY KEY,
    parent_type_id                VARCHAR(20),
    description                   VARCHAR(255)
);

CREATE TABLE prod_catalog_category (
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
