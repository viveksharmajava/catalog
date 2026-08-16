package com.playpro.playpro.catalog.entity.store;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_store_variant_value")
public class ProductStoreVariantValue {

    @Id
    @Column(name = "variant_value_id", length = 40)
    private String variantValueId;

    @Column(name = "variant_type_id", length = 40, nullable = false)
    private String variantTypeId;

    @Column(name = "product_store_id", length = 20, nullable = false)
    private String productStoreId;

    @Column(name = "display_value", length = 100, nullable = false)
    private String value;

    @Column(name = "abbreviation", length = 20)
    private String abbreviation;

    @Column(name = "sequence_num", nullable = false)
    private Integer sequenceNum = 0;

    @Column(name = "enabled", length = 1, nullable = false)
    private String enabled = "Y";

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    public String getVariantValueId() {
        return variantValueId;
    }

    public void setVariantValueId(String variantValueId) {
        this.variantValueId = variantValueId;
    }

    public String getVariantTypeId() {
        return variantTypeId;
    }

    public void setVariantTypeId(String variantTypeId) {
        this.variantTypeId = variantTypeId;
    }

    public String getProductStoreId() {
        return productStoreId;
    }

    public void setProductStoreId(String productStoreId) {
        this.productStoreId = productStoreId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public Integer getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(Integer sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
