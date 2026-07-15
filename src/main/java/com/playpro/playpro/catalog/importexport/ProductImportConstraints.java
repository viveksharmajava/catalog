package com.playpro.playpro.catalog.importexport;

import com.playpro.playpro.catalog.dto.ProductDto;

public final class ProductImportConstraints {

    public static final int MAX_PRODUCT_ID_LENGTH = 64;
    public static final int MAX_PRODUCT_NAME_LENGTH = 100;
    public static final int MAX_KEYWORD_LENGTH = 60;

    private ProductImportConstraints() {
    }

    public static void validate(ProductDto product) {
        if (product == null) {
            throw new IllegalArgumentException("product row is empty");
        }
        if (product.getProductId() != null && product.getProductId().trim().length() > MAX_PRODUCT_ID_LENGTH) {
            throw new IllegalArgumentException(
                    "product_id exceeds max length " + MAX_PRODUCT_ID_LENGTH + ": " + product.getProductId());
        }
        if (product.getProductName() != null && product.getProductName().trim().length() > MAX_PRODUCT_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    "product_name exceeds max length " + MAX_PRODUCT_NAME_LENGTH + ": " + product.getProductName());
        }
    }

    public static void validateKeyword(String keyword) {
        if (keyword != null && keyword.trim().length() > MAX_KEYWORD_LENGTH) {
            throw new IllegalArgumentException(
                    "keyword exceeds max length " + MAX_KEYWORD_LENGTH + ": " + keyword);
        }
    }
}
