package com.playpro.playpro.catalog.util;

import java.util.UUID;

public final class ProductIdGenerator {

    private ProductIdGenerator() {
    }

    public static String nextProductId() {
        return "PROD-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    public static String nextCategoryId() {
        return "CAT-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    public static String nextFeatureId() {
        return "FEAT-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    public static String nextProdCatalogId() {
        return "PCAT-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
