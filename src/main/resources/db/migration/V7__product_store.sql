-- OFBiz ProductStore and ProductStoreCatalog (product-entitymodel.xml)

CREATE TABLE product_store (
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
    days_to_cancel_non_pay        NUMERIC(20, 0),
    retry_failed_auths            CHAR(1)      DEFAULT 'Y',
    allow_password                CHAR(1)      DEFAULT 'Y',
    default_password              VARCHAR(255),
    prod_search_exclude_variants  CHAR(1)      DEFAULT 'Y',
    show_prices_with_vat_tax      CHAR(1)      DEFAULT 'N'
);

CREATE TABLE product_store_catalog (
    product_store_id VARCHAR(20) NOT NULL,
    prod_catalog_id  VARCHAR(20) NOT NULL,
    from_date        TIMESTAMP   NOT NULL,
    thru_date        TIMESTAMP,
    sequence_num     NUMERIC(20, 0),
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
