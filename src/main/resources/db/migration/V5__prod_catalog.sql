-- OFBiz ProdCatalog entity (product-entitymodel.xml)

CREATE TABLE prod_catalog (
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
