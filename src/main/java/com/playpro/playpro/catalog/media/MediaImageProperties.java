package com.playpro.playpro.catalog.media;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "catalog.media")
public class MediaImageProperties {

    private String publicBaseUrl = "http://localhost:8080";
    private String categoryStoragePath = "data/category-images";
    private String catalogStoragePath = "data/catalog-images";

    public String getPublicBaseUrl() {
        return publicBaseUrl;
    }

    public void setPublicBaseUrl(String publicBaseUrl) {
        this.publicBaseUrl = publicBaseUrl;
    }

    public String getCategoryStoragePath() {
        return categoryStoragePath;
    }

    public void setCategoryStoragePath(String categoryStoragePath) {
        this.categoryStoragePath = categoryStoragePath;
    }

    public String getCatalogStoragePath() {
        return catalogStoragePath;
    }

    public void setCatalogStoragePath(String catalogStoragePath) {
        this.catalogStoragePath = catalogStoragePath;
    }
}
