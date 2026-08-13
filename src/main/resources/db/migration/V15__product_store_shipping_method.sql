-- Configurable shipping / carrier integrations per product store

CREATE TABLE product_store_shipping_method (
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
    extra_config        CLOB,
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
