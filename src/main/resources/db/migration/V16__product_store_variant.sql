-- Store-scoped product variant types (e.g. Shoe Size, Color) and their values

CREATE TABLE product_store_variant_type (
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

CREATE TABLE product_store_variant_value (
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
