-- Product-level variant type/value assignment (virtual parent → generate child SKUs)

CREATE TABLE product_variant_option (
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

CREATE TABLE product_variant_option_value (
    product_id        VARCHAR(20)  NOT NULL,
    variant_type_id   VARCHAR(40)  NOT NULL,
    variant_value_id  VARCHAR(40)  NOT NULL,
    CONSTRAINT pk_pvov PRIMARY KEY (product_id, variant_type_id, variant_value_id),
    CONSTRAINT fk_pvov_option FOREIGN KEY (product_id, variant_type_id)
        REFERENCES product_variant_option (product_id, variant_type_id),
    CONSTRAINT fk_pvov_value FOREIGN KEY (variant_value_id) REFERENCES product_store_variant_value (variant_value_id)
);

CREATE INDEX idx_pvov_type ON product_variant_option_value (variant_type_id);
