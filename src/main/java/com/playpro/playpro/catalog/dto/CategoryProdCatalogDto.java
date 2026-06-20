package com.playpro.playpro.catalog.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CategoryProdCatalogDto {

    private String prodCatalogId;
    private String catalogName;
    private String productCategoryId;
    private String prodCatalogCategoryTypeId;
    private String prodCatalogCategoryTypeDescription;
    private LocalDateTime fromDate;
    private LocalDateTime thruDate;
    private BigDecimal sequenceNum;

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

    public String getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(String productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public String getProdCatalogCategoryTypeId() {
        return prodCatalogCategoryTypeId;
    }

    public void setProdCatalogCategoryTypeId(String prodCatalogCategoryTypeId) {
        this.prodCatalogCategoryTypeId = prodCatalogCategoryTypeId;
    }

    public String getProdCatalogCategoryTypeDescription() {
        return prodCatalogCategoryTypeDescription;
    }

    public void setProdCatalogCategoryTypeDescription(String prodCatalogCategoryTypeDescription) {
        this.prodCatalogCategoryTypeDescription = prodCatalogCategoryTypeDescription;
    }

    public LocalDateTime getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDateTime fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDateTime getThruDate() {
        return thruDate;
    }

    public void setThruDate(LocalDateTime thruDate) {
        this.thruDate = thruDate;
    }

    public BigDecimal getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(BigDecimal sequenceNum) {
        this.sequenceNum = sequenceNum;
    }
}
