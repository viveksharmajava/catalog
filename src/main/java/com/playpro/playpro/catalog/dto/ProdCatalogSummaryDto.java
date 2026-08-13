package com.playpro.playpro.catalog.dto;

public class ProdCatalogSummaryDto {

    private String prodCatalogId;
    private String catalogName;
    private String useQuickAdd;
    private String headerLogo;
    private Boolean isCartEnabled;

    public String getProdCatalogId() {
        return prodCatalogId;
    }

    public void setProdCatalogId(String prodCatalogId) {
        this.prodCatalogId = prodCatalogId;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public String getUseQuickAdd() {
        return useQuickAdd;
    }

    public void setUseQuickAdd(String useQuickAdd) {
        this.useQuickAdd = useQuickAdd;
    }

    public String getHeaderLogo() {
        return headerLogo;
    }

    public void setHeaderLogo(String headerLogo) {
        this.headerLogo = headerLogo;
    }

    public Boolean getIsCartEnabled() {
        return isCartEnabled;
    }

    public void setIsCartEnabled(Boolean isCartEnabled) {
        this.isCartEnabled = isCartEnabled;
    }
}
