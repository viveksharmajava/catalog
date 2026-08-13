-- Configurable payment methods / gateways per product store

CREATE TABLE product_store_payment_method (
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
    extra_config        CLOB,
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
