package com.playpro.playpro.catalog.entity.product;

import com.playpro.playpro.catalog.entity.AuditableEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product")
public class Product extends AuditableEntity {

    @Id
    @Column(name = "product_id", length = 20)
    private String productId;

    @Column(name = "product_type_id", length = 20, nullable = false)
    private String productTypeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_type_id", insertable = false, updatable = false)
    private ProductType productType;

    @Column(name = "primary_product_category_id", length = 20)
    private String primaryProductCategoryId;

    @Column(name = "status_id", length = 20)
    private String statusId = "DRAFT";

    @Column(name = "internal_name", length = 255)
    private String internalName;

    @Column(name = "brand_name", length = 100)
    private String brandName;

    @Column(name = "product_name", length = 100, nullable = false)
    private String productName;

    @Column(length = 255)
    private String description;

    @Column(name = "long_description", columnDefinition = "CLOB")
    private String longDescription;

    @Column(columnDefinition = "CLOB")
    private String comments;

    @Column(name = "small_image_url", length = 2000)
    private String smallImageUrl;

    @Column(name = "medium_image_url", length = 2000)
    private String mediumImageUrl;

    @Column(name = "large_image_url", length = 2000)
    private String largeImageUrl;

    @Column(name = "detail_image_url", length = 2000)
    private String detailImageUrl;

    @Column(name = "introduction_date")
    private LocalDateTime introductionDate;

    @Column(name = "release_date")
    private LocalDateTime releaseDate;

    @Column(name = "sales_discontinuation_date")
    private LocalDateTime salesDiscontinuationDate;

    @Column(name = "is_virtual", length = 1)
    private String isVirtual = "N";

    @Column(name = "is_variant", length = 1)
    private String isVariant = "N";

    @Column(length = 1)
    private String returnable = "Y";

    @Column(length = 1)
    private String taxable = "Y";

    @Column(name = "charge_shipping", length = 1)
    private String chargeShipping = "Y";

    @Column(name = "require_inventory", length = 1)
    private String requireInventory = "Y";

    @Column(name = "shipping_weight", precision = 18, scale = 6)
    private BigDecimal shippingWeight;

    @Column(name = "product_weight", precision = 18, scale = 6)
    private BigDecimal productWeight;

    @Column(name = "product_height", precision = 18, scale = 6)
    private BigDecimal productHeight;

    @Column(name = "product_width", precision = 18, scale = 6)
    private BigDecimal productWidth;

    @Column(name = "product_depth", precision = 18, scale = 6)
    private BigDecimal productDepth;

    @Version
    private Long version;

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

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
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

    public String getIsVirtual() {
        return isVirtual;
    }

    public void setIsVirtual(String isVirtual) {
        this.isVirtual = isVirtual;
    }

    public String getIsVariant() {
        return isVariant;
    }

    public void setIsVariant(String isVariant) {
        this.isVariant = isVariant;
    }

    public String getReturnable() {
        return returnable;
    }

    public void setReturnable(String returnable) {
        this.returnable = returnable;
    }

    public String getTaxable() {
        return taxable;
    }

    public void setTaxable(String taxable) {
        this.taxable = taxable;
    }

    public String getChargeShipping() {
        return chargeShipping;
    }

    public void setChargeShipping(String chargeShipping) {
        this.chargeShipping = chargeShipping;
    }

    public String getRequireInventory() {
        return requireInventory;
    }

    public void setRequireInventory(String requireInventory) {
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
}
