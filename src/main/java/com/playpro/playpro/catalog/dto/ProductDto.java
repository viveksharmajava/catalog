package com.playpro.playpro.catalog.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProductDto {

    private String productId;
    private String productTypeId;
    private String primaryProductCategoryId;
    private String statusId;
    private String internalName;
    private String brandName;
    private String productName;
    private String description;
    private String longDescription;
    private String comments;
    private String smallImageUrl;
    private String mediumImageUrl;
    private String largeImageUrl;
    private String detailImageUrl;
    private LocalDateTime introductionDate;
    private LocalDateTime releaseDate;
    private LocalDateTime salesDiscontinuationDate;
    private Boolean virtualProduct;
    private Boolean variant;
    private Boolean returnable;
    private Boolean taxable;
    private Boolean chargeShipping;
    private Boolean requireInventory;
    private BigDecimal shippingWeight;
    private BigDecimal productWeight;
    private BigDecimal productHeight;
    private BigDecimal productWidth;
    private BigDecimal productDepth;
    private Long version;
    private String sku;
    private List<String> keywords = new ArrayList<>();
    private List<ProductAttributeDto> attributes = new ArrayList<>();
    private Boolean availableForSale;
    private Boolean shippingApplies;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductTypeId() {
        return productTypeId;
    }

    public void setProductTypeId(String productTypeId) {
        this.productTypeId = productTypeId;
    }

    public String getPrimaryProductCategoryId() {
        return primaryProductCategoryId;
    }

    public void setPrimaryProductCategoryId(String primaryProductCategoryId) {
        this.primaryProductCategoryId = primaryProductCategoryId;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getInternalName() {
        return internalName;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getSmallImageUrl() {
        return smallImageUrl;
    }

    public void setSmallImageUrl(String smallImageUrl) {
        this.smallImageUrl = smallImageUrl;
    }

    public String getMediumImageUrl() {
        return mediumImageUrl;
    }

    public void setMediumImageUrl(String mediumImageUrl) {
        this.mediumImageUrl = mediumImageUrl;
    }

    public String getLargeImageUrl() {
        return largeImageUrl;
    }

    public void setLargeImageUrl(String largeImageUrl) {
        this.largeImageUrl = largeImageUrl;
    }

    public String getDetailImageUrl() {
        return detailImageUrl;
    }

    public void setDetailImageUrl(String detailImageUrl) {
        this.detailImageUrl = detailImageUrl;
    }

    public LocalDateTime getIntroductionDate() {
        return introductionDate;
    }

    public void setIntroductionDate(LocalDateTime introductionDate) {
        this.introductionDate = introductionDate;
    }

    public LocalDateTime getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDateTime releaseDate) {
        this.releaseDate = releaseDate;
    }

    public LocalDateTime getSalesDiscontinuationDate() {
        return salesDiscontinuationDate;
    }

    public void setSalesDiscontinuationDate(LocalDateTime salesDiscontinuationDate) {
        this.salesDiscontinuationDate = salesDiscontinuationDate;
    }

    public Boolean getVirtualProduct() {
        return virtualProduct;
    }

    public void setVirtualProduct(Boolean virtualProduct) {
        this.virtualProduct = virtualProduct;
    }

    public Boolean getVariant() {
        return variant;
    }

    public void setVariant(Boolean variant) {
        this.variant = variant;
    }

    public Boolean getReturnable() {
        return returnable;
    }

    public void setReturnable(Boolean returnable) {
        this.returnable = returnable;
    }

    public Boolean getTaxable() {
        return taxable;
    }

    public void setTaxable(Boolean taxable) {
        this.taxable = taxable;
    }

    public Boolean getChargeShipping() {
        return chargeShipping;
    }

    public void setChargeShipping(Boolean chargeShipping) {
        this.chargeShipping = chargeShipping;
    }

    public Boolean getRequireInventory() {
        return requireInventory;
    }

    public void setRequireInventory(Boolean requireInventory) {
        this.requireInventory = requireInventory;
    }

    public BigDecimal getShippingWeight() {
        return shippingWeight;
    }

    public void setShippingWeight(BigDecimal shippingWeight) {
        this.shippingWeight = shippingWeight;
    }

    public BigDecimal getProductWeight() {
        return productWeight;
    }

    public void setProductWeight(BigDecimal productWeight) {
        this.productWeight = productWeight;
    }

    public BigDecimal getProductHeight() {
        return productHeight;
    }

    public void setProductHeight(BigDecimal productHeight) {
        this.productHeight = productHeight;
    }

    public BigDecimal getProductWidth() {
        return productWidth;
    }

    public void setProductWidth(BigDecimal productWidth) {
        this.productWidth = productWidth;
    }

    public BigDecimal getProductDepth() {
        return productDepth;
    }

    public void setProductDepth(BigDecimal productDepth) {
        this.productDepth = productDepth;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<ProductAttributeDto> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<ProductAttributeDto> attributes) {
        this.attributes = attributes;
    }

    public Boolean getAvailableForSale() {
        return availableForSale;
    }

    public void setAvailableForSale(Boolean availableForSale) {
        this.availableForSale = availableForSale;
    }

    public Boolean getShippingApplies() {
        return shippingApplies;
    }

    public void setShippingApplies(Boolean shippingApplies) {
        this.shippingApplies = shippingApplies;
    }
}
