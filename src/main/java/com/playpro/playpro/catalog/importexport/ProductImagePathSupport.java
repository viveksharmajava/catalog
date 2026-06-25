package com.playpro.playpro.catalog.importexport;

import java.util.Arrays;
import java.util.List;

/**
 * Relative image paths: {@code {product_id}/{image_type}/{file_name}}
 * e.g. {@code PROD-001/small/hero.jpg}
 */
public final class ProductImagePathSupport {

    private static final List<String> IMAGE_TYPES = Arrays.asList("small", "medium", "large", "original");

    private ProductImagePathSupport() {
    }

    public static String normalizeImportPath(String productId, String imageType, String path) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }
        String trimmed = path.trim().replace('\\', '/');
        while (trimmed.startsWith("/")) {
            trimmed = trimmed.substring(1);
        }
        if (trimmed.contains("://")) {
            return trimmed;
        }
        String[] parts = trimmed.split("/");
        if (parts.length >= 3 && IMAGE_TYPES.contains(parts[1].toLowerCase())) {
            return parts[0] + "/" + parts[1].toLowerCase() + "/" + parts[parts.length - 1];
        }
        if (parts.length == 2 && IMAGE_TYPES.contains(parts[0].toLowerCase())) {
            return productId + "/" + parts[0].toLowerCase() + "/" + parts[1];
        }
        if (parts.length == 1) {
            return productId + "/" + imageType.toLowerCase() + "/" + parts[0];
        }
        return productId + "/" + imageType.toLowerCase() + "/" + parts[parts.length - 1];
    }

    public static String toExportPath(String productId, String imageType, String storedValue) {
        if (storedValue == null || storedValue.trim().isEmpty()) {
            return null;
        }
        String trimmed = storedValue.trim().replace('\\', '/');
        if (trimmed.contains("://")) {
            return extractRelativeFromUrl(productId, imageType, trimmed);
        }
        while (trimmed.startsWith("/")) {
            trimmed = trimmed.substring(1);
        }
        if (trimmed.contains("/")) {
            return trimmed;
        }
        return productId + "/" + imageType.toLowerCase() + "/" + trimmed;
    }

    private static String extractRelativeFromUrl(String productId, String imageType, String url) {
        int catalogIdx = url.indexOf("/catalog/product-images/");
        if (catalogIdx >= 0) {
            String tail = url.substring(catalogIdx + "/catalog/product-images/".length());
            String[] parts = tail.split("/");
            if (parts.length >= 2) {
                return parts[0] + "/" + imageType.toLowerCase() + "/" + parts[parts.length - 1];
            }
        }
        int imagesIdx = url.lastIndexOf("/" + productId + "/");
        if (imagesIdx >= 0) {
            String tail = url.substring(imagesIdx + 1);
            return tail;
        }
        int slash = url.lastIndexOf('/');
        if (slash >= 0) {
            return productId + "/" + imageType.toLowerCase() + "/" + url.substring(slash + 1);
        }
        return productId + "/" + imageType.toLowerCase() + "/" + url;
    }
}
