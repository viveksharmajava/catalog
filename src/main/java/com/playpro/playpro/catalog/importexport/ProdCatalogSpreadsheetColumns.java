package com.playpro.playpro.catalog.importexport;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class ProdCatalogSpreadsheetColumns {

    public static final String CATALOG_SHEET = "Catalogs";
    public static final String CATEGORY_LINK_SHEET = "CatalogCategories";

    private static final List<String> CATALOG_HEADERS = Collections.unmodifiableList(Arrays.asList(
            "prod_catalog_id",
            "catalog_name",
            "use_quick_add",
            "style_sheet",
            "header_logo",
            "content_path_prefix",
            "template_path_prefix",
            "view_allow_perm_reqd",
            "purchase_allow_perm_reqd",
            "is_cart_enabled"
    ));

    private static final List<String> CATEGORY_LINK_HEADERS = Collections.unmodifiableList(Arrays.asList(
            "prod_catalog_id",
            "product_category_id",
            "prod_catalog_category_type_id",
            "sequence_num",
            "from_date",
            "thru_date"
    ));

    private ProdCatalogSpreadsheetColumns() {
    }

    public static List<String> catalogHeaders() {
        return CATALOG_HEADERS;
    }

    public static List<String> categoryLinkHeaders() {
        return CATEGORY_LINK_HEADERS;
    }
}
