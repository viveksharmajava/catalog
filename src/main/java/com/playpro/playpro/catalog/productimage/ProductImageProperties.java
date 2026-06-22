package com.playpro.playpro.catalog.productimage;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "catalog.product-images")
public class ProductImageProperties {

    /**
     * Root directory for stored product image files (relative to working directory if not absolute).
     */
    private String storagePath = "data/product-images";

    /**
     * Public base URL returned in product image fields for other services (no trailing slash).
     */
    private String publicBaseUrl = "http://localhost:8080";

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getPublicBaseUrl() {
        return publicBaseUrl;
    }

    public void setPublicBaseUrl(String publicBaseUrl) {
        this.publicBaseUrl = publicBaseUrl;
    }
}
