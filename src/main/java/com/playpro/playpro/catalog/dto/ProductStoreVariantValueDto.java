package com.playpro.playpro.catalog.dto;

public class ProductStoreVariantValueDto {

    private String variantValueId;
    private String variantTypeId;
    private String productStoreId;
    private String value;
    private String abbreviation;
    private Integer sequenceNum;
    private boolean enabled;

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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
