package com.playpro.playpro.catalog.productimage;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public enum ProductImageSize {
    SMALL("small", "smallImageUrl"),
    MEDIUM("medium", "mediumImageUrl"),
    LARGE("large", "largeImageUrl"),
    DETAIL("detail", "detailImageUrl");

    private final String pathSegment;
    private final String productField;

    ProductImageSize(String pathSegment, String productField) {
        this.pathSegment = pathSegment;
        this.productField = productField;
    }

    public String getPathSegment() {
        return pathSegment;
    }

    public String getProductField() {
        return productField;
    }

    public String getLabel() {
        return name().charAt(0) + name().substring(1).toLowerCase(Locale.ROOT);
    }

    public static Optional<ProductImageSize> fromPathSegment(String value) {
        if (value == null || value.trim().isEmpty()) {
            return Optional.empty();
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(size -> size.pathSegment.equals(normalized))
                .findFirst();
    }
}
