package com.playpro.playpro.catalog.dto;

import java.time.LocalDateTime;

public class ProductStoreSummaryDto {

    private String productStoreId;
    private String storeName;
    private String title;
    private String subtitle;
    private String companyName;
    private String isDemoStore;

    public String getProductStoreId() {
        return productStoreId;
    }

    public void setProductStoreId(String productStoreId) {
        this.productStoreId = productStoreId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getIsDemoStore() {
        return isDemoStore;
    }

    public void setIsDemoStore(String isDemoStore) {
        this.isDemoStore = isDemoStore;
    }
}
