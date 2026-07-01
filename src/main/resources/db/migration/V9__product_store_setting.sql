-- Store-level customer-facing page links and default storefront flag

CREATE TABLE product_store_setting (
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
